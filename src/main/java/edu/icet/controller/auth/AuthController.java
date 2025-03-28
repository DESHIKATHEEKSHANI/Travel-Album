package edu.icet.controller.auth;

import edu.icet.dto.RegisterRequest;
import edu.icet.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> loginUser(
            @RequestParam String username,
            @RequestParam String password
    ) {
        String token = authService.loginUser(username, password);
        return ResponseEntity.ok(token);
    }
}