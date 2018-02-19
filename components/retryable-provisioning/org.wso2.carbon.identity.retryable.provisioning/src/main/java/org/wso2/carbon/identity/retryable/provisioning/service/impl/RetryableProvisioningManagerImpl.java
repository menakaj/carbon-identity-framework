/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.retryable.provisioning.service.impl;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.provisioning.OutboundProvisioningManager;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;
import org.wso2.carbon.identity.retryable.provisioning.beans.FilterConfig;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningMetadata;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.constants.RetryableProvisioningConstants;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningMetadataDAO;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningStatusDAO;
import org.wso2.carbon.identity.retryable.provisioning.dao.impl.ProvisioningMetadataDAOImpl;
import org.wso2.carbon.identity.retryable.provisioning.dao.impl.ProvisioningStatusDAOImpl;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.util.ProvisioningEntityToJsonConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * OSGI Service class for Retryable provisioning. This class contains the core functionality of persisting,
 * retrieving and deleting provisioning event information and retrying the specified provisions.
 */
public class RetryableProvisioningManagerImpl implements RetryableProvisioningManager {

    private static final Log log = LogFactory.getLog(RetryableProvisioningManagerImpl.class);
    private ProvisioningStatusDAO provisioningStatusDAO = new ProvisioningStatusDAOImpl();
    private ProvisioningMetadataDAO provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
    private ProvisioningEntityToJsonConverter jsonConverter = new ProvisioningEntityToJsonConverter();
    private Gson gson = new Gson();
    private int tenantId = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantId();

    @Override
    public void processEvent(Map<String, Object> eventProperties) {

        if (log.isDebugEnabled()) {
            log.debug("Converting event properties to respective object formats.");
        }
        //Get the Connector Configuration properties.
        Properties properties = (Properties) eventProperties.get(RetryableProvisioningConstants.CONFIGS);

        //Get the Provisioning Entity.
        ProvisioningEntity provisioningEntity = (ProvisioningEntity) eventProperties.get
                (RetryableProvisioningConstants.PROV_ENTITY);

        // Get the status code and the cause string.
        Integer statusCode = (Integer) eventProperties.get(RetryableProvisioningConstants.STATUS_CODE);
        String cause = (String) eventProperties.get(RetryableProvisioningConstants.CAUSE_TEXT);

        //Get the outbound provisioning connector name
        String connector = (String) eventProperties.get(RetryableProvisioningConstants.CONNECTOR);

        ProvisioningMetadata provisioningMetadata = generateProvisioningMetadata(provisioningEntity, properties,
                tenantId);
        ProvisioningStatus provisioningStatus = generateProvisioningStatus(provisioningEntity, statusCode, cause,
                properties, connector, tenantId);

        //Persist the generated provisioning status and metadata objects.
        if (log.isDebugEnabled()) {
            log.debug("Persisting provisioning status and provisioning metadata.");
        }

        try {
            //Get the existing provisioning status for the given status information.
            Integer existingId = provisioningStatusDAO.getProvisioningStatusByName(provisioningStatus.getName());

            if (existingId != -1) {
                //Delete the existing item
                List<Integer> statusIdsToDelete = new ArrayList<>();
                statusIdsToDelete.add(existingId);
                provisioningStatusDAO.deleteProvisioningStatus(statusIdsToDelete);
            }

            // ProvisioningMetadata is mapped with the ProvisioningStatus with 1 to 1 constraint. For this we get the
            // status id and set it in provisioning metadata.
            Integer statusId = provisioningStatusDAO.addProvisioningStatus(provisioningStatus);
            provisioningMetadata.setStatusId(statusId);
            provisioningMetadataDAO.addProvisioningMetadata(provisioningMetadata);
        } catch (RetryableProvisioningException e) {
            log.error("Error while persisting Provisioning information. ", e);
        }
    }

