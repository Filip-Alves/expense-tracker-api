import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { api } from "../services/api.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [activeTab, setActiveTab] = useState("login");

  useEffect(() => {
    const savedToken = localStorage.getItem("expense-tracker-token");
    const savedUser = localStorage.getItem("expense-tracker-user");
    if (savedToken && savedUser) {
      setToken(savedToken);
      setUser(JSON.parse(savedUser));
    }
  }, []);

  const login = async (email, password) => {
    const data = await api.post("/users/login", { email, password });
    if (data.success) {
      setToken(data.token);
      setUser(data.user);
      localStorage.setItem("expense-tracker-token", data.token);
      localStorage.setItem("expense-tracker-user", JSON.stringify(data.user));
    }
    return data;
  };

  const register = async (payload) => {
    const data = await api.post("/users/register", payload);
    return data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem("expense-tracker-token");
    localStorage.removeItem("expense-tracker-user");
  };

  const value = useMemo(
    () => ({ user, token, login, register, logout, activeTab, setActiveTab }),
    [user, token, activeTab]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}


