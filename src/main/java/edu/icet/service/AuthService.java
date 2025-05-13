package edu.icet.service;

import edu.icet.dto.LoginResponse;
import edu.icet.dto.RegisterRequest;

public interface AuthService {
    String registerUser(RegisterRequest registerRequest);
    LoginResponse loginUser(String username, String password);
}
