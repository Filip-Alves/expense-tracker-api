package org.example.controller;

import org.example.entity.Expense;
import org.example.entity.ExpenseCategory;
import org.example.service.ExpenseService;
import org.example.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private JwtService jwtService;

    /**
     * Créer une nouvelle dépense
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createExpense(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extraction du token JWT et vérification
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extraction des données
            String description = (String) request.get("description");
            Object amountObj = request.get("amount");
            String categoryStr = (String) request.get("category");
            String expenseDateStr = (String) request.get("expense_date");

            // Validation et conversion
            BigDecimal amount = parseAmount(amountObj);
            ExpenseCategory category = parseCategory(categoryStr);
            LocalDate expenseDate = parseDate(expenseDateStr);


            // Transforme l'enum en valeur compatible DB avant l'insertion
            ExpenseCategory categoryEnum = parseCategory(categoryStr);
            Expense expense = expenseService.createExpense(userId, description, amount, categoryEnum, expenseDate);
            expense.setCategory(categoryEnum); // utilise le setter qui écrit displayName



            // Réponse de succès
            Map<String, Object> expenseResponse = buildExpenseResponse(expense);

            response.put("success", true);
            response.put("message", "Expense created successfully");
            response.put("expense", expenseResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while creating expense");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Récupérer toutes les dépenses avec filtres optionnels
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getExpenses(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extraction du token JWT et vérification
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            List<Expense> expenses;

            // Application des filtres
            if (filter != null) {
                switch (filter.toLowerCase()) {
                    case "week":
                        expenses = expenseService.getExpensesLastWeek(userId);
                        break;
                    case "month":
                        expenses = expenseService.getExpensesLastMonth(userId);
                        break;
                    case "3months":
                        expenses = expenseService.getExpensesLast3Months(userId);
                        break;
                    case "custom":
                        if (startDate == null || endDate == null) {
                            response.put("success", false);
                            response.put("message", "Start date and end date are required for custom filter");
                            return ResponseEntity.badRequest().body(response);
                        }
                        LocalDate start = LocalDate.parse(startDate);
                        LocalDate end = LocalDate.parse(endDate);
                        expenses = expenseService.getExpensesByDateRange(userId, start, end);
                        break;
                    default:
                        expenses = expenseService.getAllExpensesByUser(userId);
                        break;
                }
            } else {
                expenses = expenseService.getAllExpensesByUser(userId);
            }

            // Construction de la réponse
            response.put("success", true);
            response.put("message", "Expenses retrieved successfully");
            response.put("count", expenses.size());
            response.put("expenses", expenses.stream()
                    .map(this::buildExpenseResponse)
                    .toList());

            return ResponseEntity.ok(response);

        } catch (DateTimeParseException e) {
            response.put("success", false);
            response.put("message", "Invalid date format. Use YYYY-MM-DD");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while retrieving expenses");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Modifier une dépense existante
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> updateExpense(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long expenseId,
            @RequestBody Map<String, Object> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extraction du token JWT et vérification
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extraction des données
            String description = (String) request.get("description");
            Object amountObj = request.get("amount");
            String categoryStr = (String) request.get("category");
            String expenseDateStr = (String) request.get("expense_date");

            // Validation et conversion
            BigDecimal amount = parseAmount(amountObj);
            ExpenseCategory category = parseCategory(categoryStr);
            LocalDate expenseDate = parseDate(expenseDateStr);

            ExpenseCategory categoryEnum = parseCategory(categoryStr);
            Expense expense = expenseService.updateExpense(userId, expenseId, description, amount, categoryEnum, expenseDate);
            // optionnel : ajuster la valeur pour la DB si nécessaire
            expense.setCategory(categoryEnum);

            // Réponse de succès
            Map<String, Object> expenseResponse = buildExpenseResponse(expense);

            response.put("success", true);
            response.put("message", "Expense updated successfully");
            response.put("expense", expenseResponse);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while updating expense");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Supprimer une dépense
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Map<String, Object>> deleteExpense(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long expenseId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extraction du token JWT et vérification
            Long userId = getUserIdFromToken(authHeader);
            if (userId == null) {
                response.put("success", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Suppression de la dépense
            boolean deleted = expenseService.deleteExpense(userId, expenseId);

            if (deleted) {
                response.put("success", true);
                response.put("message", "Expense deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Expense not found or access denied");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred while deleting expense");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Méthodes utilitaires privées

    private Long getUserIdFromToken(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return null;
            }

            String token = authHeader.substring(7);

            if (!jwtService.isTokenValid(token)) {
                return null;
            }

            return jwtService.getUserIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal parseAmount(Object amountObj) {
        if (amountObj == null) {
            throw new RuntimeException("Amount is required");
        }

        try {
            if (amountObj instanceof Number) {
                return BigDecimal.valueOf(((Number) amountObj).doubleValue());
            } else if (amountObj instanceof String) {
                return new BigDecimal((String) amountObj);
            } else {
                throw new RuntimeException("Invalid amount format");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid amount format");
        }
    }

    private ExpenseCategory parseCategory(String categoryStr) {
        if (categoryStr == null || categoryStr.trim().isEmpty()) {
            throw new RuntimeException("Category is required");
        }

        try {
            return ExpenseCategory.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category. Valid categories: " +
                    String.join(", ", java.util.Arrays.stream(ExpenseCategory.values())
                            .map(ExpenseCategory::name).toArray(String[]::new)));
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new RuntimeException("Expense date is required");
        }

        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format. Use YYYY-MM-DD");
        }
    }

    private Map<String, Object> buildExpenseResponse(Expense expense) {
        Map<String, Object> expenseResponse = new HashMap<>();
        expenseResponse.put("id", expense.getId());
        expenseResponse.put("description", expense.getDescription());
        expenseResponse.put("amount", expense.getAmount());
        expenseResponse.put("category", expense.getCategory()); // déjà une String maintenant
        expenseResponse.put("expense_date", expense.getExpenseDate());
        expenseResponse.put("created_at", expense.getCreatedAt());
        expenseResponse.put("updated_at", expense.getUpdatedAt());
        return expenseResponse;
    }
}