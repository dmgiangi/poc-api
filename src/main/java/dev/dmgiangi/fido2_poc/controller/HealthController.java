package dev.dmgiangi.fido2_poc.controller;

import com.azure.data.tables.TableServiceClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class HealthController {
    private final BlobContainerClient fido2ContainerClient;
    private final TableServiceClient tableServiceClient;

    public HealthController(BlobServiceClient blobServiceClient,
                            TableServiceClient tableServiceClient) {
        this.fido2ContainerClient = blobServiceClient.getBlobContainerClient("fido2");

        this.tableServiceClient = tableServiceClient;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/ready")
    public Health ready() {
        return new Health(
                fido2ContainerClient.exists(),
                tableServiceClient.listTables().iterator().hasNext());
    }

    public record Health(boolean blobConnected,
                         boolean tableConnected) {
    }
}
