package org.example.entity;

public enum ExpenseCategory {
    GROCERIES("Groceries"),
    LEISURE("Leisure"),
    ELECTRONICS("Electronics"),
    UTILITIES("Utilities"),
    CLOTHING("Clothing"),
    HEALTH("Health"),
    OTHERS("Others");

    private final String displayName;

    ExpenseCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}