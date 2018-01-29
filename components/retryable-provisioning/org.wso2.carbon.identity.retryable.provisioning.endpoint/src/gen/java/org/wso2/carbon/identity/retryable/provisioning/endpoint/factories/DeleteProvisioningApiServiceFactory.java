package org.wso2.carbon.identity.retryable.provisioning.endpoint.factories;

import org.wso2.carbon.identity.retryable.provisioning.endpoint.DeleteProvisioningApiService;
import org.wso2.carbon.identity.retryable.provisioning.endpoint.impl.DeleteProvisioningApiServiceImpl;

public class DeleteProvisioningApiServiceFactory {

   private final static DeleteProvisioningApiService service = new DeleteProvisioningApiServiceImpl();

   public static DeleteProvisioningApiService getDeleteProvisioningApi()
   {
      return service;
   }
}
