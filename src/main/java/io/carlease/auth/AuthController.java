package io.carlease.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Map<String, String> CREDENTIALS = Map.of(
            "worker", "12345",
            "customer", "12345"
    );
    private static final Map<String, String> ROLES = Map.of(
            "worker", "WORKER",
            "customer", "CUSTOMER"
    );

    @PostMapping("/login")
    public ResponseEntity<AuthUser> login(@RequestBody LoginRequest req, HttpSession session) {
        String storedPassword = CREDENTIALS.get(req.username());
        if (storedPassword == null || !storedPassword.equals(req.password())) {
            return ResponseEntity.status(401).build();
        }
        session.setAttribute("username", req.username());
        session.setAttribute("role", ROLES.get(req.username()));
        return ResponseEntity.ok(new AuthUser(req.username(), ROLES.get(req.username())));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUser> me(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        if (username == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new AuthUser(username, role));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
