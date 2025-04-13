package dev.dmgiangi.fido2_poc;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Fido2PocConfiguration {
    @Bean
    public BlobContainerClient blobServiceClient() {
        final var blobServiceClient = new BlobServiceClientBuilder()
                .credential(new DefaultAzureCredentialBuilder().build())
                .endpoint("https://stportfolio.blob.core.windows.net")
                .buildClient();

        return blobServiceClient
                .createBlobContainerIfNotExists("fido2");
    }
}
