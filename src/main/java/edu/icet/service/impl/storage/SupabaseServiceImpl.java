package edu.icet.service.impl.storage;

import edu.icet.entity.Image;
import edu.icet.repository.ImageRepository;
import edu.icet.service.SupabaseService;
import edu.icet.dto.ImageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

        // Ensure correct Supabase Storage URL
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, fileName);

        log.info("Uploading to URL: {}", uploadUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(file.getContentType()));
        headers.set("Authorization", "Bearer " + supabaseApiKey);
        headers.set("apikey", supabaseApiKey);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                uploadUrl,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, fileName);
            Image image = new Image(originalFilename, publicUrl, new Date(), username);
            Image savedImage = imageRepository.save(image);

            return new ImageResponseDTO(
                    savedImage.getId(),
                    savedImage.getFilename(),
                    savedImage.getUrl(),
                    savedImage.getUploadDate(),
                    savedImage.getUploadedBy()
            );
        } else {
            throw new RuntimeException("Upload failed with status code: " + response.getStatusCode());
        }
    }
}
