package dev.dmgiangi.fido2_poc.controller;

import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.models.TableServiceStatistics;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobServiceStatistics;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    private final BlobServiceClient blobServiceClient;
    private final TableServiceClient tableServiceClient;

    public HealthController(BlobServiceClient blobServiceClient,
                            TableServiceClient tableServiceClient) {
        this.blobServiceClient = blobServiceClient;
        this.tableServiceClient = tableServiceClient;
    }

    @GetMapping
    public Health health() {
        final var blobStatistics = blobServiceClient.getStatistics();
        final var tableStatistics = tableServiceClient.getStatistics();
        return new Health(blobStatistics, tableStatistics);
    }

    public record Health(BlobServiceStatistics blobServiceClient,
                         TableServiceStatistics tableServiceClient) {
    }
}
