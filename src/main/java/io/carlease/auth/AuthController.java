package io.carlease.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Map<String, String> CREDENTIALS = Map.of("worker", "12345");
    private static final Map<String, String> ROLES = Map.of("worker", "WORKER");

    @PostMapping("/login")
    public ResponseEntity<AuthUser> login(@RequestBody LoginRequest req, HttpSession session) {
        String storedPassword = CREDENTIALS.get(req.username());
        if (storedPassword == null || !storedPassword.equals(req.password())) {
            return ResponseEntity.status(401).build();
        }
        session.setAttribute("username", req.username());
        session.setAttribute("role", ROLES.get(req.username()));
        session.setAttribute("name", null);
        session.setAttribute("email", null);
        session.setAttribute("company", null);
        return ResponseEntity.ok(new AuthUser(req.username(), ROLES.get(req.username()), null, null, null));
    }

    @PostMapping("/customer-login")
    public ResponseEntity<AuthUser> customerLogin(@RequestBody CustomerLoginRequest req, HttpSession session) {
        if (req.name() == null || req.name().isBlank()) return ResponseEntity.badRequest().build();
        if (req.email() == null || !req.email().contains("@")) return ResponseEntity.badRequest().build();
        if (req.company() == null || req.company().isBlank()) return ResponseEntity.badRequest().build();

        session.setAttribute("username", req.email());
        session.setAttribute("role", "CUSTOMER");
        session.setAttribute("name", req.name());
        session.setAttribute("email", req.email());
        session.setAttribute("company", req.company());
        return ResponseEntity.ok(new AuthUser(req.email(), "CUSTOMER", req.name(), req.email(), req.company()));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUser> me(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String role = (String) session.getAttribute("role");
        if (username == null) return ResponseEntity.status(401).build();
        String name    = (String) session.getAttribute("name");
        String email   = (String) session.getAttribute("email");
        String company = (String) session.getAttribute("company");
        return ResponseEntity.ok(new AuthUser(username, role, name, email, company));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
