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
import org.wso2.carbon.identity.retryable.provisioning.beans.FilterConfig;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.constants.RetryableProvisioningConstants;
import org.wso2.carbon.identity.retryable.provisioning.dao.ProvisioningStatusDAO;
import org.wso2.carbon.identity.retryable.provisioning.exception.RetryableProvisioningException;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class for Retryable Provisioning status DAO.
 */
public class ProvisioningStatusDAOImpl implements ProvisioningStatusDAO {

    private static final Log log = LogFactory.getLog(ProvisioningStatusDAOImpl.class);

    @Override
    public Integer addProvisioningStatus(ProvisioningStatus provisioningStatus) throws RetryableProvisioningException {

        ResultSet rs = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Integer statusId = -1;

        try {
            connection = IdentityDatabaseUtil.getDBConnection();
            preparedStatement = connection.prepareStatement(RetryableProvisioningConstants.DAOConstants.ADD_PROVISIONING_STATUS);

            boolean isAutoCommit = connection.getAutoCommit();

            if (log.isDebugEnabled()) {
                log.debug("Persisting provisioning status.");
            }

            connection.setAutoCommit(false);
            preparedStatement.setInt(1, provisioningStatus.getTenantId());
            preparedStatement.setString(2, provisioningStatus.getIdpName());
            preparedStatement.setString(3, provisioningStatus.getStatus());
            preparedStatement.setString(4, provisioningStatus.getEntity());
            preparedStatement.setString(5, getProvisioningOperation(provisioningStatus.getOperation()));
            preparedStatement.setString(6, provisioningStatus.getCause());

            preparedStatement.executeUpdate();

            rs = preparedStatement.getGeneratedKeys();

            //Get the auto generated status id.
            while (rs.next()) {
                if (log.isDebugEnabled()) {
                    log.debug("Provisioning Status persisted successfully.");
                }
                statusId = rs.getInt(1);
            }

            connection.commit();
            connection.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while persisting provisioning status information", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, rs, preparedStatement);
        }

        return statusId;
    }

    @Override
    public List<ProvisioningStatus> getProvisioningStatus(FilterConfig filterConfig, Integer tenantId) throws
            RetryableProvisioningException {

        String entity = filterConfig.getEntity();
        String status = filterConfig.getStatus();
        String idp = filterConfig.getIdp();

        List<ProvisioningStatus> provisioningStatusList = new ArrayList<>();

        Connection connection = IdentityDatabaseUtil.getDBConnection();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement(RetryableProvisioningConstants.DAOConstants
                    .GET_PROVISIONING_STATUS);

            // Build the sql query. If the filter is not present used wild card.
            preparedStatement.setInt(1, tenantId);
            preparedStatement.setString(2, status != null ? status : "%");
            preparedStatement.setString(3, entity != null ? entity : "%");
            preparedStatement.setString(4, idp != null ? idp : "%");

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                if (log.isDebugEnabled()) {
                    log.debug("Provisioning Status found for given filter config.");
                }

                ProvisioningStatus provisioningStatus = new ProvisioningStatus();
                provisioningStatus.setTenantId(resultSet.getInt("TENANT_ID"));
                provisioningStatus.setIdentifier(resultSet.getInt("STATUS_ID"));
                provisioningStatus.setStatus(resultSet.getString("STATUS"));
                provisioningStatus.setEntity(resultSet.getString("ENTITY"));
                provisioningStatus.setIdpName(resultSet.getString("IDP_NAME"));
                provisioningStatus.setOperation(resultSet.getString("OPERATION"));
                provisioningStatus.setCause(resultSet.getString("CAUSE"));

                provisioningStatusList.add(provisioningStatus);
            }

        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while retrieving Provisioning Status.", e);
        } finally {
            IdentityDatabaseUtil.closeAllConnections(connection, resultSet, preparedStatement);
        }

        return provisioningStatusList;
    }

    @Override
    public boolean deleteProvisioningStatus(List<Integer> provisioningIDs) throws RetryableProvisioningException {
        if (log.isDebugEnabled()) {
            log.debug("Deleting provisioning status.");
        }

        try (Connection connection = IdentityDatabaseUtil.getDBConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(RetryableProvisioningConstants
                     .DAOConstants.DELETE_PROVISIONING_STATUS)) {
            boolean isAutoCommit = connection.getAutoCommit();

            Array ids = connection.createArrayOf("INTEGER", provisioningIDs.toArray());

            preparedStatement.setArray(1, ids);
            preparedStatement.executeUpdate();

            if (log.isDebugEnabled()) {
                log.debug("Provisioning status deleted successfully.");
            }
            connection.commit();

            connection.setAutoCommit(isAutoCommit);
        } catch (SQLException e) {
            throw new RetryableProvisioningException("Error while deleting Provisioning Status", e);
        }
        return true;
    }

    /**
     * Method to get the operation based on the http method.
     *
     * @param method : The HTTP Method
     * @return :
     */
    private String getProvisioningOperation(String method) {
        String op = method.toLowerCase();
        switch (op) {
            case "post":
                return RetryableProvisioningConstants.CREATE;
            case "delete":
                return RetryableProvisioningConstants.DELETE;
            default:
                return RetryableProvisioningConstants.UPDATE;
        }
    }
}
