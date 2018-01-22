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
package org.wso2.carbon.identity.retryable.provisioning.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.util.IdentityDatabaseUtil;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningMetadata;
import org.wso2.carbon.identity.retryable.provisioning.constants.RetryableProvisioningConstants;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningMetadataDAO;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProvisioningMetadataDAOImpl implements ProvisioningMetadataDAO {

    private static final Log log = LogFactory.getLog(ProvisioningMetadataDAOImpl.class);

    @Override
    public boolean addProvisioningMetadata(ProvisioningMetadata metadata) throws RetryableProvisioningException {

        try (Connection connection = IdentityDatabaseUtil.getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(RetryableProvisioningConstants
                     .DAOConstants.ADD_PROVISIONING_METADATA)) {

            boolean isAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            if (log.isDebugEnabled()) {
                log.debug("Add provisioning metadata");
            }

            preparedStatement.setInt(1, metadata.getStatusId());
            preparedStatement.setInt(2, metadata.getTenantId());
            preparedStatement.setString(3, metadata.getProvisioningEntity());
            preparedStatement.setString(4, metadata.getConnectorConfiguration());

            preparedStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while persisting Provisioning metadata", e);
        }
        return true;
    }

    @Override
    public List<ProvisioningMetadata> getProvisioningMetadata(List<Integer> provisioningIds) throws RetryableProvisioningException {

        List<ProvisioningMetadata> provisioningMetadataList = new ArrayList<>();
        ResultSet resultSet = null;

        if (log.isDebugEnabled()) {
            log.debug("Retrieving Provisioning metadata.");
        }

        try (Connection connection = IdentityDatabaseUtil.getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(RetryableProvisioningConstants
                     .DAOConstants.GET_PROVISIONING_METADATA)) {

            Array ids = connection.createArrayOf("INTEGER", provisioningIds.toArray());

            preparedStatement.setArray(1, ids);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ProvisioningMetadata provisioningMetadata = new ProvisioningMetadata();
                provisioningMetadata.setStatusId(resultSet.getInt("STATUS_ID"));
                provisioningMetadata.setConnectorConfiguration(resultSet.getString("CONNECTOR_CONFIG"));
                provisioningMetadata.setProvisioningEntity(resultSet.getString("PROVISIONING_ENTITY"));
                provisioningMetadata.setTenantId(resultSet.getInt("TENANT_ID"));
                provisioningMetadataList.add(provisioningMetadata);
            }

        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while retrieving provisioning metadata.", e);
        } finally {
            IdentityDatabaseUtil.closeResultSet(resultSet);
        }
        return provisioningMetadataList;
    }

    @Override
    public boolean deleteProvisioningMetadata(List<Integer> provisioningIds) throws RetryableProvisioningException {

        if (log.isDebugEnabled()) {
            log.debug("Deleting provisioning metadata.");
        }

        try (Connection connection = IdentityDatabaseUtil.getDBConnection();
             PreparedStatement preparedStatement =
                     connection.prepareStatement(
                             RetryableProvisioningConstants.DAOConstants.DELETE_PROVISIONING_METADATA)) {

            boolean isAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            Array ids = connection.createArrayOf("INTEGER", provisioningIds.toArray());

            preparedStatement.setArray(1, ids);
            preparedStatement.executeUpdate();
            connection.commit();

            connection.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while deleting Provisioning Metadata.", e);
        }
        return true;
    }
}
