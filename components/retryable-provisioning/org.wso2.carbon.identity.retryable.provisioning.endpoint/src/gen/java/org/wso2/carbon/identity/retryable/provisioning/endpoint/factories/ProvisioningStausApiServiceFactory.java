package org.wso2.carbon.identity.retryable.provisioning.endpoint.factories;

import org.wso2.carbon.identity.retryable.provisioning.endpoint.ProvisioningStatusApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.impl.ProvisioningStatusApiServiceImpl;

public class ProvisioningStausApiServiceFactory {

   private final static ProvisioningStatusApiService service = new ProvisioningStatusApiServiceImpl();

   public static ProvisioningStatusApiService getProvisioningStausApi()
   {
      return service;
   }
}
