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
package org.wso2.carbon.identity.retryable.provisioning.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.retryable.provisioning.handler.ProvisioningStatusHandler;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.service.impl.RetryableProvisioningManagerImpl;

@Component(name = "org.wso2.carbon.identity.retryable.provisioning.internal.RetryableProvisioningServiceComponent",
        immediate = true)
public class RetryableProvisioningServiceComponent {
    private static Log log = LogFactory.getLog(RetryableProvisioningServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {

        try {
            BundleContext bundleContext = context.getBundleContext();
            bundleContext.registerService(AbstractEventHandler.class.getName(), new ProvisioningStatusHandler(),
                    null);
            bundleContext.registerService(RetryableProvisioningManager.class.getName(),
                    new RetryableProvisioningManagerImpl(), null);
        } catch (Throwable t) {
            log.error("Error while activating the Retryble Provisioning bundle ", t);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Identity Management bundle is de-activated");
        }
    }

}
