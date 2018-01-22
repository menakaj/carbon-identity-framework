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
package org.wso2.carbon.identity.retryable.provisioning.service;

import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;

import java.util.List;
import java.util.Map;

/**
 * Retryable Provisioning Service interface. This contains the core functionality definitions of retryable
 * provisioning.
 */
public interface RetryableProvisioningManager {

    /**
     * This method will handle the processing of the received event. Extract each property from the properties map
     * and persist them in the database.
     *
     * @param eventProperties : The set of event properties.
     */
    void processEvent(Map<String, Object> eventProperties);

    /**
     * Method to get the list of provisioning status which matches the given parameters. If the parameters are empty,
     * all the records will be retrieved.
     *
     * @param idp    : The name of the idp which the records should be retrieved.
     * @param status : The status of the required records
     * @param entity : The required entity.
     * @return List of ProvisioningStatus objects which matches the provided parameters.
     */
    List<ProvisioningStatus> getProvisioningStatus(String idp, String status, String entity);

    /**
     * Delete the set of Provisioning status entries which matches the given identifiers. This will be called by the
     * REST api.
     *
     * @param statusIds : The list of identifiers that need to be deleted.
     * @return : True if the entries deleted. False otherwise.
     */
    boolean deleteStatusEntry(List<Integer> statusIds);

    /**
     * Method to re-attempt the user selected, failed provisions.
     *
     * @param statusIds : The identifier of the failed Provisioning which should be re-attempted.
     */
    void retryProvisioning(List<Integer> statusIds);

}
