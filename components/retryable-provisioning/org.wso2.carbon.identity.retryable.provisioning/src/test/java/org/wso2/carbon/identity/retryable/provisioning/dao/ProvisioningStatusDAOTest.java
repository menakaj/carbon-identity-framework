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

import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.Assert;
import org.testng.IObjectFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.retryable.provisioning.beans.FilterConfig;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.dao.impl.ProvisioningStatusDAOImpl;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.RetryableProvisioningBaseTestCase;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.TestUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({PrivilegedCarbonContext.class, IdentityDatabaseUtil.class})
@PowerMockIgnore({"javax.*", "org.apache.*", "org.w3c.*"})
public class ProvisioningStatusDAOTest extends RetryableProvisioningBaseTestCase {

    private ProvisioningStatusDAO provisioningStatusDAO;
    private static final Integer STATUS_ID_WITH_NO_RECORDS = -1;
    private static final Integer STATUS_ID_WITH_RECORDS = 1;
    private static final String DATABASE_NAME = "PROVISIONING_STATUS_DB";


    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() throws Exception {
        initMocks(this);
        initiateInMemoryH2(DATABASE_NAME);
        startTenantFlow();
    }

    @Test
    public void testGetProvisioningStatusByNameWithNoRecords() throws Exception {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();

        Connection connection = getConnection(DATABASE_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        Integer status = provisioningStatusDAO.getProvisioningStatusByName("");
        Assert.assertEquals(status, STATUS_ID_WITH_NO_RECORDS);
    }

    @Test(dependsOnMethods = "testGetProvisioningStatusByNameWithNoRecords")
    public void testAddProvisioningStatus() throws Exception {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();

        Connection connection = getConnection(DATABASE_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        Integer statusId = provisioningStatusDAO.addProvisioningStatus(TestUtils.generateProvisioningStatus());
        Assert.assertEquals(statusId, STATUS_ID_WITH_RECORDS);
    }

    @Test(dependsOnMethods = "testAddProvisioningStatus")
    public void testGetProvisioningStatus() throws Exception {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();

        Connection connection = getConnection(DATABASE_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        FilterConfig filterConfig = new FilterConfig();
        filterConfig.setStatus("SUCCESS");

        List<ProvisioningStatus> provisioningStatus = provisioningStatusDAO.getProvisioningStatus(filterConfig,
                MultitenantConstants.SUPER_TENANT_ID);
        Assert.assertTrue(provisioningStatus.size() > 0);
    }

    @Test(dependsOnMethods = "testGetProvisioningStatus")
    public void testGetProvisioningStatusByNameWithRecords() throws Exception {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();

        Connection connection = getConnection(DATABASE_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        Integer status = provisioningStatusDAO.getProvisioningStatusByName("test@wso2.com");
        Assert.assertNotEquals(status, STATUS_ID_WITH_NO_RECORDS);
    }

    @Test(dependsOnMethods = "testGetProvisioningStatusByNameWithRecords")
    public void testDeleteProvisioningStatus() throws Exception {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();

        Connection connection = getConnection(DATABASE_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        List<Integer> provisioningStatusIds = new ArrayList<>();
        provisioningStatusIds.add(1);
        Boolean status = provisioningStatusDAO.deleteProvisioningStatus(provisioningStatusIds);
        Assert.assertTrue(status);
    }

    @Test(expectedExceptions = RetryableProvisioningException.class)
    public void testDeleteProvisioningStatusWithException() throws SQLException, RetryableProvisioningException {
        provisioningStatusDAO = new ProvisioningStatusDAOImpl();
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        PreparedStatement preparedStatement = Mockito.mock(PreparedStatement.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        when(preparedStatement.execute()).thenThrow(new SQLException());

        List<Integer> provisioningStatusIds = new ArrayList<>();
        provisioningStatusIds.add(1);
        provisioningStatusIds.add(2);
        provisioningStatusDAO.deleteProvisioningStatus(provisioningStatusIds);
    }

    @Test(expectedExceptions = RetryableProvisioningException.class)
    public void testGetProvisioningStatusWithException() throws SQLException, RetryableProvisioningException {
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        FilterConfig filterConfig = new FilterConfig();
        filterConfig.setStatus("Success");

        provisioningStatusDAO.getProvisioningStatus(filterConfig, -1234);

    }

    @Test(expectedExceptions = RetryableProvisioningException.class)
    public void testGetProvisioningStatusByNameWithException() throws SQLException, RetryableProvisioningException {
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        FilterConfig filterConfig = new FilterConfig();
        filterConfig.setStatus("Success");

        provisioningStatusDAO.getProvisioningStatusByName("");
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeH2Base(DATABASE_NAME);
    }

}
