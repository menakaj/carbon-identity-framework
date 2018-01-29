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
package org.wso2.carbon.identity.retryable.provisioning.util;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;
import org.wso2.carbon.identity.retryable.provisioning.testUtils.TestUtils;

/**
 * Test cases for ProvisioningEntity to json conversion and json to ProvisioningEntity conversion.
 */
public class ProvisioningEntityToJsonConverterTest {

    private ProvisioningEntity provisioningEntity = null;
    private ProvisioningEntityToJsonConverter provisioningEntityToJsonConverter = new
            ProvisioningEntityToJsonConverter();

    @BeforeClass
    public void setup() {
        provisioningEntity = TestUtils.generateProvisioningEntity();
    }

    @Test(description = "Test converting ProvisioningEntity object to Json string")
    public void testConvertToJson() {
        String jsonString = provisioningEntityToJsonConverter.convertToJson(provisioningEntity);
        Assert.assertNotNull(jsonString);
    }

    @Test(description = "Test conversion of the given json string to ProvisioningEntity object.")
    public void testConvertToProvisioningEntity() {
        ProvisioningEntity provisioningEntityGen = provisioningEntityToJsonConverter.convertFromJson(TestUtils
                .getJSONOutput());
        Assert.assertEquals(provisioningEntityGen.getEntityName(), provisioningEntity.getEntityName());
        Assert.assertEquals(provisioningEntityGen.getEntityType(), provisioningEntity.getEntityType());
        Assert.assertEquals(provisioningEntityGen.getOperation(), provisioningEntity.getOperation());
        Assert.assertEquals(provisioningEntityGen.getAttributes(), provisioningEntity.getAttributes());
    }

}
