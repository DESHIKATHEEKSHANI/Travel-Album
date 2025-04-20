package edu.icet.service;

import edu.icet.dto.MemoryDTO;
import java.util.List;

public interface MemoryService {
    MemoryDTO createMemory(MemoryDTO memoryDTO);
    List<MemoryDTO> getMemories(String username);
    MemoryDTO getMemoryById(String id);
    MemoryDTO updateMemory(MemoryDTO memoryDTO);
    boolean deleteMemory(String id);
}