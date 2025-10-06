import React, { useMemo, useState } from "react";
import { useExpenses } from "../context/ExpensesContext.jsx";

const categoryOptions = [
  { value: "Groceries", label: "Alimentation" },
  { value: "Leisure", label: "Loisirs" },
  { value: "Electronics", label: "Électronique" },
  { value: "Utilities", label: "Énergie" },
  { value: "Clothing", label: "Vêtements" },
  { value: "Health", label: "Santé" },
  { value: "Others", label: "Autre" },
];

const filterOptions = [
  { value: "all", label: "Toutes" },
  { value: "week", label: "Cette semaine" },
  { value: "month", label: "Ce mois" },
  { value: "3months", label: "3 derniers mois" },
];

function getCategoryDisplayName(category) {
  const map = Object.fromEntries(categoryOptions.map((o) => [o.value, o.label]));
  return map[category] || category;
}

export default function Dashboard() {
  const { expenses, loading, filter, setFilter, createExpense, updateExpense, deleteExpense, editingExpenseId, setEditingExpenseId } = useExpenses();
  const [form, setForm] = useState({ description: "", amount: "", category: "Groceries", expense_date: new Date().toISOString().split("T")[0] });

  const total = useMemo(() => expenses.reduce((sum, e) => sum + parseFloat(e.amount || 0), 0), [expenses]);

  const onSubmit = async (e) => {
    e.preventDefault();
    const payload = { ...form };
    if (editingExpenseId) {
      const res = await updateExpense(editingExpenseId, payload);
      if (!res.success) alert(res.message || "Erreur mise à jour dépense");
      setEditingExpenseId(null);
    } else {
      const res = await createExpense(payload);
      if (!res.success) alert(res.message || "Erreur création dépense");
    }
    setForm({ description: "", amount: "", category: "Groceries", expense_date: new Date().toISOString().split("T")[0] });
  };

  const startEdit = (expense) => {
    setEditingExpenseId(expense.id);
    setForm({ description: expense.description, amount: expense.amount, category: expense.category, expense_date: expense.expense_date });
  };

  const cancelEdit = () => {
    setEditingExpenseId(null);
    setForm({ description: "", amount: "", category: "Groceries", expense_date: new Date().toISOString().split("T")[0] });
  };

  return (
    <div className="dashboard">
      <div className="stats">
        <div className="stat-card">
          <h3>Total Dépenses</h3>
          <p className="total-amount">{`${total.toFixed(2)}€`}</p>
        </div>
        <div className="stat-card">
          <h3>Dépenses</h3>
          <p className="count">{expenses.length}</p>
        </div>
      </div>

      <div className="content-grid">
        <div className="expense-form-section">
          <h2>{editingExpenseId ? "Modifier Dépense" : "Nouvelle Dépense"}</h2>
          <form onSubmit={onSubmit}>
            <input
              placeholder="Description"
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              required
            />
            <input
              type="number"
              step="0.01"
              placeholder="Montant"
              value={form.amount}
              onChange={(e) => setForm({ ...form, amount: e.target.value })}
              required
            />
            <select
              value={form.category}
              onChange={(e) => setForm({ ...form, category: e.target.value })}
            >
              {categoryOptions.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
            <input
              type="date"
              value={form.expense_date}
              onChange={(e) => setForm({ ...form, expense_date: e.target.value })}
              required
            />
            <div className="form-actions">
              {editingExpenseId && (
                <button className="btn secondary-btn" type="button" onClick={cancelEdit}>Annuler</button>
              )}
              <button className="btn primary-btn" type="submit">{editingExpenseId ? "Mettre à jour" : "Ajouter"}</button>
            </div>
          </form>
        </div>

        <div className="expenses-section">
          <div className="expenses-header">
            <h2>Historique</h2>
            <select value={filter} onChange={(e) => setFilter(e.target.value)}>
              {filterOptions.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          </div>

          {loading ? (
            <div className="loading">Chargement...</div>
          ) : (
            <div className="expenses-list">
              {expenses.map((expense) => (
                <div key={expense.id} className="expense-item">
                  <div className="expense-content">
                    <div className="expense-main">
                      <span className="description">{expense.description}</span>
                      <span className="amount">{`${expense.amount}€`}</span>
                    </div>
                    <div className="expense-details">
                      <span className="category">{getCategoryDisplayName(expense.category)}</span>
                      <span className="date">{expense.expense_date}</span>
                    </div>
                  </div>
                  <div className="expense-actions">
                    <button className="btn edit-btn" onClick={() => startEdit(expense)}>✏️</button>
                    <button className="btn danger-btn" onClick={() => deleteExpense(expense.id)}>🗑️</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}


