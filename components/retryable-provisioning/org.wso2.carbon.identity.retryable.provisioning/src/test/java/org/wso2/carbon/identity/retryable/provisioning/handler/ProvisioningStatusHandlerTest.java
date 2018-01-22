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

public class ProvisioningStatusHandlerTest extends AbstractEventHandler {

    private static final Log log = LogFactory.getLog(ProvisioningStatusHandlerTest.class);
    private static final String HANDLER_NAME = "provisioningStatusHandler";
    private static final String FRIENDLY_NAME = "Provisioning Status Handler";

    @Override
    public String getName() {
        return HANDLER_NAME;
    }

    public String getFriendlyName() {
        return FRIENDLY_NAME;
    }

    @Override
    public void init(InitConfig configuration) {
        super.init(configuration);
    }

    @Override
    public int getPriority(MessageContext messageContext) {
        return 10;
    }

    @Override
    public void handleEvent(Event event) throws IdentityEventException {
        log.info("Event received.....");

        log.info(event.getEventName());
    }
}
