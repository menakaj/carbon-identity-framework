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
package org.wso2.carbon.identity.retryable.provisioning.constants;

/**
 * Constants for Retryable provisioning.
 */
public class RetryableProvisioningConstants {

    public static final String CONFIGS = "config";
    public static final String CREATE = "Create";
    public static final String DELETE = "Delete";
    public static final String PROV_ENTITY = "payload";
    public static final String UPDATE = "Update";
    public static final String IDP_NAME = "identityProviderName";
    public static final String STATUS_CODE = "status_code";
    public static final String CAUSE_TEXT = "cause";
    public static final String REPLACEABLE_CHAR = "#";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILED = "FAILED";

    public class DAOConstants {

        //Provisioning status queries.
        public static final String GET_PROVISIONING_STATUS = "SELECT * FROM IDN_PROVISIONING_STATUS WHERE " +
                "TENANT_ID = ? AND STATUS LIKE ? AND ENTITY LIKE ? AND IDP_NAME LIKE ?";
        public static final String ADD_PROVISIONING_STATUS = "INSERT INTO IDN_PROVISIONING_STATUS (TENANT_ID, " +
                "IDP_NAME, STATUS, ENTITY, OPERATION, CAUSE) VALUES (?, ?, ?, ?, ?, ?)";
        public static final String DELETE_PROVISIONING_STATUS = "DELETE FROM IDN_PROVISIONING_STATUS WHERE STATUS_ID " +
                "IN (#)";

        //Provisioning metadata queries.
        public static final String ADD_PROVISIONING_METADATA = "INSERT INTO IDN_PROVISIONING_METADATA (STATUS_ID, " +
                "TENANT_ID, PROVISIONING_ENTITY, CONNECTOR_CONFIG) VALUES (?, ?, ?, ?)";
        public static final String GET_PROVISIONING_METADATA = "SELECT * FROM IDN_PROVISIONING_METADATA WHERE " +
                "STATUS_ID IN (#) ORDER BY STATUS_ID";
        public static final String DELETE_PROVISIONING_METADATA = "DELETE FROM IDN_PROVISIONING_METADATA WHERE " +
                "STATUS_ID IN (#)";

    }
}
