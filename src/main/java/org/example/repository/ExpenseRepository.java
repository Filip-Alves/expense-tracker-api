package org.example.repository;

import org.example.entity.Expense;
import org.example.entity.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Toutes les dépenses d'un utilisateur
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    // Dépenses d'un utilisateur entre deux dates
    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);

    // Dépenses d'un utilisateur par catégorie
    List<Expense> findByUserIdAndCategoryOrderByExpenseDateDesc(Long userId, ExpenseCategory category);

    // Dépenses d'un utilisateur après une date donnée
    List<Expense> findByUserIdAndExpenseDateGreaterThanEqualOrderByExpenseDateDesc(
            Long userId, LocalDate fromDate);

    // Vérifier qu'une dépense appartient bien à un utilisateur
    boolean existsByIdAndUserId(Long expenseId, Long userId);

    // Trouver une dépense par ID et utilisateur (pour sécurité)
    @Query("SELECT e FROM Expense e WHERE e.id = :expenseId AND e.userId = :userId")
    Expense findByIdAndUserId(@Param("expenseId") Long expenseId, @Param("userId") Long userId);

    // Supprimer une dépense d'un utilisateur spécifique
    void deleteByIdAndUserId(Long expenseId, Long userId);
}