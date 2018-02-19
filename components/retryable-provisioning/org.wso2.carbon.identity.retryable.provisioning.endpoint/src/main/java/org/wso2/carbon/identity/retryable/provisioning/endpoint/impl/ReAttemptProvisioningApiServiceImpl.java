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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.regexp.RE;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ApiResponseMessage;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ReAttemptProvisioningApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.dto.RetryProvisioningRequestDTO;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.util.RetryableAPIUtils;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;

import javax.ws.rs.core.Response;

/**
 * The implementation class for the ReAttemptProvisioningAPI, which will be used for re-attempting the selected
 * provisions.
 *
 * This api takes a list of Ids which represents the provisions.
 */
public class ReAttemptProvisioningApiServiceImpl extends ReAttemptProvisioningApiService {

    private static final Log log = LogFactory.getLog(ReAttemptProvisioningApiService.class);

    @Override
    public Response reAttemptPost(RetryProvisioningRequestDTO provisioningIds) {

        RetryableProvisioningManager retryableProvisioningManager = RetryableAPIUtils.getRetryableProvisioningService();

        if (provisioningIds.getProvisioningIds().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You should specify at least 1 failed entity " +
                    "to retry.").build();
        }

        try {
            boolean result = retryableProvisioningManager.retryProvisioning(provisioningIds.getProvisioningIds());
            return result ? Response.status(Response.Status.ACCEPTED).entity(new ApiResponseMessage
                    (ApiResponseMessage.OK, "The provisioning request received.Please note that only the failed" +
                            " provisions will be re-attempted.")).build() :
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage
                            (ApiResponseMessage.ERROR, "Error occurred while reattempting the provisioning.")).build();
        } catch (RetryableProvisioningException e) {
            log.error("Error occurred while re-attempting the provisioning. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage
                    (ApiResponseMessage.ERROR, "Error occurred while reattempting the provisioning.")).build();
        }
    }
}
