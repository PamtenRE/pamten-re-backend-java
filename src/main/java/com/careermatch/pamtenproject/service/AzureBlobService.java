package com.careermatch.pamtenproject.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobUrlParts;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.time.OffsetDateTime;

@Service
public class AzureBlobService {

    private final BlobServiceClient blobServiceClient;
    private final String containerName;
    private final int sasTtlMinutes;

    public AzureBlobService(
            BlobServiceClient blobServiceClient,
            @Value("${azure.storage.container}") String containerName,
            @Value("${azure.storage.sas.ttl-minutes:10}") int sasTtlMinutes
    ) {
        this.blobServiceClient = blobServiceClient;
        this.containerName = containerName;
        this.sasTtlMinutes = sasTtlMinutes;

        // Ensure container exists
        BlobContainerClient c = blobServiceClient.getBlobContainerClient(containerName);
        if (!c.exists()) {
            c.create();
        }
    }

    /**
     * Uploads a multipart file to Azure Blob Storage at the given blobPath.
     * Returns the absolute blob URL (no SAS).
     */
    public String upload(MultipartFile file, String blobPath) throws Exception {
        BlobContainerClient container = blobServiceClient.getBlobContainerClient(containerName);
        BlobClient blob = container.getBlobClient(blobPath);

        try (InputStream in = file.getInputStream()) {
            blob.upload(in, file.getSize(), true);

            // Set headers so downloads open with correct type/filename
            BlobHttpHeaders headers = new BlobHttpHeaders();
            if (file.getContentType() != null) {
                headers.setContentType(file.getContentType());
            }
            headers.setContentDisposition("inline; filename=\"" + file.getOriginalFilename() + "\"");
            blob.setHttpHeaders(headers);
        }

        return blob.getBlobUrl();
    }

    /**
     * Generates a read-only SAS URL for the given absolute blob URL, valid for sasTtlMinutes.
     */
    public String generateReadSasUrl(String blobUrl) {
        try {
            // Parse container and blob name from the absolute URL
            BlobUrlParts parts = BlobUrlParts.parse(new URL(blobUrl));
            String containerName = parts.getBlobContainerName();
            String blobName = parts.getBlobName();

            // Recreate a credentialed BlobClient from our BlobServiceClient
            BlobClient blob = blobServiceClient
                    .getBlobContainerClient(containerName)
                    .getBlobClient(blobName);

            // Build read-only SAS with configured TTL
            BlobSasPermission perm = new BlobSasPermission().setReadPermission(true);
            BlobServiceSasSignatureValues vals = new BlobServiceSasSignatureValues(
                    OffsetDateTime.now().plusMinutes(sasTtlMinutes),
                    perm
            );

            String sas = blob.generateSas(vals);
            return blob.getBlobUrl() + "?" + sas;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SAS URL: " + e.getMessage(), e);
        }
    }
}
