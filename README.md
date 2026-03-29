# 🚂 Train Ticket Booking System — Spring Boot

## Project Structure

```
train-booking/
├── pom.xml
└── src/main/
    ├── java/Train/
    │   ├── package-info.java       ← உங்க original file
    │   ├── Ticket.java             ← உங்க original file
    │   ├── Passenger.java          ← உங்க original file
    │   ├── TicketBooking.java      ← உங்க original file (minor: fields made public)
    │   ├── TicketController.java   ← உங்க original file
    │   └── TrainApplication.java   ← NEW: Spring Boot entry point
    └── resources/
        ├── application.properties  ← NEW: port config
        └── static/
            └── index.html          ← NEW: Frontend (Java API-ஐ fetch() மூலம் call பண்றது)
```

## Run பண்ண

### Prerequisites
- Java 17+
- Maven 3.6+

### Steps

```bash
# 1. Project folder-க்கு போ
cd train-booking

# 2. Build & Run
mvn spring-boot:run
```

### Browser-ல open பண்ண
```
http://localhost:8080
```

## API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST   | /api/tickets/book | Ticket book பண்ண |
| DELETE | /api/tickets/cancel/{id} | Ticket cancel பண்ண |
| GET    | /api/tickets/confirmed | Confirmed list |
| GET    | /api/tickets/rac | RAC list |
| GET    | /api/tickets/waiting | Waiting list |
| GET    | /api/tickets/availability | Seat availability |

## Changes made to your code

Only **one change** in `TicketBooking.java`:
- `private` fields → `public` (so `TicketController` can access them)
- `COMPARTMENTS`, `BERTHS`, `BERTH_CAPACITY_PER_TYPE`, `RAC_CAPACITY`, `WAITING_CAPACITY`, `confirmedMap`, `racQueues`, `waitingQueues`

Everything else is **your original code, unchanged**.
