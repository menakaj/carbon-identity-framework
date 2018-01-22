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

import org.wso2.carbon.identity.provisioning.ProvisionedIdentifier;
import org.wso2.carbon.identity.provisioning.ProvisioningEntityType;
import org.wso2.carbon.identity.provisioning.ProvisioningOperation;

import java.io.Serializable;

/**
 * A temporary class which has simple attributes. This class is a representation of ProvisioningEntity
 * class which has complex object types.
 *
 * The main purpose of this class is storage. The original ProvisioningEntity class could not be converted back in to
 * it's original state after converting it to a json string due to the complex types. So, with this class, it can be
 * done while preserving the original class structure, which helps when building the original class.
 */
public class SerializableProvisioningEntity implements Serializable {
    private ProvisioningEntityType entityType;
    private ProvisioningOperation operation;
    private ProvisionedIdentifier identifier;
    private String entityName;
    private boolean jitProvisioning;
    private String attributes;
    private String inboundAttributes;

    public ProvisioningEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(ProvisioningEntityType entityType) {
        this.entityType = entityType;
    }

    public ProvisioningOperation getOperation() {
        return operation;
    }

    public void setOperation(ProvisioningOperation operation) {
        this.operation = operation;
    }

    public ProvisionedIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(ProvisionedIdentifier identifier) {
        this.identifier = identifier;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public boolean isJitProvisioning() {
        return jitProvisioning;
    }

    public void setJitProvisioning(boolean jitProvisioning) {
        this.jitProvisioning = jitProvisioning;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getInboundAttributes() {
        return inboundAttributes;
    }

    public void setInboundAttributes(String inboundAttributes) {
        this.inboundAttributes = inboundAttributes;
    }
}

