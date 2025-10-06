package org.example.service;

import org.example.entity.Expense;
import org.example.entity.ExpenseCategory;
import org.example.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    /**
     * Créer une nouvelle dépense
     */
    public Expense createExpense(Long userId, String description, BigDecimal amount,
                                 ExpenseCategory category, LocalDate expenseDate) {

        // Validations
        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Description is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        if (category == null) {
            throw new RuntimeException("Category is required");
        }

        if (expenseDate == null) {
            throw new RuntimeException("Expense date is required");
        }

        // Création de la dépense
        Expense expense = new Expense(userId, description.trim(), amount, category, expenseDate);

        return expenseRepository.save(expense);
    }

    /**
     * Récupérer toutes les dépenses d'un utilisateur
     */
    public List<Expense> getAllExpensesByUser(Long userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId);
    }

    /**
     * Récupérer les dépenses par filtre de date
     */
    public List<Expense> getExpensesByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetweenOrderByExpenseDateDesc(
                userId, startDate, endDate);
    }

    /**
     * Récupérer les dépenses de la semaine passée
     */
    public List<Expense> getExpensesLastWeek(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);
        return getExpensesByDateRange(userId, startDate, endDate);
    }

    /**
     * Récupérer les dépenses du mois passé
     */
    public List<Expense> getExpensesLastMonth(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);
        return getExpensesByDateRange(userId, startDate, endDate);
    }

    /**
     * Récupérer les dépenses des 3 derniers mois
     */
    public List<Expense> getExpensesLast3Months(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);
        return getExpensesByDateRange(userId, startDate, endDate);
    }

    /**
     * Modifier une dépense existante
     */
    @Transactional
    public Expense updateExpense(Long userId, Long expenseId, String description,
                                 BigDecimal amount, ExpenseCategory category, LocalDate expenseDate) {

        // Vérifier que la dépense appartient à l'utilisateur
        Expense expense = expenseRepository.findByIdAndUserId(expenseId, userId);
        if (expense == null) {
            throw new RuntimeException("Expense not found or access denied");
        }

        // Validations (mêmes que pour la création)
        if (description == null || description.trim().isEmpty()) {
            throw new RuntimeException("Description is required");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        if (category == null) {
            throw new RuntimeException("Category is required");
        }

        if (expenseDate == null) {
            throw new RuntimeException("Expense date is required");
        }

        // Mise à jour
        expense.setDescription(description.trim());
        expense.setAmount(amount);
        expense.setCategory(category);
        expense.setExpenseDate(expenseDate);

        return expenseRepository.save(expense);
    }

    /**
     * Supprimer une dépense
     */
    @Transactional
    public boolean deleteExpense(Long userId, Long expenseId) {
        // Vérifier que la dépense existe et appartient à l'utilisateur
        if (!expenseRepository.existsByIdAndUserId(expenseId, userId)) {
            return false;
        }

        expenseRepository.deleteByIdAndUserId(expenseId, userId);
        return true;
    }

    /**
     * Récupérer une dépense par ID (avec vérification utilisateur)
     */
    public Expense getExpenseById(Long userId, Long expenseId) {
        return expenseRepository.findByIdAndUserId(expenseId, userId);
    }
}