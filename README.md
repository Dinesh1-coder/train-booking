# 🚂 Train Ticket Booking System — Spring Boot

## Project Structure

```
train-booking/
├── pom.xml
└── src/main/
    ├── java/Train/
    │   ├── package-info.java       ← Your original file
    │   ├── Ticket.java             ← Your original file
    │   ├── Passenger.java          ← Your original file
    │   ├── TicketBooking.java      ← Your original file (minor change: fields made public)
    │   ├── TicketController.java   ← Your original file
    │   └── TrainApplication.java   ← NEW: Spring Boot entry point
    └── resources/
        ├── application.properties  ← NEW: Port configuration
        └── static/
            └── index.html          ← NEW: Frontend (calls the Java API using fetch())
```

## How to Run

### Prerequisites
- Java 17+
- Maven 3.6+

### Steps

```bash
# 1. Navigate to the project folder
cd train-booking

# 2. Build and run the application
mvn spring-boot:run
```

### Open in a Browser
```
http://localhost:8080
```
### Render Deployment Link 

https://train-booking-sshp.onrender.com

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST   | /api/tickets/book | Book a ticket |
| DELETE | /api/tickets/cancel/{id} | Cancel a ticket |
| GET    | /api/tickets/confirmed | List of confirmed tickets |
| GET    | /api/tickets/rac | List of RAC tickets |
| GET    | /api/tickets/waiting | List of waiting-list tickets |
| GET    | /api/tickets/availability | Check seat availability |
