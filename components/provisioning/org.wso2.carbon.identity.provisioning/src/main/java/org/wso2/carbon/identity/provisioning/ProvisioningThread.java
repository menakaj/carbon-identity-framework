/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.provisioning;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.application.common.IdentityApplicationManagementException;
import org.wso2.carbon.identity.provisioning.dao.CacheBackedProvisioningMgtDAO;
import org.wso2.carbon.idp.mgt.util.IdPManagementUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.UUID;
import java.util.concurrent.Callable;

public class ProvisioningThread implements Callable<Boolean> {

    private ProvisioningEntity provisioningEntity;
    private String tenantDomainName;
    private AbstractOutboundProvisioningConnector connector;
    private String connectorType;
    private String idPName;
    private CacheBackedProvisioningMgtDAO dao;
    private static final Log log = LogFactory.getLog(ProvisioningThread.class);

    public ProvisioningThread(ProvisioningEntity provisioningEntity, String tenantDomainName,
                              AbstractOutboundProvisioningConnector connector, String connectorType, String idPName,
                              CacheBackedProvisioningMgtDAO dao) {
        super();
        this.provisioningEntity = provisioningEntity;
        this.tenantDomainName = tenantDomainName;
        this.connector = connector;
        this.connectorType = connectorType;
        this.idPName = idPName;
        this.dao = dao;
    }

    @Override
    public Boolean call() throws IdentityProvisioningException {

        boolean success = false;
        String tenantDomainName = this.tenantDomainName;

        try {

            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(tenantDomainName);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(getTenantIdFromDomain(tenantDomainName));

            ProvisionedIdentifier provisionedIdentifier = null;
            // real provisioning happens now.
            provisionedIdentifier = connector.provision(provisioningEntity);

            int tenantId = IdPManagementUtil.getTenantIdOfDomain(tenantDomainName);

            if (provisioningEntity.getOperation() == ProvisioningOperation.DELETE) {

                //Check whether the entry exists before delete.
                // This check is required when retrying the provisioning. At the first attempt of deleting an entity,
                // the cached entry will be removed. Then, when re-trying, it will again try to remove the same
                // record and throw an error. To avoid this we check whether the provisioning identifier exists
                // before deleting.
                if (dao.getProvisionedIdentifier(idPName, connectorType, provisioningEntity, tenantId,
                        tenantDomainName) != null) {
                    deleteProvisionedEntityIdentifier(idPName, connectorType, provisioningEntity,
                            tenantDomainName);
                }

            } else if (provisioningEntity.getOperation() == ProvisioningOperation.POST) {

                if (provisionedIdentifier == null || provisionedIdentifier.getIdentifier() == null) {
                    provisionedIdentifier = new ProvisionedIdentifier();
                    provisionedIdentifier.setIdentifier(UUID.randomUUID().toString());
                }

                provisioningEntity.setIdentifier(provisionedIdentifier);

                // Check for the existing provisioning identifier.
                // The identifier will be persisted in the time of provisioning. So, when re-attempting it will try
                // to persist the same payload which will throw an Unique indexing or Primary key violation exception.
                if (dao.getProvisionedIdentifier(idPName, connectorType, provisioningEntity, tenantId,
                        tenantDomainName) == null) {
                    // store provisioned identifier for future reference.
                    storeProvisionedEntityIdentifier(idPName, connectorType, provisioningEntity,
                            tenantDomainName);
                }

            } else if (provisioningEntity.getEntityType() == ProvisioningEntityType.GROUP &&
                       provisioningEntity.getOperation() == ProvisioningOperation.PUT) {

                String newGroupName = ProvisioningUtil.getAttributeValue(provisioningEntity,
                                                                IdentityProvisioningConstants.NEW_GROUP_NAME_CLAIM_URI);
                if(newGroupName != null){
                    // update provisioned entity name for future reference. this is applicable for only
                    // group name update
                    dao.updateProvisionedEntityName(provisioningEntity);
                }
            }
            success = true;
        } catch (Exception e) {
            String errMsg = " Provisioning for Entity " + provisioningEntity.getEntityName() +
                    " For operation = " + provisioningEntity.getOperation();
            log.error(errMsg, e);
            throw new IdentityProvisioningException(errMsg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();

            if (tenantDomainName != null) {
                PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(
                        tenantDomainName);
                PrivilegedCarbonContext.getThreadLocalCarbonContext()
                                       .setTenantId(getTenantIdFromDomain(tenantDomainName));
            }
        }

        return success;
    }

    /**
     * @param idpName
     * @param connectorType
     * @param provisioningEntity
     * @param tenantDomain
     * @throws IdentityApplicationManagementException
     */
    private void storeProvisionedEntityIdentifier(String idpName, String connectorType,
                                                  ProvisioningEntity provisioningEntity, String tenantDomain)
            throws IdentityApplicationManagementException {
        int tenantId;
        try {
            tenantId = IdPManagementUtil.getTenantIdOfDomain(tenantDomain);
            dao.addProvisioningEntity(idpName, connectorType, provisioningEntity, tenantId, tenantDomain);
        } catch (UserStoreException e) {
            throw new IdentityApplicationManagementException(
                    "Error while storing provisioning identifier.", e);
        }
    }

    /**
     * @param idpName
     * @param connectorType
     * @param provisioningEntity
     * @param tenantDomain
     * @return
     * @throws IdentityApplicationManagementException
     */
    private void deleteProvisionedEntityIdentifier(String idpName, String connectorType,
                                                   ProvisioningEntity provisioningEntity, String tenantDomain)
            throws IdentityApplicationManagementException {
        int tenantId;
        try {
            tenantId = IdPManagementUtil.getTenantIdOfDomain(tenantDomain);
            dao.deleteProvisioningEntity(idpName, connectorType, provisioningEntity, tenantId, tenantDomain);
        } catch (UserStoreException e) {
            throw new IdentityApplicationManagementException(
                    "Error while deleting provisioning identifier.", e);
        }
    }

    private int getTenantIdFromDomain(String tenantDomainName) throws IdentityProvisioningException {

        if (StringUtils.isBlank(tenantDomainName)) {
            throw new IdentityProvisioningException("Provided tenant domain is invalid");
        }

        try {
            return IdPManagementUtil.getTenantIdOfDomain(tenantDomainName);
        } catch (UserStoreException e) {
            throw new IdentityProvisioningException(
                    "Error occurred while resolving tenant Id from tenant domain :" + tenantDomainName, e);
        }
    }

}
