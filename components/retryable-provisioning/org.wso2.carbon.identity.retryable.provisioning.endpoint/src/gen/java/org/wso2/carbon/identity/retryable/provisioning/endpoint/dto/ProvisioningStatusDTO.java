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
package org.wso2.carbon.identity.retryable.provisioning.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "")
public class ProvisioningStatusDTO {

    private Integer identifier = null;

    private Integer tenantId = null;

    private String status = null;

    private String cause = null;

    private String entity = null;

    private String name = null;

    private String operation = null;

    private String idpName = null;


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("identifier")
    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("tenantId")
    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("cause")
    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("entity")
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("operation")
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("idpName")
    public String getIdpName() {
        return idpName;
    }

    public void setIdpName(String idpName) {
        this.idpName = idpName;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ProvisioningStatusDTO {\n");

        sb.append("  identifier: ").append(identifier).append("\n");
        sb.append("  tenantId: ").append(tenantId).append("\n");
        sb.append("  status: ").append(status).append("\n");
        sb.append("  cause: ").append(cause).append("\n");
        sb.append("  entity: ").append(entity).append("\n");
        sb.append("  name: ").append(name).append("\n");
        sb.append("  operation: ").append(operation).append("\n");
        sb.append("  idpName: ").append(idpName).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
