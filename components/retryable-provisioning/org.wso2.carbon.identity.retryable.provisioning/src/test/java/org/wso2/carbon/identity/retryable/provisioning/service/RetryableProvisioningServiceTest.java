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

import org.junit.Assert;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.provisioning.OutboundProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningMetadataDAO;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningStatusDAO;
import org.wso2.carbon.identity.retryable.provisioning.dao.impl.ProvisioningStatusDAOImpl;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.service.impl.RetryableProvisioningManagerImpl;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.RetryableProvisioningBaseTestCase;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.TestUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.wso2.carbon.utils.multitenancy.MultitenantConstants.TENANT_DOMAIN;

@PrepareForTest({ProvisioningStatusDAO.class, ProvisioningMetadataDAO.class, PrivilegedCarbonContext.class,
        IdentityDatabaseUtil.class, OutboundProvisioningManager.class})
@PowerMockIgnore({"javax.*", "org.apache.*", "org.w3c.*", "org.wso2.carbon.tomcat.*"})
public class RetryableProvisioningServiceTest extends RetryableProvisioningBaseTestCase {

    private RetryableProvisioningManager retryableProvisioningManager;
    private static final String DB_NAME = "STATUS_DB";
    private static final String TEST_IDP = "test_idp";

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void setup() throws Exception {
        initMocks(this);
        initiateInMemoryH2(DB_NAME);
        startTenantFlow();
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeH2Base(DB_NAME);
    }

    @Test (description = "Test the process event method.")
    public void testProcessEvent() throws Exception {
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        retryableProvisioningManager = new RetryableProvisioningManagerImpl();
        retryableProvisioningManager.processEvent(TestUtils.buildEventMap());
        connection.close();
    }

    @Test (description = "Test the process event method when existing data is available.")
    public void testProcessEventWithExistingStatus() throws Exception {
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        retryableProvisioningManager = new RetryableProvisioningManagerImpl();
        retryableProvisioningManager.processEvent(TestUtils.buildEventMap());
    }

    @Test (description = "Test method to retrieve provisioning status for the given filtering criteria.")
    public void testGetProvisioningStatus() throws Exception {
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        retryableProvisioningManager = new RetryableProvisioningManagerImpl();
        List<ProvisioningStatus> provisioningStatusList = retryableProvisioningManager.getProvisioningStatus(TEST_IDP,
                "");
        connection.close();
        Assert.assertNotNull(provisioningStatusList);
    }

    @Test(description = "Test retry provisioning with RetryableProvisioningException.", expectedExceptions =
            RetryableProvisioningException
            .class)
    public void testRetryProvisioningWithException() throws Exception {
        List<Integer> idsToRetry = new ArrayList<>();
        idsToRetry.add(1);

        ProvisioningStatusDAOImpl provisioningStatusDAO = Mockito.mock(ProvisioningStatusDAOImpl.class);
        whenNew(ProvisioningStatusDAOImpl.class).withNoArguments().thenReturn(provisioningStatusDAO);
        when(provisioningStatusDAO.deleteProvisioningStatus(idsToRetry)).thenReturn(true);

        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        mockStatic(OutboundProvisioningManager.class);
        OutboundProvisioningManager outboundProvisioningManager = Mockito.mock(OutboundProvisioningManager.class);
        when(OutboundProvisioningManager.getInstance()).thenReturn(outboundProvisioningManager);

        when(outboundProvisioningManager.provisionToIDP(TestUtils.generateProvisioningEntity(),
                TestUtils.generateProvisioningStatus().getIdpName(), TENANT_DOMAIN)).thenReturn(true);

        retryableProvisioningManager = new RetryableProvisioningManagerImpl();
        retryableProvisioningManager.retryProvisioning(idsToRetry);
    }
}
