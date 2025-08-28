# Expense Tracker API

API REST pour la gestion des dépenses personnelles, avec authentification JWT et documentation Swagger.

---

## Prérequis

- **Java 17** (ou version compatible Spring Boot 3)
- **Maven**
- **Oracle Database XE** (ou équivalent)
- Optionnel : **Oracle SQL Developer** pour manipuler la base de données facilement
- IDE recommandé : IntelliJ, VSCode, Eclipse…

---

## Installation de la base de données

1. **Installer Oracle Database XE**  
   Suivez les instructions officielles : https://www.oracle.com/database/technologies/xe-downloads.html

2. **Installer Oracle SQL Developer** (optionnel)  
   Téléchargez ici : https://www.oracle.com/tools/downloads/sqldev-downloads.html

3. **Créer une connexion dans SQL Developer**  
   - Nom de la connexion : `expenseTrackerDB`  
   - Username / Password : votre utilisateur Oracle (ex: SYSTEM)  
   - Hostname : localhost  
   - Port : 1521  
   - SID : XE  

4. **Importer le fichier SQL du projet**  
   - Ouvrir `project_expense.sql` via SQL Developer  
   - Exécuter le script pour créer les tables `expenses` et `users` et autres objets nécessaires  

---

## Configuration du projet

1. Renommer le fichier `application.properties.txt` en `application.properties`  
2. Modifier les propriétés si nécessaire :  

    spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE  
    spring.datasource.username=VOTRE_USER  
    spring.datasource.password=VOTRE_PASSWORD  
    spring.datasource.driver-class-name=oracle.jdbc.OracleDriver  
    spring.jpa.hibernate.ddl-auto=none  

> ⚠️ **Ne pas inclure vos credentials réels dans GitHub.**

---

## Lancer le projet

1. Dans le terminal ou IDE :  

    mvn spring-boot:run

2. L’API sera accessible sur :  
    http://localhost:8080

3. Swagger UI (documentation et test des API) :  
    http://localhost:8080/swagger-ui.html

---

## API principales

- **UserController** : `/api/users/register`, `/api/users/login`  
- **ExpenseController** : `/api/expenses` (GET, POST), `/api/expenses/{id}` (PUT, DELETE)  

> Swagger vous permet de tester toutes les routes directement depuis le navigateur.  

---

## Tester avec Postman

- Créer les headers :  
  - `Content-Type: application/json`  
  - `Authorization: Bearer <votre_token>` pour les endpoints sécurisés  

- Exemple POST `/api/expenses` :  

    {
      "description": "Visite médicale",
      "amount": 120.00,
      "category": "Health",
      "expense_date": "2025-08-28"
    }

---

## Notes

- La base Oracle doit être en cours d’exécution pour que l’application fonctionne.  
- Les tables sont pré-remplies via le script SQL `project_expense.sql`.  
- Swagger UI peut être utilisé pour générer et tester des tokens JWT, mais Postman fonctionne aussi parfaitement.

---

## Commit suggéré

    git commit -m "Add README with setup instructions and database import"
