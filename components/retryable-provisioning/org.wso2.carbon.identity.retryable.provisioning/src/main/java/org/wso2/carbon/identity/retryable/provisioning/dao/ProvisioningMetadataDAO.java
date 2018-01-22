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

import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningMetadata;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;

import java.util.List;

/**
 * Interface to define the DB transactions related to Provisioning Metadata.
 * i.e Provisioning entity, configurations etc.
 */
public interface ProvisioningMetadataDAO {

    /**
     * Method to persist provisioning metadata. This information will be retrieved when attempting a retry.
     *
     * @return true is successful, false otherwise.
     */
    boolean addProvisioningMetadata(ProvisioningMetadata metadata) throws RetryableProvisioningException;

    /**
     * Method to retrieve provisioning metadata for a given list of provisioning ids. The parameter will also be a
     * list.
     *
     * @param provisioningIds : The list of provisioning identification numbers which the metadata should be retrieved.
     * @return : List of Provisioning metadata objects.
     */
    List<ProvisioningMetadata> getProvisioningMetadata(List<Integer> provisioningIds)
            throws RetryableProvisioningException;

    /**
     * Method to delete a set of provisioning metadata for a given list of provisioning ids.
     *
     * @param provisioningIds : The list of provisioning identification numbers which the metadata should be deleted.
     * @return : True if deletion is successful, false otherwise.
     */
    boolean deleteProvisioningMetadata(List<Integer> provisioningIds) throws RetryableProvisioningException;

}
