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

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ReAttemptProvisioningApi;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.dto.RetryProvisioningRequestDTO;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.util.RetryableAPIUtils;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.service.impl.RetryableProvisioningManagerImpl;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore({"javax.*"})
@PrepareForTest({RetryableProvisioningManager.class, PrivilegedCarbonContext.class, RetryableAPIUtils.class})
public class ReAttemptProvisioningApiServiceTest {

    private ReAttemptProvisioningApi reAttemptProvisioningApiService;
    private RetryableProvisioningManager retryableProvisioningManager;
    private RetryProvisioningRequestDTO retryProvisioningRequestDTO;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {
        reAttemptProvisioningApiService = new ReAttemptProvisioningApi();
        retryableProvisioningManager = PowerMockito.mock(RetryableProvisioningManagerImpl.class);
        retryProvisioningRequestDTO = new RetryProvisioningRequestDTO();
    }

    @Test(dataProvider = "ReAttemptDataProvider")
    public void testReAttemptProvisioning(Response.Status status, Throwable throwable) throws RetryableProvisioningException {
        mockStatic(RetryableAPIUtils.class);
        when(RetryableAPIUtils.getRetryableProvisioningService()).thenReturn(retryableProvisioningManager);

        if (status.equals(Response.Status.BAD_REQUEST)) {
            Response response = reAttemptProvisioningApiService.reAttemptPost(retryProvisioningRequestDTO);
            Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
        } else if (status.equals(Response.Status.ACCEPTED)) {
            List<Integer> idsList = new ArrayList<>();
            idsList.add(1);
            retryProvisioningRequestDTO.setProvisioningIds(idsList);
            when(retryableProvisioningManager.retryProvisioning(retryProvisioningRequestDTO.getProvisioningIds()))
                    .thenReturn(true);
            Response response = reAttemptProvisioningApiService.reAttemptPost(retryProvisioningRequestDTO);
            Assert.assertEquals(response.getStatus(), Response.Status.ACCEPTED.getStatusCode());
        } else if (status.equals(Response.Status.INTERNAL_SERVER_ERROR) && throwable == null) {
            List<Integer> idsList = new ArrayList<>();
            idsList.add(1);
            retryProvisioningRequestDTO.setProvisioningIds(idsList);
            when(retryableProvisioningManager.retryProvisioning(retryProvisioningRequestDTO.getProvisioningIds()))
                    .thenReturn(false);
            Response response = reAttemptProvisioningApiService.reAttemptPost(retryProvisioningRequestDTO);
            Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        } else {
            List<Integer> idsList = new ArrayList<>();
            idsList.add(1);
            retryProvisioningRequestDTO.setProvisioningIds(idsList);
            when(retryableProvisioningManager.retryProvisioning(retryProvisioningRequestDTO.getProvisioningIds()))
                    .thenThrow(throwable);
            Response response = reAttemptProvisioningApiService.reAttemptPost(retryProvisioningRequestDTO);
            Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }

    @DataProvider(name = "ReAttemptDataProvider")
    public Object[][] reAttemptDataProvider() {

        RetryableProvisioningException retryableProvisioningException = new RetryableProvisioningException("Error " +
                "while re-attempting provisionings");

        return new Object[][]{
                {Response.Status.BAD_REQUEST, null},
                {Response.Status.ACCEPTED, null},
                {Response.Status.INTERNAL_SERVER_ERROR, null},
                {Response.Status.INTERNAL_SERVER_ERROR, retryableProvisioningException}
        };
    }

}
