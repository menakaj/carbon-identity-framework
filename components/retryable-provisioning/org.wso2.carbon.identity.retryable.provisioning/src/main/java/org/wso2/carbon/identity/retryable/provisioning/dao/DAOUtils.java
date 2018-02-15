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
package org.wso2.carbon.identity.retryable.provisioning.dao;

import org.wso2.carbon.identity.retryable.provisioning.constants.RetryableProvisioningConstants;

/**
 * Utility class for DAOs.
 */
public class DAOUtils {

    private DAOUtils() {
        //Private Constructor.
    }

    /**
     * This method will generate dynamic sql query in the following format.
     *
     * SELECT * FROM TABLE_X WHERE ID IN (?, ?, ?, ... ?);
     * DELETE FROM TABLE_Y WHERE ID IN (?, ?, ?, ... ?);
     *
     * The reason for dynamically building this is, some db drivers does not support setting Arrays as follows.
     *
     * Array ids = connection.createArrayOf(<String>, <array>)
     *
     * Calling the createArrayOf method throws Feature Not supported exception. So as a generic implementation, we
     * build the query dynamically.
     *
     * @param query : The db query that should be rebuilt
     * @param length : The length of the variables array.
     * @return : The modified query,
     */
    public static String buildDynamicQuery(String query, int length) {

        StringBuilder stringBuilder = new StringBuilder();

        if (length == 1) {
            return query.replaceAll(RetryableProvisioningConstants.REPLACEABLE_CHAR,
                    RetryableProvisioningConstants.VALUE_PLACEHOLDER);
        }

        for (int i = 0; i < length; i++) {
            stringBuilder.append(RetryableProvisioningConstants.VALUE_PLACEHOLDER);

            if (i != length - 1) { //If the current element is not the last element, add a ','
                stringBuilder.append(RetryableProvisioningConstants.COMMA);
            }
        }
        return query.replaceAll(RetryableProvisioningConstants.REPLACEABLE_CHAR, stringBuilder.toString());
    }
}
