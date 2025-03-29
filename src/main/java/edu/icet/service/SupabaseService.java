package edu.icet.service;

import edu.icet.dto.ImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface SupabaseService {
    ImageResponseDTO uploadImage(MultipartFile file, String username) throws IOException;
}