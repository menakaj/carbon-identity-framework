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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;
import org.wso2.carbon.identity.provisioning.ProvisionedIdentifier;
import org.wso2.carbon.identity.provisioning.ProvisioningEntity;
import org.wso2.carbon.identity.provisioning.ProvisioningEntityType;
import org.wso2.carbon.identity.provisioning.ProvisioningOperation;
import org.wso2.carbon.identity.retryable.provisioning.beans.SerializableProvisioningEntity;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to convert Java object to Json string for storage purposes.
 * This class specifically converts, ProvisioningEntity class to Json String and vice versa.
 * ProvisioningEntity has following class definition.
 *
 *      ProvisioningEntityType entityType
 *      ProvisioningOperation operation
 *      ProvisionedIdentifier identifier
 *      String entityName
 *      boolean jitProvisioning
 *      Map<ClaimMapping, List<String>> attributes
 *      Map<String, String> inboundAttributes
 *
 * When converting this class, the attributes map could not be directly convertible to a JSON String because of the
 * ClaimMapping object is a complex object.
 *
 * So, here we separately convert the ClaimMapping objects to string and build a Map<String, String> for attributes.
 *
 * When converting the string back to the original Java object, we separately convert the above map to original
 * ClaimMapping map.
 */
public class ProvisioningEntityToJsonConverter {

    private static final Log log = LogFactory.getLog(ProvisioningEntityToJsonConverter.class);
    private Gson gson = new Gson();

    public ProvisioningEntityToJsonConverter() {
        //Empty constructor
    }

    /**
     * Convert the provided ProvisioningEntity Object to json string. This json string will be persisted in the
     * database to reattempt the failed provisioning attempts.
     *
     * @param provisioningEntity : The ProvisioningEntity object that needs to be converted.
     * @return : Json string.
     */
    public String convertToJson(ProvisioningEntity provisioningEntity) {

        if (log.isDebugEnabled()) {
            log.debug("Converting ProvisioningEntity to json");
        }

        // 1. Generate json string of the attributes. (which is a Map<ClaimMapping, List<String>>).
        String attributes = buildClaimMappingJson(provisioningEntity.getAttributes());

        // 2. Generate a json string of the inbound attributes.
        String inboundAttributes = gson.toJson(provisioningEntity.getInboundAttributes());

        // 3. Extract each variable from provisioning entity.
        ProvisioningEntityType type = provisioningEntity.getEntityType();
        ProvisioningOperation operation = provisioningEntity.getOperation();
        ProvisionedIdentifier identifier = provisioningEntity.getIdentifier();
        String entityName = provisioningEntity.getEntityName();
        boolean jitProvisioning = provisioningEntity.isJitProvisioning();

        // 4. Create new version of provisioningEntity with the above values.
        //
        // The main reason is, after converting  the original object to a json, it could not be converted back to the
        // java object due to complex objects. In steps 1 and 2, we simplify those complex objects and assign them
        // into the new object. This will then converted into json string.
        SerializableProvisioningEntity serialozableProvisioningEntity = new SerializableProvisioningEntity();
        serialozableProvisioningEntity.setAttributes(attributes);
        serialozableProvisioningEntity.setEntityName(entityName);
        serialozableProvisioningEntity.setEntityType(type);
        serialozableProvisioningEntity.setIdentifier(identifier);
        serialozableProvisioningEntity.setJitProvisioning(jitProvisioning);
        serialozableProvisioningEntity.setOperation(operation);
        serialozableProvisioningEntity.setInboundAttributes(inboundAttributes);

        return gson.toJson(serialozableProvisioningEntity);
    }

