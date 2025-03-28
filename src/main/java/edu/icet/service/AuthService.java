package edu.icet.service;

import edu.icet.dto.RegisterRequest;

public interface AuthService {
    String registerUser(RegisterRequest registerRequest);
    String loginUser(String username, String password);
}
