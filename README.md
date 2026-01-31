# 💰 Expense Tracker Backend

A production-grade REST API for expense tracking built with Spring Boot.

## 🚀 Features

- **Authentication & Authorization** - JWT-based auth with role-based access control (User/Admin)
- **Expense Management** - Full CRUD operations with pagination, sorting, and filtering
- **File Uploads** - Attach receipts and documents to expenses
- **Email Notifications** - Welcome emails and expense notifications via SMTP
- **Analytics** - Total expenses and category-wise summaries (Admin only)
- **Rate Limiting** - API abuse prevention with configurable limits
- **Caching** - Caffeine cache for improved performance
- **API Documentation** - Interactive Swagger UI

## 🛠️ Tech Stack

| Component | Technology |
|-----------|------------|
| Framework | Spring Boot 4.0.2 |
| Database | PostgreSQL |
| Security | Spring Security + JWT |
| Caching | Caffeine |
| Rate Limiting | Bucket4j |
| Documentation | SpringDoc OpenAPI |
| Email | Spring Mail (SMTP) |

## 📁 Project Structure

```
src/main/java/com/expensetracker/
├── config/          # Security, JWT, Cache, Rate limiting configs
├── controller/      # REST API endpoints
├── dto/             # Request/Response objects
├── exception/       # Custom exceptions & global handler
├── model/           # JPA entities
├── repository/      # Data access layer
├── service/         # Business logic
└── util/            # Utility classes (JWT)
```

## 🔧 Setup & Installation

### Prerequisites
- Java 17+
- PostgreSQL
- Maven (or use included wrapper)

### 1. Clone the repository
```bash
git clone https://github.com/your-username/expense-tracker-backend.git
cd expense-tracker-backend
```

### 2. Create PostgreSQL database
```sql
CREATE DATABASE expense_tracker;
```

### 3. Set environment variables
```bash
# Required
export DB_PASSWORD=your_db_password
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_app_password

# Optional (have defaults)
export DB_URL=jdbc:postgresql://localhost:5432/expense_tracker
export DB_USERNAME=postgres
export JWT_SECRET=your_secret_key
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📖 API Documentation

Once running, access Swagger UI at:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

## 🔐 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login & get JWT token |

### Expenses (Requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/expenses` | Create expense |
| GET | `/expenses` | List expenses (paginated) |
| GET | `/expenses/{id}` | Get expense by ID |
| PUT | `/expenses/{id}` | Update expense |
| DELETE | `/expenses/{id}` | Delete expense |

### Files (Requires JWT)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/files/upload` | Upload file |
| GET | `/files` | List user's files |
| GET | `/files/{id}/download` | Download file |

### Analytics (Admin Only)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/analytics/total` | Get total expenses |
| GET | `/analytics/category-summary` | Get category breakdown |

## 🔑 Authentication

All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

## ⚙️ Configuration

Key configuration in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `jwt.expiration` | Token expiry (ms) | 86400000 (24h) |
| `app.rate-limit.requests-per-minute` | Rate limit | 100 |
| `spring.servlet.multipart.max-file-size` | Max upload size | 10MB |
| `app.email.enabled` | Enable email sending | true |

## 📧 Email Setup (Gmail)

1. Enable 2-Factor Authentication on your Google account
2. Generate an App Password: Google Account → Security → App passwords
3. Set environment variables:
```bash
export MAIL_USERNAME=your_email@gmail.com
export MAIL_PASSWORD=your_16_char_app_password
```