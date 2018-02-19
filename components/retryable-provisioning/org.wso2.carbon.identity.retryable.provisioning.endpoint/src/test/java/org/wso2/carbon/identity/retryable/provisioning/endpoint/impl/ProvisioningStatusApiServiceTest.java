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

import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.ProvisioningStatusApi;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.impl.utils.TestUtils;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.util.RetryableAPIUtils;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore({"javax.*"})
@PrepareForTest({RetryableProvisioningManager.class, PrivilegedCarbonContext.class, RetryableAPIUtils.class})
public class ProvisioningStatusApiServiceTest {

    private static final String IDP = "test_IDP";
    private static final String STATUS = "SUCCESS";
    private ProvisioningStatusApi provisioningStatusApiService;

    @Mock
    private RetryableProvisioningManager retryableProvisioningManager;

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() {
        initMocks(this);
        provisioningStatusApiService = new ProvisioningStatusApi();
    }

    @DataProvider(name = "GetProvisioningStatus")
    public Object[][] buildGetProvisioningStatus() {

        RetryableProvisioningException retryableProvisioningException = new RetryableProvisioningException("Error " +
                "when retrieving provisioning status");

        return new Object[][]{
                {OK, null},
                {Response.Status.NO_CONTENT, null},
                {Response.Status.INTERNAL_SERVER_ERROR, retryableProvisioningException}
        };
    }

    @Test(dataProvider = "GetProvisioningStatus")
    public void testProvisioningStatusGet(Response.Status status, Throwable exception) throws
            RetryableProvisioningException {

        mockStatic(RetryableAPIUtils.class);
        when(RetryableAPIUtils.getRetryableProvisioningService()).thenReturn(retryableProvisioningManager);
        if (status.equals(OK)) {
            ProvisioningStatus provisioningStatus = TestUtils.generateProvisioningStatus();
            List<ProvisioningStatus> provisioningStatusList = new ArrayList<>();
            provisioningStatusList.add(provisioningStatus);

            when(retryableProvisioningManager.getProvisioningStatus(IDP, STATUS)).thenReturn(provisioningStatusList);
            Response response = provisioningStatusApiService.provisioningStatusGet(STATUS, IDP);
            Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        } else if (status.equals(NO_CONTENT)) {
            when(retryableProvisioningManager.getProvisioningStatus(IDP, STATUS))
                    .thenReturn(new ArrayList<ProvisioningStatus>());
            Response response = provisioningStatusApiService.provisioningStatusGet(STATUS, IDP);
            Assert.assertEquals(response.getStatus(), Response.Status.NO_CONTENT.getStatusCode());
        } else {
            when(retryableProvisioningManager.getProvisioningStatus(IDP, STATUS)).thenThrow(exception);
            Response response = provisioningStatusApiService.provisioningStatusGet(STATUS, IDP);
            Assert.assertEquals(response.getStatus(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
        }

    }
}
