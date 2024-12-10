package org.wccsconnector.extensions.datatransfer;

import org.eclipse.edc.connector.controlplane.transfer.spi.provision.Provisioner;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.DeprovisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionResponse;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedResource;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.StatusResult;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;



public class AuthHeadersProvisioner implements Provisioner<AuthHeadersResourceDefinition, AuthHeadersProvisionedResource> {

    @Override
    public boolean canProvision(ResourceDefinition resourceDefinition) {
        return resourceDefinition instanceof AuthHeadersResourceDefinition;
    }

    @Override
    public boolean canDeprovision(ProvisionedResource provisionedResource) {
        return provisionedResource instanceof AuthHeadersProvisionedResource;
    }

    @Override
    public CompletableFuture<StatusResult<ProvisionResponse>> provision(AuthHeadersResourceDefinition resourceDefinition, Policy policy) {

        var address =
                HttpDataAddress.Builder.newInstance()
                        .copyFrom(resourceDefinition.getDataAddress())
                        .addAdditionalHeader("Dataspace-Requester", resourceDefinition.getBpn())
                        .addAdditionalHeader("Token", resourceDefinition.getToken())
                        .build();

        var provisioned = AuthHeadersProvisionedResource.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .resourceDefinitionId(resourceDefinition.getId())
                .transferProcessId(resourceDefinition.getTransferProcessId())
                .dataAddress(address)
                .resourceName(UUID.randomUUID().toString())
                .hasToken(false)
                .build();

        var response = ProvisionResponse.Builder.newInstance().resource(provisioned).build();
        var result = StatusResult.success(response);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<StatusResult<DeprovisionedResource>> deprovision(
            AuthHeadersProvisionedResource resource, Policy policy) {
        return CompletableFuture.completedFuture(StatusResult.success(DeprovisionedResource.Builder.newInstance().provisionedResourceId(resource.getId()).build())); // nothing to deprovision
    }
}