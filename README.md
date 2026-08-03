# RentWheels — Spring Boot

Web version of the RentWheels vehicle rental management system.

## Requirements

- Java 21+
- Maven 3.8+
- MySQL 8+ (local server running)

## MySQL installation

### macOS (Homebrew)

```bash
brew install mysql
brew services start mysql
```

### macOS (Oracle MySQL installer)

Install MySQL Community Server from [https://dev.mysql.com/downloads/mysql/](https://dev.mysql.com/downloads/mysql/), then start it from System Settings → MySQL.

### Verify MySQL is running

```bash
mysql -u root -e "SELECT VERSION();"
```

If your root user has a password, use:

```bash
mysql -u root -p
```

## Database creation

Create the `rentwheels` schema once:

```sql
CREATE DATABASE IF NOT EXISTS rentwheels
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

From the terminal:

```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS rentwheels CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

Hibernate will create/update all tables automatically on startup (`spring.jpa.hibernate.ddl-auto=update`).

## Configuration

Active profile: **dev** (`spring.profiles.active=dev` in `application.properties`).

MySQL settings live in `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rentwheels
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

If your MySQL `root` user has a password, set it in `application-dev.properties`:

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## Run

```bash
cd rental-wheels-springboot
mvn spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080)

## Default logins

| Username  | Password | Role     |
|-----------|----------|----------|
| admin     | 1234     | ADMIN    |
| employee  | 1234     | EMPLOYEE |

Demo users are created only if missing. Demo customers/vehicles are seeded only when those tables are empty.

Reports Analytics is visible only to **ADMIN**.

## Features

- Login with Spring Security (ADMIN / EMPLOYEE)
- Dashboard stats (vehicles, customers)
- Vehicle CRUD + image upload + filters
- Customer CRUD + search
- Rent vehicle (available only) with live day/rent calculation
- Return vehicle with late fine (**Rs. 1000/day**)
- Billing invoices + CSV export
- Admin reports (inventory, utilization, revenue)

## Data

- MySQL database: `rentwheels` on `localhost:3306`
- Uploaded vehicle images: `./uploads/images/`
