import React from "react";
import { useAuth } from "../context/AuthContext.jsx";

export default function Header() {
  const { user, logout, activeTab, setActiveTab } = useAuth();
  return (
    <div className="header">
      <h1 className="logo">💰 ExpenseTracker</h1>
      {user ? (
        <div className="user-section">
          <span className="welcome">{`Bonjour, ${user.username}`}</span>
          <button className="btn logout-btn" onClick={logout}>Déconnexion</button>
        </div>
      ) : (
        <div className="auth-tabs">
          <button
            className={`tab ${activeTab === "login" ? "active" : ""}`}
            onClick={() => setActiveTab("login")}
          >
            Connexion
          </button>
          <button
            className={`tab ${activeTab === "register" ? "active" : ""}`}
            onClick={() => setActiveTab("register")}
          >
            Inscription
          </button>
        </div>
      )}
    </div>
  );
}


