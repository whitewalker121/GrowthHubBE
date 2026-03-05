# growthhub Salon — Spring Boot REST API

## Stack
- **Java 17** + Spring Boot 3.2
- **PostgreSQL 15+**
- **JWT** authentication (jjwt 0.11.5)
- **Spring Data JPA** + Hibernate
- **Swagger UI** at `/swagger-ui.html`

## Quick Start

```bash
# 1. Create database
psql -U postgres -c "CREATE DATABASE growthhub_salon;"

# 2. Run schema (creates all tables + seed admin user)
psql -U postgres -d growthhub_salon -f src/main/resources/schema.sql

# 3. Edit DB credentials
#    src/main/resources/application.yml -> spring.datasource.password

# 4. First run: set ddl-auto: create, then switch to validate
mvn spring-boot:run
```

## Default Credentials
| Username | Password | Role  |
|----------|----------|-------|
| admin    | Admin@123 | ADMIN |

## API Modules & Endpoints

### Auth
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/auth/login | Login → JWT token |

### Dashboard
| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/v1/dashboard/stats | KPIs + revenue chart |

### Clients
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/clients | Create client |
| GET | /api/v1/clients?search=&page=&size= | List / search |
| GET | /api/v1/clients/{id} | Get by ID |
| PUT | /api/v1/clients/{id} | Update |
| DELETE | /api/v1/clients/{id} | Soft-delete |

### Staff
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/staff | Add staff member |
| GET | /api/v1/staff?status= | List |
| GET | /api/v1/staff/{id} | Get by ID |
| PUT | /api/v1/staff/{id} | Update |
| PATCH | /api/v1/staff/{id}/status?status= | Change status |

### Services
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/services | Create service |
| GET | /api/v1/services?status= | List |
| PUT | /api/v1/services/{id} | Update |
| DELETE | /api/v1/services/{id} | Deactivate |
| GET | /api/v1/services/categories | List categories |
| POST | /api/v1/services/categories | Create category |

### Appointments
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/appointments | Book appointment |
| GET | /api/v1/appointments?date= | By date |
| GET | /api/v1/appointments?from=&to= | Date range |
| PATCH | /api/v1/appointments/{id}/status | Update status |
| DELETE | /api/v1/appointments/{id} | Cancel |
| GET | /api/v1/appointments/client/{clientId} | Client history |

### Invoices / POS
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/invoices | Create bill (auto-earns loyalty pts) |
| GET | /api/v1/invoices/{id} | Get invoice |
| GET | /api/v1/invoices?from=&to= | Date range |
| GET | /api/v1/invoices/client/{clientId} | Client bills |

### Inventory
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/inventory | Add item |
| GET | /api/v1/inventory?category=&status=&page= | List |
| PUT | /api/v1/inventory/{id} | Update |
| PATCH | /api/v1/inventory/{id}/restock?quantity= | Add stock |
| GET | /api/v1/inventory/low-stock | Low/out-of-stock list |
| DELETE | /api/v1/inventory/{id} | Delete |

### Attendance
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/attendance | Mark attendance |
| GET | /api/v1/attendance?date= | By date |
| GET | /api/v1/attendance/staff/{id}?from=&to= | Staff history |
| PUT | /api/v1/attendance/{id} | Update record |

### Expenses
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/expenses | Record expense |
| GET | /api/v1/expenses?category=&from=&to= | List |
| PUT | /api/v1/expenses/{id} | Update |
| PATCH | /api/v1/expenses/{id}/approve?approverStaffId= | Approve |
| PATCH | /api/v1/expenses/{id}/reject | Reject |
| DELETE | /api/v1/expenses/{id} | Delete |

### Loyalty & Rewards
| Method | URL | Description |
|--------|-----|-------------|
| POST | /api/v1/loyalty/programs | Create program |
| GET | /api/v1/loyalty/programs | List programs |
| PATCH | /api/v1/loyalty/programs/{id}/toggle | Toggle active |
| POST | /api/v1/loyalty/packages | Create package |
| GET | /api/v1/loyalty/packages | List packages |
| POST | /api/v1/loyalty/memberships/sell | Sell to client |
| GET | /api/v1/loyalty/accounts/{clientId} | Points balance |
| POST | /api/v1/loyalty/points/adjust | Manual adjust |
| GET | /api/v1/loyalty/transactions/{clientId} | History |
| POST | /api/v1/loyalty/vouchers | Issue voucher |
| GET | /api/v1/loyalty/vouchers | List all |
| GET | /api/v1/loyalty/vouchers/check/{code} | Check code |
| POST | /api/v1/loyalty/vouchers/redeem | Redeem |

## Authentication
All endpoints (except `/api/v1/auth/**`) require:
```
Authorization: Bearer <token>
```

## Response Format
```json
{
  "success": true,
  "message": "OK",
  "data": { ... },
  "timestamp": "2025-02-03T10:00:00"
}
```
# GrowthHubBE
