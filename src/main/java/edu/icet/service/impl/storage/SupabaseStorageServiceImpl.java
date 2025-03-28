package edu.icet.service.impl.storage;

import edu.icet.service.SupabaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class SupabaseStorageServiceImpl implements SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Supabase Storage Upload URL
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;

            // Set Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.set("Content-Type", file.getContentType());

            // Create Request
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // Send POST Request
            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
            } else {
                throw new RuntimeException("Failed to upload image");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error uploading image: " + e.getMessage());
        }
    }
}
