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
package org.wso2.carbon.identity.retryable.provisioning.endpoint;

import io.swagger.annotations.ApiParam;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.dto.RetryProvisioningRequestDTO;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.factories.DeleteProvisioningApiServiceFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/delete-provisioning")
@Consumes({"application/json"})
@Produces({"application/json"})
@io.swagger.annotations.Api(value = "/delete-provisioning")
public class DeleteProvisioningApi {

    private final DeleteProvisioningApiService delegate = DeleteProvisioningApiServiceFactory.getDeleteProvisioningApi();

    @POST

    @Consumes({"application/json"})
    @Produces({"application/json"})
    @io.swagger.annotations.ApiOperation(value = "Delete Provisioning.\n", notes = "This API is used to delete the selected set of provisionings.\n", response = void.class)
    @io.swagger.annotations.ApiResponses(value = {
            @io.swagger.annotations.ApiResponse(code = 202, message = "Accepted"),

            @io.swagger.annotations.ApiResponse(code = 400, message = "Bad Request"),

            @io.swagger.annotations.ApiResponse(code = 500, message = "Server Error")})

    public Response deleteProvisioningPost(@ApiParam(value = "The list of provisioning status ids to be deleted.", required = true) RetryProvisioningRequestDTO provisioningIds) {
        return delegate.deleteProvisioningPost(provisioningIds);
    }
}