    /**
     * Convert the given json string to ProvisioningEntity object.
     *
     * @param jsonString : The json string that needs to be converted.
     * @return : The converted Provisioning Entity object.
     */
    public ProvisioningEntity convertFromJson(String jsonString) {

        // 1. Convert the json string to the new representation of the ProvisioningEntity class.
        SerializableProvisioningEntity serialozableProvisioningEntity = gson.fromJson(jsonString,
                SerializableProvisioningEntity.class);

        // 2. Convert the Json string of attributes to its original Map.
        Map<ClaimMapping, List<String>> attributes = buildClaimMappingMap(serialozableProvisioningEntity
                .getAttributes());

        // 3. Convert the json string of Inbound attributes to it's original map.
        Map<String, String> inboundAttributes = convertJsonToStringMap(serialozableProvisioningEntity.getInboundAttributes());

        // 4. Extract each of the converted parameter
        ProvisioningEntityType type = serialozableProvisioningEntity.getEntityType();
        ProvisioningOperation operation = serialozableProvisioningEntity.getOperation();
        ProvisionedIdentifier identifier = serialozableProvisioningEntity.getIdentifier();
        String entityName = serialozableProvisioningEntity.getEntityName();
        boolean jitProvisioning = serialozableProvisioningEntity.isJitProvisioning();

        // 5. Create an instance of the original ProvisioningEntity object with the parameters.
        ProvisioningEntity provisioningEntity = new ProvisioningEntity(type, operation, attributes);
        provisioningEntity.setIdentifier(identifier);
        provisioningEntity.setInboundAttributes(inboundAttributes);
        provisioningEntity.setJitProvisioning(jitProvisioning);
        provisioningEntity.setEntityName(entityName);

        return provisioningEntity;
    }

    /**
     * Generate the original form of claim mapping map from the provided json string.
     *
     * @param jsonMap : Json string that is needs to be converted.
     * @return : Map<ClaimMapping, List<String>> object.
     */
    private Map<ClaimMapping, List<String>> buildClaimMappingMap(String jsonMap) {
        // Define the type of object that needs to be returned from the conversion.
        Type listType = new TypeToken<List<String>>() {}.getType();

        // Create a simple String map from the given string.
        Map<String, String> claimMappingStringMap = convertJsonToStringMap(jsonMap);

        Map<ClaimMapping, List<String>> claimMappingListMap = new HashMap<>();

        for (Map.Entry<String, String> entry : claimMappingStringMap.entrySet()) {
            ClaimMapping claimMapping = convertJsonToClaimMapping(entry.getKey());
            List<String> list = gson.fromJson(entry.getValue(), listType);
            claimMappingListMap.put(claimMapping, list);
        }
        return claimMappingListMap;
    }

    /**
     * Convert the given ClaimMapping map to a json string.
     *
     * First, the ClaimMapping objects and string lists are converted to json strings separately and put in to a
     * String map. (To preserve the original mapping)
     *
     * Then this Map<String, String> is converted to a Json string.
     *
     * @param claimMappingListMap : The Map<ClaimMapping, List<String>> object that needs to be converted.
     * @return : Json string.
     */
    private String buildClaimMappingJson(Map<ClaimMapping, List<String>> claimMappingListMap) {
        Map<String, String> claimStringMap = new HashMap<>();

        for (Map.Entry<ClaimMapping, List<String>> entry : claimMappingListMap.entrySet()) {
            String claimMappingString = convertClaimMappingToJson(entry.getKey());
            String list = gson.toJson(entry.getValue());

            claimStringMap.put(claimMappingString, list);
        }
        return gson.toJson(claimStringMap);
    }

    /**
     * Convert the given Json representation of a String Map to it's object form.
     *
     * @param jsonString : The Json string that needs to be converted.
     * @return : Map<String, String>
     */
    private Map<String, String> convertJsonToStringMap(String jsonString) {
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(jsonString, type);
    }

    /**
     * Convert ClaimMapping object to Json String.
     *
     * @param claimMapping : The ClaimMapping object that needs to be converted.
     * @return : Json String
     */
    private String convertClaimMappingToJson(ClaimMapping claimMapping) {
        return gson.toJson(claimMapping);
    }

    /**
     * Convert given string representation of ClaimMapping to it's original class.
     *
     * @param claimMappingString : The Json string that needs to be converted.
     * @return : ClaimMapping object.
     */
    private ClaimMapping convertJsonToClaimMapping(String claimMappingString) {
        return gson.fromJson(claimMappingString, ClaimMapping.class);
    }
}
