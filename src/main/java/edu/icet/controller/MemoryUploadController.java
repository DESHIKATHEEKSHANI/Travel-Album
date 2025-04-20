package edu.icet.controller;

import edu.icet.dto.ImageResponseDTO;
import edu.icet.dto.MemoryDTO;
import edu.icet.service.MemoryService;
import edu.icet.service.SupabaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memory-upload")
@Slf4j
public class MemoryUploadController {

    private final SupabaseService supabaseService;
    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<MemoryDTO> uploadMemoryWithImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username,
            @RequestParam("location") String location,
            @RequestParam("date") String dateStr,
            @RequestParam("description") String description,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Creating new memory with image for user: {}", username);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 1. Upload the image
            ImageResponseDTO imageResponse = supabaseService.uploadImage(file, username);

            // 2. Parse the date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(dateStr);

            // 3. Create the memory object
            MemoryDTO memoryDTO = new MemoryDTO();
            memoryDTO.setUsername(username);
            memoryDTO.setLocation(location);
            memoryDTO.setDate(date);
            memoryDTO.setDescription(description);
            memoryDTO.setImageUrl(imageResponse.getUrl());
            memoryDTO.setCreatedAt(new Date());

            // 4. Save the memory
            MemoryDTO createdMemory = memoryService.createMemory(memoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMemory);
        } catch (ParseException e) {
            log.error("Invalid date format", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to create memory with image", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}