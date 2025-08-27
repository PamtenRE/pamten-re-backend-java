package com.careermatch.pamtenproject.security;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class GcsConfig {

    @Bean
    public Storage storage() throws IOException {
        try {
            log.info("Initializing GCS Storage...");

            // Loads the service account JSON from src/main/resources
            InputStream serviceAccountStream = getClass().getResourceAsStream("/recruitedge-resumes.json");

            if (serviceAccountStream == null) {
                throw new IOException("Service account key file not found: /recruitedge-resumes.json");
            }

            // Create credentials from service account
            ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);

            log.info("Service account credentials loaded for: {}", credentials.getClientEmail());

            // Build storage
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .build()
                    .getService();

            log.info("GCS Storage initialized successfully");
            return storage;

        } catch (Exception e) {
            log.error("Failed to initialize GCS Storage: {}", e.getMessage(), e);
            throw new IOException("Failed to initialize GCS Storage: " + e.getMessage(), e);
        }
    }
}