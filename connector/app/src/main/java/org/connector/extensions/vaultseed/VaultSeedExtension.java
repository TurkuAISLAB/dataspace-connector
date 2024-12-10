package org.wccsconnector.extensions.vaultseed;

import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;

import java.lang.System;


public class VaultSeedExtension implements ServiceExtension {

    @Inject
    private Vault vault;

    @Setting(value = "The filepath to the used Connector certificate")
    private final String certificateFilePath = "edc.connector.certificate";
    
    @Setting(value = "The filepath to the used Connector private key")
    private final String privateKeyFilePath = "edc.connector.privatekey";

    @Override
    public void initialize(ServiceExtensionContext context) {
        
        String privateKeyPath = context.getSetting(privateKeyFilePath, null);
        String certificatePath = context.getSetting(certificateFilePath, null);

        String PRIVATE_KEY = FileReaderWrapper.getFileData(privateKeyPath);
        String CERTIFICATE = FileReaderWrapper.getFileData(certificatePath);

        vault.storeSecret("public-key",CERTIFICATE);
        vault.storeSecret("private-key",PRIVATE_KEY);
    }
}