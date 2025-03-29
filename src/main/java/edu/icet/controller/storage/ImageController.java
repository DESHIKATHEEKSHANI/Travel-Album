package edu.icet.controller.storage;

import edu.icet.dto.ImageResponseDTO;
import edu.icet.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Slf4j
public class ImageController {

    private final SupabaseService supabaseService;

    @PostMapping("/upload")
    public ResponseEntity<ImageResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Authorization header: {}", authHeader);

        try {
            // Verify the token is present
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            ImageResponseDTO response = supabaseService.uploadImage(file, username);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Upload failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}