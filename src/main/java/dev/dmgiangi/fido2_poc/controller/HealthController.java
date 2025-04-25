package dev.dmgiangi.fido2_poc.controller;

import com.azure.data.tables.TableServiceClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import dev.dmgiangi.fido2_poc.service.GraphService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping
public class HealthController {
    private final BlobContainerClient fido2ContainerClient;
    private final TableServiceClient tableServiceClient;
    private final GraphService graphService;
    private final DataSource dataSource;

    public HealthController(BlobServiceClient blobServiceClient,
                            TableServiceClient tableServiceClient,
                            GraphService graphService,
                            DataSource dataSource) {
        this.fido2ContainerClient = blobServiceClient.getBlobContainerClient("fido2");
        this.tableServiceClient = tableServiceClient;
        this.graphService = graphService;
        this.dataSource = dataSource;
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/ready")
    public Health ready() {
        return new Health(
                fido2ContainerClient.exists(),
                tableServiceClient.listTables().iterator().hasNext(),
                graphService.checkConnection(),
                this.isDatabaseReachable());
    }

    public boolean isDatabaseReachable() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(30);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    public record Health(boolean blobConnected,
                         boolean tableConnected,
                         boolean graphConnected,
                         boolean databaseConnected) {
    }
}
