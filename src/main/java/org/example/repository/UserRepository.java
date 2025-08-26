package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Méthode pour trouver un utilisateur par email
    Optional<User> findByEmail(String email);

    // Méthode pour trouver un utilisateur par username
    Optional<User> findByUsername(String username);

    // Méthode pour vérifier si un email existe déjà
    boolean existsByEmail(String email);

    // Méthode pour vérifier si un username existe déjà
    boolean existsByUsername(String username);
}