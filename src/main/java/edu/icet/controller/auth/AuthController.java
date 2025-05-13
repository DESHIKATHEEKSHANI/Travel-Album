package edu.icet.controller.auth;

import edu.icet.dto.LoginResponse;
import edu.icet.dto.RegisterRequest;
import edu.icet.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registerUser(registerRequest));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestParam String username, @RequestParam String password) {
        return authService.loginUser(username, password);
    }
}