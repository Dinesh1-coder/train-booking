package Train;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {
	
	@Autowired
	private GoogleSheetsService sheetsService;

    @PostMapping("/book")
    public Map<String, Object> bookTicket(@RequestBody Passenger passenger) {
        Map<String, Object> response = new HashMap<>();
        try {
        	sheetsService.appendBooking(passenger);
            TicketBooking.bookTicket(passenger);
            response.put("success", true);
            response.put("message", "Booking successful");
            response.put("passenger", passenger);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @DeleteMapping("/cancel/{id}")
    public Map<String, Object> cancelTicket(@PathVariable int id) {
        Map<String, Object> response = new HashMap<>();
        
        // PNR எடு cancel-க்கு முன்னாடி
        Passenger p = TicketBooking.getPassengerById(id);
        if (p != null) {
            sheetsService.deleteBooking(p.getPnr());
        }
        
        String result = TicketBooking.cancelTicketById(id);
        response.put("success", result.contains("Successful"));
        response.put("message", result);
        return response;
    }

    @GetMapping("/confirmed")
    public List<Passenger> getConfirmedTickets() {
        List<Passenger> allConfirmed = new ArrayList<>();
        for (String comp : TicketBooking.COMPARTMENTS) {
            for (char berth : TicketBooking.BERTHS) {
                allConfirmed.addAll(
                    TicketBooking.confirmedMap.get(comp + "-" + berth)
                );
            }
        }
        return allConfirmed;
    }

    @GetMapping("/rac")
    public Map<String, Queue<Passenger>> getRACTickets() {
        return TicketBooking.racQueues;
    }

    @GetMapping("/waiting")
    public Map<String, Queue<Passenger>> getWaitingTickets() {
        return TicketBooking.waitingQueues;
    }

    @GetMapping("/availability")
    public Map<String, Object> getAvailability() {
        Map<String, Object> availability = new HashMap<>();
        for (String comp : TicketBooking.COMPARTMENTS) {
            Map<String, Object> compData = new HashMap<>();
            for (char berth : TicketBooking.BERTHS) {
                int used = TicketBooking.confirmedMap
                    .get(comp + "-" + berth).size();
                compData.put(String.valueOf(berth),
                    TicketBooking.BERTH_CAPACITY_PER_TYPE - used);
            }
            compData.put("rac",
                TicketBooking.RAC_CAPACITY -
                TicketBooking.racQueues.get(comp).size());
            compData.put("waiting",
                TicketBooking.WAITING_CAPACITY -
                TicketBooking.waitingQueues.get(comp).size());
            availability.put(comp, compData);
        }
        return availability;
    }
}
