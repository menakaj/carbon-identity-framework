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
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ApiResponseMessage;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.DeleteProvisioningApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.dto.RetryProvisioningRequestDTO;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.util.RetryableAPIUtils;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;

import javax.ws.rs.core.Response;

/**
 * Implementation class for DeleteProvisioningApiService.
 * This api will take a list of status ids, which needs to be deleted.
 */
public class DeleteProvisioningApiServiceImpl extends DeleteProvisioningApiService {

    private static final Log log = LogFactory.getLog(DeleteProvisioningApiService.class);

    @Override
    public Response deleteProvisioningPost(RetryProvisioningRequestDTO provisioningIds) {

        RetryableProvisioningManager retryableProvisioningManager = RetryableAPIUtils.getRetryableProvisioningService();

        if (log.isDebugEnabled()) {
            log.debug("Deleting Provisioning status information.");
        }

        if (provisioningIds.getProvisioningIds().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You should specify at least 1 entity to " +
                    "delete.").build();
        }

        try {
            boolean success = retryableProvisioningManager.deleteStatusEntry(provisioningIds.getProvisioningIds());

            if (log.isDebugEnabled()) {
                if (success) {
                    log.debug("Deleting provisioning status success.");
                } else {
                    log.debug("Deleting provisioning status failed");
                }
            }

            return success ? Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,
                    "Provisioning details deleted successfully.")).build() :
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(
                            ApiResponseMessage.ERROR, "Deleting provisioning details failed.")).build();
        } catch (RetryableProvisioningException e) {
            log.error("Error while deleting provisioning status. ", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(
                    ApiResponseMessage.ERROR, "Deleting provisioning details failed.")).build();

        }
    }
}
