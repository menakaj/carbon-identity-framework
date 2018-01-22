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

import org.wso2.carbon.identity.retryable.provisioning.beans.FilterConfig;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;

import java.util.List;

/**
 * This interface defines db transactions related to Provisioning status. This class will have the access to the
 * IDN_PROVISIONING_STATUS table.
 * Provisioning status will be used by the UI to visualize whether the provision is successful or not.
 */
public interface ProvisioningStatusDAO {

    /**
     * Method to persist provisioning status information.
     *
     * @param provisioningStatus : The provisioning status object created from the outbound provisioning connector
     *                           response.
     * @return The auto generated provisioning status identifier. (-1 if error occurred)
     */
    Integer addProvisioningStatus(ProvisioningStatus provisioningStatus) throws RetryableProvisioningException;

    /**
     * Method to retrieve a list of provisioning status entries which matches the given filter. The filter is defined
     * as follows.
     * 1. Status : The provisioning status. (Success or failed)
     * 2. Entity : The provisioning entity.
     * 3. IDP : The IDP to which the provisioning belongs to.
     * <p>
     * If the filtering configuration is not specified, the method will return all the records based on the pagination.
     *
     * @param filterConfig : Filtering options that should be applied to retrieve the provisioning status information.
     * @param tenantId : The tenant id which the provisioning status should be retrieved.
     * @return : List of provisioning status objects.
     */
    List<ProvisioningStatus> getProvisioningStatus(FilterConfig filterConfig, Integer tenantId)
            throws RetryableProvisioningException;

    /**
     * Deleted the given provisioning status entries.
     *
     * @param provisioningIDs : The list of provisioning ids to be deleted.
     */
    boolean deleteProvisioningStatus(List<Integer> provisioningIDs) throws RetryableProvisioningException;

}
