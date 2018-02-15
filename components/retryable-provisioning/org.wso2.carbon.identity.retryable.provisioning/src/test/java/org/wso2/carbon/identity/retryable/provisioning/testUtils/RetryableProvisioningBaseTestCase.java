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
package org.wso2.carbon.identity.retryable.provisioning.testUtils;

import org.apache.commons.dbcp.BasicDataSource;
import org.wso2.carbon.base.CarbonBaseConstants;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.testutil.powermock.PowerMockIdentityBaseTest;

import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;

public abstract class RetryableProvisioningBaseTestCase extends PowerMockIdentityBaseTest {

    private Map<String, BasicDataSource> dataSourceMap = new HashMap<>();

    protected void initiateInMemoryH2(String dbName) throws Exception {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUsername("username");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:h2:mem:" + dbName);
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("RUNSCRIPT FROM 'src/test/resources/dbscripts/h2.sql'");
        }
        dataSourceMap.put(dbName, dataSource);
    }

    protected void startTenantFlow() {
        System.setProperty(
                CarbonBaseConstants.CARBON_HOME,
                Paths.get(System.getProperty("user.dir"), "src", "test", "resources").toString()
        );
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(SUPER_TENANT_DOMAIN_NAME);
        PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        PrivilegedCarbonContext.endTenantFlow();
    }

    protected void closeH2Base(String dbName) throws Exception {
        BasicDataSource dataSource = dataSourceMap.get(dbName);

        if (dataSource != null) {
            dataSource.close();
        }
    }

    protected Connection getConnection(String dbName) throws Exception {
        BasicDataSource dataSource = dataSourceMap.get(dbName);

        if (dataSource != null) {
            return dataSource.getConnection();
        }
        throw new Exception("Datasource is null");
    }

}
