package org.wccsconnector.extensions.datatransfer;

import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ProvisionManager;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ResourceManifestGenerator;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;

public class AuthHeadersProvisionExtension implements ServiceExtension {

    @Inject
    private ResourceManifestGenerator resourceManifestGenerator;

    @Inject
    private ProvisionManager provisionManager;

    @Inject
    private TypeManager typeManager;

    @Inject
    private ContractAgreementService contractAgreementService;

    @Override
    public void initialize(ServiceExtensionContext context) {
        typeManager.registerTypes(AuthHeadersResourceDefinition.class, AuthHeadersProvisionedResource.class);
        resourceManifestGenerator.registerGenerator(new AuthHeadersResourceDefinitionGenerator(contractAgreementService, context));
        provisionManager.register(new AuthHeadersProvisioner());
    }
}