# Expense Tracker Project

## Description
Ce projet est une API REST pour gérer des dépenses personnelles. Il utilise Spring Boot et une base de données Oracle XE.

---

## Prérequis

- Java 17 ou supérieur
- Maven
- Oracle Database XE (gratuit)
- Optionnel : Oracle SQL Developer pour gérer la base de données

---

## Installation Oracle Database XE

1. Télécharger Oracle Database XE : [Oracle XE Downloads](https://www.oracle.com/database/technologies/xe-downloads.html)
2. Installer Oracle XE en suivant les instructions officielles.
3. Créer un utilisateur dédié pour le projet ou utiliser `system`.
4. Créer une base de données nommée `expenseTrackerDB`.

---

## Installation Oracle SQL Developer (optionnel)

1. Télécharger Oracle SQL Developer : [SQL Developer Downloads](https://www.oracle.com/tools/downloads/sqldev-downloads.html)
2. Installer et lancer SQL Developer.
3. Créer une connexion vers `expenseTrackerDB` avec les credentials choisis.
4. Tester la connexion.

---

## Importer la base de données du projet

1. Dans SQL Developer, ouvrir l’onglet "Fichier" → "Ouvrir".
2. Sélectionner le fichier `project_expense.sql`.
3. Exécuter le script pour créer uniquement les tables `users` et `expenses` utilisées par le projet.
4. Vérifier que les tables ont été créées.

---

## Configuration du projet

1. Copier `application.properties.txt` en `application.properties`.
2. Modifier les paramètres pour correspondre à votre base de données Oracle XE :

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.database-platform=org.hibernate.dialect.Oracle12cDialect
```

---

## Lancer le projet

1. Depuis la racine du projet, exécuter :

```bash
mvn clean install
mvn spring-boot:run
```

2. L’API sera disponible sur : `http://localhost:8080`

---

## Swagger UI

Une fois le projet lancé, Swagger est disponible pour tester les API :

- URL : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Vous pourrez y tester toutes les API (`UserController` et `ExpenseController`) directement depuis le navigateur.

---

## API principales

### User

- **POST /api/users/register** – Créer un utilisateur
- **POST /api/users/login** – Authentifier un utilisateur

### Expense

- **POST /api/expenses** – Créer une dépense
- **GET /api/expenses** – Lister les dépenses (avec filtres)
- **PUT /api/expenses/{id}** – Mettre à jour une dépense
- **DELETE /api/expenses/{id}** – Supprimer une dépense

> Attention : toutes les API Expense nécessitent un JWT dans l’en-tête `Authorization`.

---

## Notes

- Les tests peuvent être faits via **Postman** ou **Swagger UI**.
- Le fichier `application.properties` n’est pas versionné pour des raisons de sécurité (mot de passe).
- Le projet utilise Spring Boot, Spring Data JPA et une base Oracle XE.

