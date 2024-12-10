package org.wccsconnector.extensions.datatransfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.ProvisionedContentResource;

@JsonDeserialize(builder = AuthHeadersProvisionedResource.Builder.class)
class AuthHeadersProvisionedResource extends ProvisionedContentResource {

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder
            extends ProvisionedContentResource.Builder<AuthHeadersProvisionedResource, Builder> {

        private Builder() {
            super(new AuthHeadersProvisionedResource());
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }
    }
}