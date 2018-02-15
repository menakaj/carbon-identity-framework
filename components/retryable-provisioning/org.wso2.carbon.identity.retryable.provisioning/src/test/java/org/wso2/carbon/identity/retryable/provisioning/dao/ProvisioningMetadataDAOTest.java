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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.wso2.carbon.identity.base.IdentityRuntimeException;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningMetadata;
import org.wso2.carbon.identity.retryable.provisioning.dao.impl.ProvisioningMetadataDAOImpl;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.RetryableProvisioningBaseTestCase;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.TestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest({PrivilegedCarbonContext.class, IdentityDatabaseUtil.class})
@PowerMockIgnore({"javax.*", "org.apache.*", "org.w3c.*"})
public class ProvisioningMetadataDAOTest extends RetryableProvisioningBaseTestCase {

    private static final Log log = LogFactory.getLog(ProvisioningMetadataDAOTest.class);
    private ProvisioningMetadataDAO provisioningMetadataDAO;
    private static final String DB_NAME = "PROVISIONING_METADATA_DB";
    private static final String GET_CONNECTION_ERR = "ERROR WHILE CREATING CONNECTION";

    @ObjectFactory
    public IObjectFactory getObjectFactory() {
        return new org.powermock.modules.testng.PowerMockObjectFactory();
    }

    @BeforeClass
    public void init() throws Exception {
        initMocks(this);
        initiateInMemoryH2(DB_NAME);
        startTenantFlow();
    }

    @AfterClass
    public void tearDown() throws Exception {
        closeH2Base(DB_NAME);
    }

    @Test (description = "Test the Get provisioning metadata when there are no records.")
    public void testGetProvisioningMetadataWithNoRecords() throws Exception {
        log.info("Test get provisioning metadata with no records");
        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        List<Integer> metadataIdList = new ArrayList<>();
        metadataIdList.add(1);

        List<ProvisioningMetadata> provisioningMetadataList = provisioningMetadataDAO.getProvisioningMetadata
                (metadataIdList);

        Assert.assertTrue(provisioningMetadataList.size() == 0);
    }

    @Test(description = "Test method to add provisioning metadata.", dependsOnMethods =
            "testGetProvisioningMetadataWithNoRecords")
    public void testAddProvisioningMetadata() throws Exception {
        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();

        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        provisioningMetadataDAO.addProvisioningMetadata(TestUtils.generateProvisioningMetadata());
    }

    @Test(description = "Test method to get provisioning metadata.", dependsOnMethods = "testAddProvisioningMetadata")
    public void testGetProvisioningMetadata() throws Exception {
        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        List<Integer> metadataIdList = new ArrayList<>();
        metadataIdList.add(1);

        List<ProvisioningMetadata> provisioningMetadataList = provisioningMetadataDAO.getProvisioningMetadata
                (metadataIdList);

        Assert.assertTrue(provisioningMetadataList.size() > 0);
    }

    @Test(description = "Test method to test delete metadata from the database.", dependsOnMethods =
            "testGetProvisioningMetadata")
    public void testDeleteMetadata() throws Exception {
        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        Connection connection = getConnection(DB_NAME);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);

        List<Integer> metadataIdList = new ArrayList<>();
        metadataIdList.add(1);

        Boolean status = provisioningMetadataDAO.deleteProvisioningMetadata(metadataIdList);
        Assert.assertTrue(status);
    }

    @Test(description = "Test exception scenario when adding provisioning metadata.", expectedExceptions =
            RetryableProvisioningException.class)
    public void testAddProvisioningMetadataWithException() throws SQLException, RetryableProvisioningException {
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.addProvisioningMetadata(TestUtils.generateProvisioningMetadata());
    }

    @Test(description = "Test exception scenario when retrieving provisioning metadata.", expectedExceptions =
            RetryableProvisioningException.class)
    public void testGetProvisioningMetadataWithException() throws SQLException, RetryableProvisioningException {
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        List<Integer> metadataIds = new ArrayList<>();
        metadataIds.add(1);

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.getProvisioningMetadata(metadataIds);
    }

    @Test(description = "Test exception scenario when deleting metadata.", expectedExceptions =
            RetryableProvisioningException.class)
    public void testDeleteProvisioningMetadataWithException() throws SQLException, RetryableProvisioningException {
        Connection connection = Mockito.mock(Connection.class, Mockito.RETURNS_MOCKS);
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenReturn(connection);
        when(connection.prepareStatement(Mockito.anyString())).thenThrow(new SQLException());

        List<Integer> metadataIds = new ArrayList<>();
        metadataIds.add(1);

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.deleteProvisioningMetadata(metadataIds);
    }

    @Test(description = "Test exception scenario on getConnection method is called when Adding Provisioning metadata",
            expectedExceptions = IdentityRuntimeException.class)
    public void testAddProvisioningMetadataWithGetConnectionThrowException() throws RetryableProvisioningException {
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenThrow(new IdentityRuntimeException(GET_CONNECTION_ERR));

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.addProvisioningMetadata(TestUtils.generateProvisioningMetadata());
    }

    @Test(description = "Test exception scenario on getConnection method is called when retrieving Provisioning " +
            "metadata",
            expectedExceptions = IdentityRuntimeException.class)
    public void testGetProvisioningMetadataWithGetConnectionThrowException() throws RetryableProvisioningException {
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenThrow(new IdentityRuntimeException(GET_CONNECTION_ERR));

        List<Integer> metadataIds = new ArrayList<>();
        metadataIds.add(1);

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.getProvisioningMetadata(metadataIds);
    }

    @Test(description = "Test exception scenario on getConnection method is called when deleting Provisioning " +
            "metadata", expectedExceptions = IdentityRuntimeException.class)
    public void testDeleteProvisioningMetadataWithGetConnectionThrowException() throws RetryableProvisioningException {
        mockStatic(IdentityDatabaseUtil.class);
        when(IdentityDatabaseUtil.getDBConnection()).thenThrow(new IdentityRuntimeException(GET_CONNECTION_ERR));

        List<Integer> metadataIds = new ArrayList<>();
        metadataIds.add(1);

        provisioningMetadataDAO = new ProvisioningMetadataDAOImpl();
        provisioningMetadataDAO.deleteProvisioningMetadata(metadataIds);
    }
}
