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
package org.wso2.carbon.identity.retryable.provisioning.endpoint.util;

import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.service.impl.RetryableProvisioningManagerImpl;

/**
 * Utility class for Retryable Provisioning REST API.
 */
public class RetryableAPIUtils {

    /**
     * Get the RetryableProvisioning service.
     *
     * @return RetryableProvisioningManager class.
     */
    public static RetryableProvisioningManager getRetryableProvisioningService() {
        return ((RetryableProvisioningManager) PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService
                (RetryableProvisioningManager.class, null));
    }
}
