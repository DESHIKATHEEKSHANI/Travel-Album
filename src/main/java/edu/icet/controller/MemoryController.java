package edu.icet.controller;

import edu.icet.dto.MemoryDTO;
import edu.icet.service.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/memories")
@Slf4j
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping
    public ResponseEntity<MemoryDTO> createMemory(
            @RequestBody MemoryDTO memoryDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Creating new memory: {}", memoryDTO);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            MemoryDTO createdMemory = memoryService.createMemory(memoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMemory);
        } catch (Exception e) {
            log.error("Failed to create memory", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MemoryDTO>> getAllMemories(
            @RequestParam(required = false) String username,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Fetching memories for user: {}", username);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<MemoryDTO> memories = memoryService.getMemories(username);
            return ResponseEntity.ok(memories);
        } catch (Exception e) {
            log.error("Failed to fetch memories", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemoryDTO> getMemoryById(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Fetching memory with ID: {}", id);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            MemoryDTO memory = memoryService.getMemoryById(id);
            if (memory != null) {
                return ResponseEntity.ok(memory);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to fetch memory", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MemoryDTO> updateMemory(
            @PathVariable String id,
            @RequestBody MemoryDTO memoryDTO,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Updating memory with ID: {}", id);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            memoryDTO.setId(id);
            MemoryDTO updatedMemory = memoryService.updateMemory(memoryDTO);
            if (updatedMemory != null) {
                return ResponseEntity.ok(updatedMemory);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to update memory", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemory(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader) {

        log.info("Deleting memory with ID: {}", id);

        // Verify the token is present
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            boolean deleted = memoryService.deleteMemory(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to delete memory", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}