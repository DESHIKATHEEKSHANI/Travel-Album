package edu.icet.service.impl.storage;

import edu.icet.entity.Image;
import edu.icet.repository.ImageRepository;
import edu.icet.service.SupabaseService;
import edu.icet.dto.ImageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupabaseServiceImpl implements SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseApiKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final ImageRepository imageRepository;
    private final RestTemplate restTemplate;

    @Override
    public ImageResponseDTO uploadImage(MultipartFile file, String username) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID() + fileExtension;

        // Ensure proper URL formatting for Supabase Storage API
        String uploadUrl = supabaseUrl;
        if (!uploadUrl.endsWith("/")) {
            uploadUrl += "/";
        }
        uploadUrl += "storage/v1/object/" + bucketName + "/" + fileName;

        log.info("Uploading to URL: {}", uploadUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        // Set the API key in the correct format required by Supabase
        headers.set("Authorization", "Bearer " + supabaseApiKey);
        headers.set("apikey", supabaseApiKey);
        // Add cache control to prevent caching issues
        headers.setCacheControl("no-cache");

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        try {
            log.info("Sending request to Supabase with headers: {}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            log.info("Received response: {}", response.getStatusCode());

            if (response.getStatusCode().is2xxSuccessful()) {
                // Construct the public URL correctly
                String publicUrl = supabaseUrl;
                if (!publicUrl.endsWith("/")) {
                    publicUrl += "/";
                }
                publicUrl += "storage/v1/object/public/" + bucketName + "/" + fileName;

                log.info("File uploaded successfully. Public URL: {}", publicUrl);

                // Save image metadata in MongoDB
                Image image = new Image(originalFilename, publicUrl, new Date(), username);
                Image savedImage = imageRepository.save(image);

                // Return DTO with image information
                return new ImageResponseDTO(
                        savedImage.getId(),
                        savedImage.getFilename(),
                        savedImage.getUrl(),
                        savedImage.getUploadDate(),
                        savedImage.getUploadedBy()
                );
            } else {
                log.error("Failed to upload image. Status code: {}", response.getStatusCode());
                throw new RuntimeException("Failed to upload image to Supabase. Status Code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error during image upload", e);
            throw new RuntimeException("Error during image upload: " + e.getMessage(), e);
        }
    }
}