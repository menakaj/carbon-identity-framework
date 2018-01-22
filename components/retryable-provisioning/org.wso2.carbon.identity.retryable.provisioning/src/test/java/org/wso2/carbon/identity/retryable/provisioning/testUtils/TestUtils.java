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

import org.apache.axis2.databinding.types.xsd.ID;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.common.model.Claim;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;
import org.wso2.carbon.identity.provisioning.ProvisioningEntityType;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningMetadata;
import org.wso2.carbon.identity.retryable.provisioning.beans.ProvisioningStatus;
import org.wso2.carbon.identity.retryable.provisioning.beans.SerializableProvisioningEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {

    private static final String ENTITY_NAME = "test@wso2.com";
    private static final String[] CLAIM_URLs = {"http://wso2.org/claims/givenname",
            "http://wso2.org/claims/emailaddress", "http://wso2.org/claims/otherphone", "http://wso2.org/claims/dob",
            "http://wso2.org/claims/lastname", "http://wso2.org/claims/primaryChallengeQuestion",
            "http://wso2.org/claims/role", "http://wso2.org/claims/challengeQuestion1",
            "http://wso2.org/claims/telephone", "http://wso2.org/claims/mobile", "http://wso2.org/claims/country",
            "http://wso2.org/claims/emailaddress"};

    private static final String[] CLAIM_VALUES = {"Test", "test@wso2.com", "UTF-8", "en_US", "Test", "en_US",
            "00e90000001aV2o", "America/Los_Angeles", "false", "false", "false", "test@wso2.com"};

    private static final String CAUSE = "Created";
    private static final String OPERATION = "POST";
    private static final int IDENTIFIER = 1;
    private static final String ENTITY = "User";
    private static final String IDP_NAME = "test_idp";
    private static final String STATUS = "SUCCESS";

    public static ProvisioningEntity generateProvisioningEntity() {
        ProvisioningEntity provisioningEntity = new ProvisioningEntity(ProvisioningEntityType.USER, org.wso2.carbon
                .identity.provisioning.ProvisioningOperation.POST, generateClaimsMap());
        provisioningEntity.setEntityName(ENTITY_NAME);
        provisioningEntity.setJitProvisioning(false);
        provisioningEntity.setIdentifier(null);
        return provisioningEntity;
    }

    private static Map<ClaimMapping, List<String>> generateClaimsMap() {
        Map<ClaimMapping, List<String>> claimMappingStringMap = new HashMap<>();

        for (int i = 0; i < 11; i++) {
            Claim claim = new Claim();
            claim.setClaimUri(CLAIM_URLs[i]);
            claim.setClaimId(i);

            List<String> claimValue = new ArrayList<>();
            claimValue.add(CLAIM_VALUES[i]);

            ClaimMapping claimMapping = new ClaimMapping();
            claimMapping.setLocalClaim(claim);
            claimMappingStringMap.put(claimMapping, claimValue);
        }
        return claimMappingStringMap;
    }

    public static ProvisioningStatus generateProvisioningStatus() {
        ProvisioningStatus provisioningStatus = new ProvisioningStatus();
        provisioningStatus.setCause(CAUSE);
        provisioningStatus.setOperation(OPERATION);
        provisioningStatus.setIdentifier(IDENTIFIER);
        provisioningStatus.setIdpName(IDP_NAME);
        provisioningStatus.setEntity(ENTITY);
        provisioningStatus.setName(ENTITY_NAME);
        provisioningStatus.setStatus(STATUS);
        return provisioningStatus;
    }

    public static ProvisioningMetadata generateProvisioningMetadata() {
        ProvisioningMetadata provisioningMetadata = new ProvisioningMetadata();
        provisioningMetadata.setTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantId());
        provisioningMetadata.setProvisioningEntity(getJSONOutput());
        provisioningMetadata.setStatusId(IDENTIFIER);
        provisioningMetadata.setIdpName(IDP_NAME);
        provisioningMetadata.setConnectorConfiguration("");
        return provisioningMetadata;
    }

    public static String getJSONOutput() {
        return
                "{\"entityType\":\"USER\",\"operation\":\"POST\",\"entityName\":\"test@wso2.com\",\"jitProvisioning\"" +
                        ":false,\"attributes\":\"{\\\"{\\\\\\\"localClaim\\\\\\\":{\\\\\\\"claimUri\\\\\\\":" +
                        "\\\\\\\"http://wso2.org/claims/mobile\\\\\\\",\\\\\\\"claimId\\\\\\\":9}," +
                        "\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}\\\":" +
                        "\\\"[\\\\\\\"false\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":{\\\\\\\"claimUri\\\\\\\":" +
                        "\\\\\\\"http://wso2.org/claims/emailaddress\\\\\\\",\\\\\\\"claimId\\\\\\\":1}," +
                        "\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}\\\":\\\"" +
                        "[\\\\\\\"test@wso2.com\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/givenname\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":0},\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}" +
                        "\\\":\\\"[\\\\\\\"Test\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/challengeQuestion1\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":7},\\\\\\\"requested\\\\\\\":false," +
                        "\\\\\\\"isMandatory\\\\\\\":false}\\\":\\\"[\\\\\\\"America/Los_Angeles\\\\\\\"]\\\"," +
                        "\\\"{\\\\\\\"localClaim\\\\\\\":{\\\\\\\"claimUri\\\\\\\":" +
                        "\\\\\\\"http://wso2.org/claims/country\\\\\\\",\\\\\\\"claimId\\\\\\\":10}," +
                        "\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}\\\":" +
                        "\\\"[\\\\\\\"false\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":{\\\\\\\"claimUri\\\\\\\":" +
                        "\\\\\\\"http://wso2.org/claims/otherphone\\\\\\\",\\\\\\\"claimId\\\\\\\":2}," +
                        "\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}\\\":" +
                        "\\\"[\\\\\\\"UTF-8\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":{\\\\\\\"claimUri\\\\\\\":" +
                        "\\\\\\\"http://wso2.org/claims/role\\\\\\\",\\\\\\\"claimId\\\\\\\":6}," +
                        "\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}\\\":" +
                        "\\\"[\\\\\\\"00e90000001aV2o\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/lastname\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":4},\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}" +
                        "\\\":\\\"[\\\\\\\"Test\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/primaryChallengeQuestion\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":5},\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":false}" +
                        "\\\":\\\"[\\\\\\\"en_US\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/telephone\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":8},\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":" +
                        "false}\\\":\\\"[\\\\\\\"false\\\\\\\"]\\\",\\\"{\\\\\\\"localClaim\\\\\\\":" +
                        "{\\\\\\\"claimUri\\\\\\\":\\\\\\\"http://wso2.org/claims/dob\\\\\\\"," +
                        "\\\\\\\"claimId\\\\\\\":3},\\\\\\\"requested\\\\\\\":false,\\\\\\\"isMandatory\\\\\\\":" +
                        "false}\\\":\\\"[\\\\\\\"en_US\\\\\\\"]\\\"}\",\"inboundAttributes\":\"null\"}\n";
    }

}
