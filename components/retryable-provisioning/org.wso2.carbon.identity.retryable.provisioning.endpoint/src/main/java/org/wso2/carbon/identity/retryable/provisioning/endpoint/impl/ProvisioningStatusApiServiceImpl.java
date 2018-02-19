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
package org.wso2.carbon.identity.retryable.provisioning.endpoint.impl;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ApiResponseMessage;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ProvisioningStatusApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.util.RetryableAPIUtils;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;

import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Implementation of the ProvisioningStatusAPI. This api will be used to retrieve the provisioning status information
 * from the database, which will be displayed in the UI.
 *
 * This api is also used when filtering the results. By calling this api with proper parameters will return the
 * filtered results in the response.
 */
public class ProvisioningStatusApiServiceImpl extends ProvisioningStatusApiService {

    private static final Log log = LogFactory.getLog(ProvisioningStatusApiService.class);

    @Override
    public Response provisioningStatusGet(String status, String idp) {
        RetryableProvisioningManager retryableProvisioningManager = RetryableAPIUtils.getRetryableProvisioningService();

        List<ProvisioningStatus> provisioningStatusList;

        if (log.isDebugEnabled()) {
            log.debug("Retrieving provisioning status details.");
        }

        try {
            provisioningStatusList = retryableProvisioningManager.getProvisioningStatus(idp, status);

            if (provisioningStatusList.isEmpty()) {

                if (log.isDebugEnabled()) {
                    log.debug("No matching content found for the given criteria.");
                }

                return Response.status(Response.Status.NO_CONTENT).entity(new ApiResponseMessage(ApiResponseMessage
                        .NO_CONTENT, "No Content")).build();
            }
            return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,
                    new Gson().toJson(provisioningStatusList))).build();
        } catch (RetryableProvisioningException e) {
            log.error("Error while retrieving Provisioning Status information. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
