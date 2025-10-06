package org.example.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Inscription d'un nouvel utilisateur
     */
    public User registerUser(String username, String email, String password) {
        // Vérification si l'email existe déjà
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // Vérification si le username existe déjà
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Validation du mot de passe (minimum 6 caractères)
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        // Hashage du mot de passe
        String hashedPassword = passwordEncoder.encode(password);

        // Création de l'utilisateur
        User user = new User(username, email, hashedPassword);

        // Sauvegarde en base
        return userRepository.save(user);
    }

    /**
     * Trouver un utilisateur par email
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Vérifier si un mot de passe correspond au hash
     */
    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}