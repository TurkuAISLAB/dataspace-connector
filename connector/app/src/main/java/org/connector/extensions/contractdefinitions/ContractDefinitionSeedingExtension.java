package org.wccsconnector.extensions.contractdefinitions;

import org.eclipse.edc.api.model.IdResponse;
import org.eclipse.edc.connector.controlplane.services.spi.contractdefinition.ContractDefinitionService;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractDefinition;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

import static java.lang.String.format;
import java.lang.System;
import java.util.ArrayList;


public class ContractDefinitionSeedingExtension implements ServiceExtension {
    private final String NAME = "WCCS RDP Contract Definition Seeding Extension";

    @Override
    public String name() {
        return NAME;
    }

    @Inject
    ContractDefinitionService contractDefinitionService;

    @Override
    public void initialize(ServiceExtensionContext context) {

        Monitor monitor = context.getMonitor();
        
        String targetContractDefinitionId = "wccs-rdp-all-allowed-contract";

        ContractDefinition defaultContractDefinition = ContractDefinition.Builder.newInstance()
            .id(targetContractDefinitionId)
            .accessPolicyId("data-platform-all-allowed-policy")
            .contractPolicyId("data-platform-all-allowed-policy")
            .assetsSelector(new ArrayList<Criterion>())
            .build();


        var contractDefinitionCreated = contractDefinitionService.create(defaultContractDefinition)
                .map(contractDefinition -> IdResponse.Builder.newInstance()
                        .id(contractDefinition.getId())
                        .createdAt(contractDefinition.getCreatedAt())
                        .build())
                .orElseThrow(exceptionMapper(ContractDefinition.class));

        monitor.debug(String.format("%s created Contract Definition: %s", NAME, targetContractDefinitionId));
    }

}