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
package org.wso2.carbon.identity.retryable.provisioning.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.core.handler.InitConfig;
import org.wso2.carbon.identity.event.IdentityEventException;
import org.wso2.carbon.identity.event.event.Event;
import org.wso2.carbon.identity.event.handler.AbstractEventHandler;
import org.wso2.carbon.identity.retryable.provisioning.service.RetryableProvisioningManager;
import org.wso2.carbon.identity.retryable.provisioning.service.impl.RetryableProvisioningManagerImpl;

import java.util.Map;

/**
 * The event handler class for handling Provisioning status events.
 */
public class ProvisioningStatusHandler extends AbstractEventHandler {

    private static final Log log = LogFactory.getLog(ProvisioningStatusHandler.class);
    private static final String HANDLER_NAME = "provisioningStatusHandler";
    public static final Integer PRIORITY = 10;

    @Override
    public String getName() {
        return HANDLER_NAME;
    }

    @Override
    public void init(InitConfig configuration) {
        super.init(configuration);
    }

    @Override
    public int getPriority(MessageContext messageContext) {
        return PRIORITY;
    }

    /**
     * The method for handling the event. In this method, the received event will be processed and persisted.
     *
     * @param event : The event published by the Outbound Connector.
     * @throws IdentityEventException :
     */
    @Override
    public void handleEvent(Event event) throws IdentityEventException {

        if (log.isDebugEnabled()) {
            log.debug(String.format("The event %s is received.", event.getEventName()));
        }

        //Extract the properties from the received event.
        Map<String, Object> properties = event.getEventProperties();

        RetryableProvisioningManager retryableProvisioningManager = new RetryableProvisioningManagerImpl();
        retryableProvisioningManager.processEvent(properties);
    }
}
