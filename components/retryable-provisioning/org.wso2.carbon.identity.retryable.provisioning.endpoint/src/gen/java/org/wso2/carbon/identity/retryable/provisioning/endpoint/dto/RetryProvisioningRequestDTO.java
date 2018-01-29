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
package org.wso2.carbon.identity.retryable.provisioning.endpoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;


@ApiModel(description = "")
public class RetryProvisioningRequestDTO {


    private List<Integer> provisioningIds = new ArrayList<Integer>();


    /**
     **/
    @ApiModelProperty(value = "")
    @JsonProperty("provisioningIds")
    public List<Integer> getProvisioningIds() {
        return provisioningIds;
    }

    public void setProvisioningIds(List<Integer> provisioningIds) {
        this.provisioningIds = provisioningIds;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class RetryProvisioningRequestDTO {\n");

        sb.append("  provisioningIds: ").append(provisioningIds).append("\n");
        sb.append("}\n");
        return sb.toString();
    }
}
