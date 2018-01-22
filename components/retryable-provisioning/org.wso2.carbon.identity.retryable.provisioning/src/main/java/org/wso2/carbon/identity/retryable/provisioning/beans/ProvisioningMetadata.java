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
package org.wso2.carbon.identity.retryable.provisioning.beans;

/**
 * Class to hold the metadata related to Outbound Provisioning.
 * 1. IDP name
 * 2. Connector name
 * 3. Connector configurations.
 * 4. Provisioning entity information.
 */
public class ProvisioningMetadata {

    private Integer statusId;
    private String idpName;
    private Integer tenantId;
    private String connectorConfiguration;
    private String provisioningEntity;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getIdpName() {
        return idpName;
    }

    public void setIdpName(String idpName) {
        this.idpName = idpName;
    }

    public String getConnectorConfiguration() {
        return connectorConfiguration;
    }

    public void setConnectorConfiguration(String connectorConfiguration) {
        this.connectorConfiguration = connectorConfiguration;
    }

    public String getProvisioningEntity() {
        return provisioningEntity;
    }

    public void setProvisioningEntity(String provisioningEntity) {
        this.provisioningEntity = provisioningEntity;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }
}
