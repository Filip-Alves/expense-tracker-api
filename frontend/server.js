const express = require("express");
const path = require("path");
const app = express();

const PORT = process.env.PORT || 3000;
const distPath = path.join(__dirname, "dist");

app.use(express.json());
app.use(express.static(distPath));

// Middleware "catch-all" pour SPA (Express 5 safe)
app.use((req, res, next) => {
  if (req.path.startsWith("/api")) return next();
  // Vérifier si c'est un fichier statique (déjà géré par express.static)
  if (path.extname(req.path)) return next();
  res.sendFile(path.join(distPath, "index.html"));
});

app.listen(PORT, () => {
  console.log(`Serveur démarré sur http://localhost:${PORT}`);
});
