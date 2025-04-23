package edu.icet.service.impl;

import edu.icet.dto.MemoryDTO;
import edu.icet.entity.Memory;
import edu.icet.repository.MemoryRepository;
import edu.icet.service.MemoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository memoryRepository;

    @Override
    public MemoryDTO createMemory(MemoryDTO memoryDTO) {
        Memory memory = mapToEntity(memoryDTO);

        // Generate a unique ID if not provided
        if (memory.getId() == null || memory.getId().isEmpty()) {
            memory.setId(UUID.randomUUID().toString());
        }

        // Set creation timestamp
        memory.setCreatedAt(new Date());

        Memory savedMemory = memoryRepository.save(memory);
        return mapToDTO(savedMemory);
    }

    @Override
    public List<MemoryDTO> getMemories(String username) {
        List<Memory> memories;

        if (username != null && !username.isEmpty()) {
            memories = memoryRepository.findByUsername(username);
        } else {
            memories = memoryRepository.findAll();
        }

        return memories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MemoryDTO getMemoryById(String id) {
        Optional<Memory> memoryOptional = memoryRepository.findById(id);
        return memoryOptional.map(this::mapToDTO).orElse(null);
    }

    @Override
    public MemoryDTO updateMemory(MemoryDTO memoryDTO) {
        if (memoryRepository.existsById(memoryDTO.getId())) {
            Memory memory = mapToEntity(memoryDTO);
            Memory updatedMemory = memoryRepository.save(memory);
            return mapToDTO(updatedMemory);
        }
        return null;
    }

    @Override
    public boolean deleteMemory(String id) {
        if (memoryRepository.existsById(id)) {
            memoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private Memory mapToEntity(MemoryDTO dto) {
        Memory memory = new Memory();
        memory.setId(dto.getId());
        memory.setLocation(dto.getLocation());
        memory.setDate(dto.getDate());
        memory.setDescription(dto.getDescription());
        memory.setImageUrl(dto.getImageUrl());
        memory.setUsername(dto.getUsername());
        memory.setCategory(dto.getCategory());
        memory.setFavorite(dto.isFavorite());
        memory.setCreatedAt(dto.getCreatedAt());
        return memory;
    }

    private MemoryDTO mapToDTO(Memory entity) {
        return new MemoryDTO(
                entity.getId(),
                entity.getLocation(),
                entity.getDate(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getUsername(),
                entity.getCategory(),
                entity.isFavorite(),
                entity.getCreatedAt()
        );
    }
}