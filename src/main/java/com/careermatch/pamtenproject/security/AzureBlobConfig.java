package com.careermatch.pamtenproject.security;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {

    @Bean
    public BlobServiceClient blobServiceClient(
            @Value("${azure.storage.account-name}") String accountName,
            @Value("${azure.storage.account-key}") String accountKey,
            @Value("${azure.storage.endpoint}") String endpoint
    ) {
        String connectionString = String.format(
            "DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net",
            accountName, accountKey
        );

        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .endpoint(endpoint)
                .buildClient();
    }
}
