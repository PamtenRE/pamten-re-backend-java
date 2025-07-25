package com.careermatch.pamtenproject.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class GcsService {
    private final Storage storage;
    private final String bucketName = "recruitedge-resumes"; // <-- Replace with your actual bucket name

    public GcsService(Storage storage) {
        this.storage = storage;
    }

    public String uploadFile(MultipartFile file, Integer candidateId) throws IOException {
        try {
            // Create a unique object name for the file in the bucket
            String timestamp = String.valueOf(System.currentTimeMillis());
            String objectName = "resumes/" + candidateId + "/" + timestamp + "_" + file.getOriginalFilename();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                    .setContentType(file.getContentType())
                    .build();

            storage.create(blobInfo, file.getBytes());

            // Return the GCS path
            return String.format("gs://%s/%s", bucketName, objectName);
        } catch (Exception e) {
            throw new IOException("Failed to upload file to Google Cloud Storage: " + e.getMessage(), e);
        }
    }
    
}