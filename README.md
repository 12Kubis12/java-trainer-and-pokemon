# Java Trainer and Pokémon - JDBC CRUD Application 🐾

This is a simple console-based Java application using JDBC to manage Pokémon and their trainers. It supports CRUD (Create, Read, Update, Delete) operations for both entities and includes several special features. The app demonstrates relational data handling with one-to-many and many-to-one relationships using a MySQL database.

---

## ✨ Features

- CRUD operations for trainers
- CRUD operations for Pokémon
- List all Pokémon belonging to a specific trainer
- List all trainers sorted by the number of Pokémon they own
- List all Pokémon that are not assigned to any trainer (wild Pokémon)
- Catch a Pokémon: assign a wild Pokémon to a selected trainer

---

## 🛠️ Technologies Used

- Java 17
- Maven
- JDBC
- MySQL

---

## 🚀 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/12Kubis12/java-trainer-and-pokemon.git

2. Database Initialization.
   -  Open your SQL client (MySQL) and use this SQL script to set up your database: [`src/main/resources/init.sql`](https://github.com/12Kubis12/java-trainer-and-pokemon/blob/main/src/main/resources/init.sql)
   -  The code in this file :
      - Creates the `trainers` and `pokemons` tables
      - Inserts example data into both tables
  
3. Create a new file named `application.properties` inside the resources directory:

   [`src/main/resources`](https://github.com/12Kubis12/java-trainer-and-pokemon/blob/main/src/main/resources)
   - Add the following content to the file, replacing YOUR_USER_NAME and YOUR_PASSWORD with your actual MySQL credentials:
   
      ```properties
      db.name=trainer_and_pokemon
      db.username=YOUR_USER_NAME
      db.password=YOUR_PASSWORD
      ```
      
5. In this file, replace the URL (`jdbc:mysql://localhost:3306/`) with the appropriate connection string for your own database, if it's different (e.g. using a different host, port, or parameters):

   [`src/main/java/ParentPackage/db/HikariCPDataSource.java`](https://github.com/12Kubis12/java-trainer-and-pokemon/blob/main/src/main/java/ParentPackage/db/HikariCPDataSource.java)
   
    ```
    config.setJdbcUrl("jdbc:mysql://localhost:3306/" + prop.getProperty("db.name"));
    ```
      
7.  Build and run the application.

8. Follow the instructions in console.
