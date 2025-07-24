package com.careermatch.pamtenproject.security;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class GcsConfig {
    @Bean
    public Storage storage() throws IOException {
        // Loads the service account JSON from src/main/resources
        InputStream serviceAccountStream = getClass().getResourceAsStream("/recruitedge-resumes.json");
        return StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(serviceAccountStream))
                .build()
                .getService();
    }
}