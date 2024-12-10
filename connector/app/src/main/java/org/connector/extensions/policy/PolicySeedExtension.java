package org.wccsconnector.extensions.policy;

import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition;
import org.eclipse.edc.connector.controlplane.services.spi.policydefinition.PolicyDefinitionService;
import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_TYPE;
import org.eclipse.edc.policy.model.Policy;
import static org.eclipse.edc.web.spi.exception.ServiceResultHandler.exceptionMapper;

import jakarta.json.Json;
import jakarta.json.JsonWriter;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import java.lang.System;
import static java.lang.String.format;
import java.util.Map;


public class PolicySeedExtension implements ServiceExtension{

    private final String NAME = "WCCS RDP Policy Seeding Extension";

    @Inject
    private PolicyDefinitionService service;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        Monitor monitor = context.getMonitor();

        Policy policy = Policy.Builder.newInstance().build();

        PolicyDefinition definition = PolicyDefinition.Builder.newInstance()
            .id("data-platform-all-allowed-policy")
            .policy(policy)
            .build();

        var createdDefinition = service.create(definition)
                .onSuccess(d -> monitor.debug(format("WCCS Data Platform Policy Definition created %s", d.getId())))
                .orElseThrow(exceptionMapper(PolicyDefinition.class, definition.getId()));
    }
    
}