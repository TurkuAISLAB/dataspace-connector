package org.wccsconnector.extensions.datatransfer;

import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreement;
import org.eclipse.edc.connector.controlplane.services.spi.contractagreement.ContractAgreementService;
import org.eclipse.edc.connector.controlplane.transfer.spi.provision.ProviderResourceDefinitionGenerator;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

class AuthHeadersResourceDefinitionGenerator implements ProviderResourceDefinitionGenerator {

    private final ContractAgreementService contractAgreementService;
    private ServiceExtensionContext context;

    @Setting(value = "Data Platform Realm")
    private final String DataPlatformRealmSetting = "wccs.dataplatform.realm";

    @Setting(value = "Identity Provider Token URL")
    private final String DataPlatformTokenUrlSetting = "wccs.dataplatform.idp.token.url";

    @Setting(value = "Data Platform Client ID")
    private final String DataPlatformClientIdSetting = "wccs.dataplatform.clientid";

    @Setting(value = "Data Platform Client Secret")
    private final String DataPlatformClientSecretSetting = "wccs.dataplatform.clientsecret";

    AuthHeadersResourceDefinitionGenerator(ContractAgreementService contractAgreementService, ServiceExtensionContext context) {
        this.contractAgreementService = contractAgreementService;
        this.context = context;
    }

    @Override
    public @Nullable ResourceDefinition generate(TransferProcess transferProcess, DataAddress dataAddress, Policy policy) {
        var bpn = Optional.of(transferProcess.getContractId())
                .map(contractAgreementService::findById)
                .map(ContractAgreement::getConsumerId)
                .orElse(null);

        String clientID = this.context.getSetting(DataPlatformClientIdSetting, null);
        String clientSecret = this.context.getSetting(DataPlatformClientSecretSetting, null);
        String realm = this.context.getSetting(DataPlatformRealmSetting, null);
        String identityProviderTokenUrl = this.context.getSetting(DataPlatformTokenUrlSetting, null);
        String token = DataPlatformIamAdapter.getToken(identityProviderTokenUrl, clientID, clientSecret);

        return AuthHeadersResourceDefinition.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .dataAddress(dataAddress)
                .contractId(transferProcess.getContractId())
                .token(token)
                .bpn(bpn)
                .build();
    }

    @Override
    public boolean canGenerate(TransferProcess transferProcess, DataAddress dataAddress, Policy policy) {
        return "HttpData".equals(dataAddress.getType());
    }
}
