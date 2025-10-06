import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { useAuth } from "./AuthContext.jsx";
import { api } from "../services/api.js";

const ExpensesContext = createContext(null);

export function ExpensesProvider({ children }) {
  const { token } = useAuth();
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState("all");
  const [editingExpenseId, setEditingExpenseId] = useState(null);

  useEffect(() => {
    if (token) load();
  }, [token, filter]);

  const load = async () => {
    if (!token) return;
    setLoading(true);
    try {
      const query = filter !== "all" ? `?filter=${encodeURIComponent(filter)}` : "";
      const data = await api.get(`/expenses${query}`, { token });
      if (data.success) setExpenses(data.expenses || []);
    } finally {
      setLoading(false);
    }
  };

  const createExpense = async (payload) => {
    const data = await api.post("/expenses", payload, { token });
    if (data.success) await load();
    return data;
  };

  const updateExpense = async (id, payload) => {
    const data = await api.put(`/expenses/${id}`, payload, { token });
    if (data.success) await load();
    return data;
  };

  const deleteExpense = async (id) => {
    const data = await api.delete(`/expenses/${id}`, { token });
    if (data.success) await load();
    return data;
  };

  const value = useMemo(
    () => ({
      expenses,
      loading,
      filter,
      setFilter,
      load,
      createExpense,
      updateExpense,
      deleteExpense,
      editingExpenseId,
      setEditingExpenseId,
    }),
    [expenses, loading, filter, editingExpenseId]
  );

  return <ExpensesContext.Provider value={value}>{children}</ExpensesContext.Provider>;
}

export function useExpenses() {
  const ctx = useContext(ExpensesContext);
  if (!ctx) throw new Error("useExpenses must be used within ExpensesProvider");
  return ctx;
}


