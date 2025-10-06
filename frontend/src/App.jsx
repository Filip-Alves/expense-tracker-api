import React from "react";
import { AuthProvider } from "./context/AuthContext.jsx";
import { ExpensesProvider } from "./context/ExpensesContext.jsx";
import Header from "./components/Header.jsx";
import AuthView from "./components/AuthView.jsx";
import Dashboard from "./components/Dashboard.jsx";

export default function App() {
  return (
    <AuthProvider>
      <ExpensesProvider>
        <div className="app">
          <Header />
          <AppContent />
        </div>
      </ExpensesProvider>
    </AuthProvider>
  );
}

function AppContent() {
  return (
    <AuthView>
      <Dashboard />
    </AuthView>
  );
}


