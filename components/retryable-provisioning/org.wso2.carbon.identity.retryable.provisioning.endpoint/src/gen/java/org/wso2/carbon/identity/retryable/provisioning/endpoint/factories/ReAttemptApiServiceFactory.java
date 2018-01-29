package org.wso2.carbon.identity.retryable.provisioning.endpoint.factories;

import org.wso2.carbon.identity.retryable.provisioning.endpoint.ReAttemptProvisioningApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.impl.ReAttemptProvisioningApiServiceImpl;

public class ReAttemptApiServiceFactory {

   private final static ReAttemptProvisioningApiService service = new ReAttemptProvisioningApiServiceImpl();

   public static ReAttemptProvisioningApiService getReAttemptApi()
   {
      return service;
   }
}
