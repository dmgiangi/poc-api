package dev.dmgiangi.fido2_poc.configuration;

import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class Fido2PocConfiguration {
    @Bean
    @Profile("default")
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .credential(new DefaultAzureCredentialBuilder().build())
                .endpoint("https://stportfolio.blob.core.windows.net")
                .buildClient();
    }

    @Bean
    @Profile("default")
    public TableServiceClient tableServiceClient() {
        return new TableServiceClientBuilder()
                .credential(new DefaultAzureCredentialBuilder().build())
                .endpoint("https://stportfolio.table.core.windows.net")
                .buildClient();
    }

    @Bean
    @Profile("local")
    public BlobServiceClient localBlobServiceClient(
            @Value("${AZURE_STORAGE_ACCOUNT_CONNECTION_STRING}") String connectionString) {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    @Bean
    @Profile("local")
    public TableServiceClient localTableServiceClient(
            @Value("${AZURE_STORAGE_ACCOUNT_CONNECTION_STRING}") String connectionString) {
        return new TableServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
