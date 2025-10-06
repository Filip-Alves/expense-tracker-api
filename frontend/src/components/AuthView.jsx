import React, { useState } from "react";
import { useAuth } from "../context/AuthContext.jsx";

export default function AuthView({ children }) {
  const { user, activeTab } = useAuth();
  return user ? <>{children}</> : <AuthForms activeTab={activeTab} />;
}

function AuthForms({ activeTab }) {
  const { login, register, setActiveTab } = useAuth();
  const [loginForm, setLoginForm] = useState({ email: "", password: "" });
  const [registerForm, setRegisterForm] = useState({ username: "", email: "", password: "" });

  const submitLogin = async (e) => {
    e.preventDefault();
    const res = await login(loginForm.email, loginForm.password);
    if (!res.success) alert(res.message || "Identifiants incorrects");
  };

  const submitRegister = async (e) => {
    e.preventDefault();
    const res = await register(registerForm);
    if (res.success) {
      alert("Inscription réussie! Connectez-vous maintenant.");
      setRegisterForm({ username: "", email: "", password: "" });
      setActiveTab("login");
    } else {
      alert(res.message || "Erreur d'inscription");
    }
  };

  return (
    <div className="auth-container">
      {activeTab === "login" ? (
        <form onSubmit={submitLogin} className="auth-form login-form">
          <h2>Connexion</h2>
          <input
            type="email"
            placeholder="Email"
            value={loginForm.email}
            onChange={(e) => setLoginForm({ ...loginForm, email: e.target.value })}
            required
          />
          <input
            type="password"
            placeholder="Mot de passe"
            value={loginForm.password}
            onChange={(e) => setLoginForm({ ...loginForm, password: e.target.value })}
            required
          />
          <button className="btn primary-btn" type="submit">Se connecter</button>
        </form>
      ) : (
        <form onSubmit={submitRegister} className="auth-form register-form">
          <h2>Inscription</h2>
          <input
            placeholder="Nom d'utilisateur"
            value={registerForm.username}
            onChange={(e) => setRegisterForm({ ...registerForm, username: e.target.value })}
            required
          />
          <input
            type="email"
            placeholder="Email"
            value={registerForm.email}
            onChange={(e) => setRegisterForm({ ...registerForm, email: e.target.value })}
            required
          />
          <input
            type="password"
            placeholder="Mot de passe"
            value={registerForm.password}
            onChange={(e) => setRegisterForm({ ...registerForm, password: e.target.value })}
            required
          />
          <button className="btn primary-btn" type="submit">S'inscrire</button>
        </form>
      )}
    </div>
  );
}


