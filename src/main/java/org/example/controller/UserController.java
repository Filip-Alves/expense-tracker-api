package org.example.controller;

import org.example.entity.User;
import org.example.service.JwtService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Récupération des données de la requête
            String username = request.get("username");
            String email = request.get("email");
            String password = request.get("password");

            // Validation des données reçues
            if (username == null || username.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Username is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Inscription de l'utilisateur
            User user = userService.registerUser(username.trim(), email.trim(), password);

            // Création de la réponse de succès
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("username", user.getUsername());
            userResponse.put("email", user.getEmail());
            userResponse.put("created_at", user.getCreatedAt());

            response.put("success", true);
            response.put("message", "Account created successfully");
            response.put("user", userResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            // Gestion des erreurs (email/username déjà existant, mot de passe trop court, etc.)
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            // Erreur inattendue
            response.put("success", false);
            response.put("message", "An error occurred while creating account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");

            // Validation des données
            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Recherche de l'utilisateur par email
            User user = userService.findByEmail(email.trim());
            if (user == null) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }

            // Vérification du mot de passe
            if (!userService.checkPassword(password, user.getPasswordHash())) {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }

            // Génération du token JWT
            String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getEmail());

            // Réponse de succès
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("id", user.getId());
            userResponse.put("username", user.getUsername());
            userResponse.put("email", user.getEmail());

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", token);
            response.put("user", userResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred during login");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        /*
        token valide: eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjIxLCJlbWFpbCI6InRlc3RAZXhhbXBsZS5jb20iLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwic3ViIjoiMjEiLCJpYXQiOjE3NTYyOTIxNjgsImV4cCI6MTc1NjM3ODU2OH0.X0MowcPQ7igw9ZPu5KrFq32-ldrGNl95jWs9Ii7Cqx8
         */
    }
}