    @Override
    public List<ProvisioningStatus> getProvisioningStatus(String idp, String status)
            throws RetryableProvisioningException {

        //Create a filter config based on the parameters.
        FilterConfig filterConfig = new FilterConfig();
        filterConfig.setIdp(idp);
        filterConfig.setStatus(status);
        return provisioningStatusDAO.getProvisioningStatus(filterConfig, tenantId);
    }

    @Override
    public boolean deleteStatusEntry(List<Integer> statusIds) throws RetryableProvisioningException {
        return provisioningStatusDAO.deleteProvisioningStatus(statusIds);
    }

    @Override
    public boolean retryProvisioning(List<Integer> statusIds) throws RetryableProvisioningException {
        List<ProvisioningMetadata> metadataList;
        List<Integer> provisioningIDsToDelete = new ArrayList<>();

        metadataList = provisioningMetadataDAO.getProvisioningMetadata(statusIds);
        for (ProvisioningMetadata provisioningMetadata : metadataList) {
            ProvisioningEntity provisioningEntity = jsonConverter.convertFromJson(provisioningMetadata
                    .getProvisioningEntity());
            String idpName = gson.fromJson(provisioningMetadata.getConnectorConfiguration(), Properties.class)
                    .getProperty(RetryableProvisioningConstants.IDP_NAME);

            OutboundProvisioningManager.getInstance().provisionToIDP(provisioningEntity, idpName,
                    CarbonContext.getThreadLocalCarbonContext().getTenantDomain());

            provisioningIDsToDelete.add(provisioningMetadata.getStatusId());
        }

        //The retry process should be immutable. So we remove the re-attempted provisions.
        return provisioningStatusDAO.deleteProvisioningStatus(provisioningIDsToDelete);
    }

    /**
     * Create Provisioning metadata object from the provisioning entity and configs.
     *
     * @param provisioningEntity : The ProvisioningEntity object that needs to be persisted.
     * @param config             : The connector configurations.
     * @return new ProvisioningMetadata object.
     */
    private ProvisioningMetadata generateProvisioningMetadata(ProvisioningEntity provisioningEntity, Properties
            config, int tenantId) {
        String provisioningEntityString = jsonConverter.convertToJson(provisioningEntity);
        ProvisioningMetadata provisioningMetadata = new ProvisioningMetadata();
        provisioningMetadata.setTenantId(tenantId);
        provisioningMetadata.setProvisioningEntity(provisioningEntityString);
        provisioningMetadata.setConnectorConfiguration(gson.toJson(config));
        provisioningMetadata.setIdpName(config.getProperty(RetryableProvisioningConstants.IDP_NAME));
        return provisioningMetadata;
    }

    /**
     * Generate a ProvisioningStatus object.
     *
     * @param provisioningEntity : The provisioning entity object.
     * @param statusCode         : The status code which is returned from the outbound idp.
     * @param cause              : The cause string.
     * @param config             : The connector configurations.
     * @param connector          : The name of the connector.
     * @return A new ProvisioningStatus object.
     */
    private ProvisioningStatus generateProvisioningStatus(ProvisioningEntity provisioningEntity, int statusCode,
                                                          String cause, Properties config, String connector,
                                                          int tenantId) {
        ProvisioningStatus provisioningStatus = new ProvisioningStatus();
        provisioningStatus.setIdpName(config.getProperty(RetryableProvisioningConstants.IDP_NAME));

        //Check whether the status code is 200, 201 or 202
        if (HttpStatus.SC_OK != statusCode && HttpStatus.SC_CREATED != statusCode &&
                HttpStatus.SC_NO_CONTENT != statusCode) {
            provisioningStatus.setStatus(RetryableProvisioningConstants.FAILED);
        } else {
            provisioningStatus.setStatus(RetryableProvisioningConstants.SUCCESS);
        }
        provisioningStatus.setTenantId(tenantId);
        provisioningStatus.setOperation(provisioningEntity.getOperation().name());
        provisioningStatus.setCause(cause);
        provisioningStatus.setName(provisioningEntity.getEntityName());
        provisioningStatus.setEntity(provisioningEntity.getEntityType().toString());
        provisioningStatus.setConnector(connector);
        return provisioningStatus;
    }
}
