package org.wccsconnector.extensions.datatransfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ResourceDefinition;
import org.eclipse.edc.spi.types.domain.DataAddress;



@JsonDeserialize(builder = AuthHeadersResourceDefinition.Builder.class)
@JsonTypeName("dataspaceconnector:authheadersresourcedefinition")
class AuthHeadersResourceDefinition extends ResourceDefinition {

    private String contractId;
    private DataAddress dataAddress;
    private String bpn;
    private String token;

    @Override
    public Builder toBuilder() {
        return initializeBuilder(new Builder());
    }

    public DataAddress getDataAddress() {
        return dataAddress;
    }

    public String getContractId() {
        return contractId;
    }

    public String getToken(){
        return token;
    }

    public String getBpn() {
        return bpn;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder
            extends ResourceDefinition.Builder<AuthHeadersResourceDefinition, Builder> {

        protected Builder() {
            super(new AuthHeadersResourceDefinition());
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder contractId(String contractId) {
            resourceDefinition.contractId = contractId;
            return this;
        }

        public Builder dataAddress(DataAddress dataAddress) {
            resourceDefinition.dataAddress = dataAddress;
            return this;
        }

        public Builder bpn(String bpn) {
            resourceDefinition.bpn = bpn;
            return this;
        }
        
        public Builder token(String token){
            resourceDefinition.token = token;
            return this;
        }
    }
}