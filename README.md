# 📚 Digital Library Management System

A full-stack digital library application built with Spring Boot, Thymeleaf, and OAuth2 (GitHub Login). It allows users to view and reserve books, and admins to manage users and book inventory professionally.

---

## 💡 Main Features

### 👤 Normal User
- GitHub OAuth2 login
- View book list with advanced search (by title, author, or ISBN)
- Reserve a book if not already reserved and copies are available
- Cancel existing reservations
- View list of own reservations

### 🛠️ Admin
- Full access to the admin panel
- View all registered users and their reservation status
- Manage reservations of any user (view/cancel)
- Add / Edit / Delete books

---

## 🔐 Authentication

- OAuth2 login with GitHub
- First-time GitHub login triggers auto-registration
- Role-based access control using Spring Security

---

## 🧩 Technical Stack

| Technology        | Purpose                            |
|-------------------|------------------------------------|
| Java + Spring Boot| Back-End REST & MVC logic          |
| Thymeleaf         | Dynamic user interfaces             |
| Spring Security + OAuth2 | Authentication & Authorization |
| Spring Data JPA + H2/MySQL | Database ORM                 |
| Spring Cache      | Performance via caching            |
| Bootstrap 5       | Responsive UI design               |

---

## 🗃️ Database Schema

- **User**: User info with active/inactive reservation state
- **Book**: Book details including available copies
- **Reservation**: Tracks user-book reservations with status

---

## 🧠 Reservation Logic

- A user can only reserve a book once if an active reservation exists
- If no copies are available, reservation is denied
- Cancelling a reservation increases available copies
- Reserving decreases available copies

---

## 🧪 Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/your-username/digital-library.git
cd digital-library
```

### 2. Open in IntelliJ or VS Code

- Use JDK 17+
- Run `DigitalLibraryManagementSystemApplication.java`

### 3. Setup GitHub OAuth App

Create a GitHub OAuth app at: `https://github.com/settings/developers`

Add the credentials to `application.yml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: YOUR_CLIENT_ID
            client-secret: YOUR_CLIENT_SECRET
```

---

## 📂 Project Structure

```
src
├── controller
│   └── AuthController, BookController, ReservationController, AdminController
├── service
│   └── BookService, ReservationService, UserService
├── repository
│   └── BookRepository, UserRepository, ReservationRepository
├── entity
│   └── User, Book, Reservation
├── config
│   └── SecurityConfig, OAuth2UserService
├── templates
│   └── Thymeleaf HTML Pages
└── static
    └── css, js
```

---

## 📸 Screenshots

> Add UI screenshots here (book list, reservation, admin dashboard, etc.)

---

## 📃 License

This project is open source for academic and personal use only. Commercial use is not allowed without permission.

---

## 🙌 Author

- Name: [Your Name]
- GitHub: [Your GitHub Profile]