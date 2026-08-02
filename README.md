# RentWheels — Spring Boot

Web version of the RentWheels vehicle rental management system (same features as the Swing desktop app).

## Requirements

- Java 21+
- Maven 3.8+

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

- H2 file database at `./data/rentwheelsdb` (persists across restarts)
- H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  JDBC URL: `jdbc:h2:file:./data/rentwheelsdb`
- Uploaded vehicle images: `./uploads/images/`
