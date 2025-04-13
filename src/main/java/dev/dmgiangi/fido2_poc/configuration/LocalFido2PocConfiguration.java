package dev.dmgiangi.fido2_poc.configuration;

import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalFido2PocConfiguration {
    @Bean
    public BlobServiceClient blobServiceClient(
            @Value("${STORAGE_ACCOUNT_CONNECTION_STRING}") String connectionString) {
        final var blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        blobServiceClient.createBlobContainerIfNotExists("fido2").createIfNotExists();

        return blobServiceClient;
    }

    @Bean
    public TableServiceClient tableServiceClient(
            @Value("${STORAGE_ACCOUNT_CONNECTION_STRING}") String connectionString) {
        final var tableServiceClient = new TableServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        tableServiceClient.getTableClient("user");

        return tableServiceClient;
    }
}
