package edu.icet.service;

import org.springframework.web.multipart.MultipartFile;

public interface SupabaseStorageService {
    String uploadImage(MultipartFile file);
}
