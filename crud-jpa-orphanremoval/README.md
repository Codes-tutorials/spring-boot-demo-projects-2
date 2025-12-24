# Spring Boot JPA Orphan Removal Demo

This project demonstrates the **Orphan Removal** feature in JPA (Java Persistence API) using Spring Boot. It highlights how to manage parent-child relationships where removing a child from the parent's collection automatically deletes the child from the database.

## 🚀 Tech Stack

*   **Java**: 17 or higher
*   **Spring Boot**: 4.0.1
*   **Spring Data JPA**: For ORM and database interactions
*   **Database**: H2 In-Memory Database (or configured MySQL/PostgreSQL)

## 🛠️ Key Concepts

### One-to-Many Relationship
The project models an **Author** (Parent) having many **Books** (Children).

### Orphan Removal & Cascade
*   **Entity**: `Author.java`
*   **Annotation**: `@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)`
*   **Behavior**:
    *   **CascadeType.ALL**: Operations (Save, Update, Delete) on `Author` are propagated to `Book`.
    *   **orphanRemoval=true**: When a `Book` object is removed from the `books` list in the `Author` entity, JPA automatically deletes that `Book` record from the database.

## 🏃‍♂️ How to Run

1.  **Navigate to the project directory**:
    ```bash
    cd crud-jpa-orphanremoval
    ```
2.  **Run the application**:
    ```bash
    ../mvnw spring-boot:run
    ```
    *Note: Use `..\mvnw` on Windows Command Prompt.*

## 📡 API Endpoints

The application exposes REST endpoints to manage Authors and their Books.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/authors` | List all authors and their books |
| `POST` | `/authors` | Create a new author with books |
| `PUT` | `/authors/{id}` | Update an author (demonstrates orphan removal) |
| `DELETE` | `/authors/{id}` | Delete an author (cascades delete to books) |

### Example JSON Payload (Create/Update)
```json
{
  "name": "J.K. Rowling",
  "books": [
    { "title": "Harry Potter and the Sorcerer's Stone" },
    { "title": "Harry Potter and the Chamber of Secrets" }
  ]
}
```

## 🧪 Verifying Orphan Removal

1.  **Create** an Author with 2 Books.
2.  **Update** the Author by sending a PUT request with a list containing only 1 of those Books (or a new list).
3.  **Result**: The omitted Book is automatically deleted from the `book` database table, not just dissociated.
