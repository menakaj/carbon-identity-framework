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
package org.wso2.carbon.identity.retryable.provisioning.endpoint.impl.utils;

import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * Utility class for Endpoint test cases.
 */
public class TestUtils {

    private static final String CAUSE = "Created";
    private static final String OPERATION = "POST";
    private static final int IDENTIFIER = 1;
    private static final String ENTITY = "User";
    private static final String IDP_NAME = "test_idp";
    private static final String STATUS = "SUCCESS";
    private static final String PAYLOAD = "payload";
    private static final String CONFIGS = "config";
    private static final String STATUS_CODE = "status_code";
    private static final String CAUSE_TEXT = "cause";
    private static final String CONNECTOR_NAME = "connector";
    private static final String ENTITY_NAME = "test";

    public static ProvisioningStatus generateProvisioningStatus() {
        ProvisioningStatus provisioningStatus = new ProvisioningStatus();
        provisioningStatus.setCause(CAUSE);
        provisioningStatus.setOperation(OPERATION);
        provisioningStatus.setIdentifier(IDENTIFIER);
        provisioningStatus.setIdpName(IDP_NAME);
        provisioningStatus.setEntity(ENTITY);
        provisioningStatus.setName(ENTITY_NAME);
        provisioningStatus.setStatus(STATUS);
        provisioningStatus.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        return provisioningStatus;
    }

}
