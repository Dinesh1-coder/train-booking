package Train;

public class Passenger {

    private static int idProvider = 1;

    private int id;
    private String name;
    private int age;
    private String from;
    private String to;
    private String compartment;     // "Sleeper" or "General"
    private char berthPreference;   // 'U', 'M', 'L', 'N'
    private String seatLabel;       // e.g., "Sleeper-U1"
    private long pnr;
    private String ticketType;      // "Confirmed", "RAC", "Waiting"

    public Passenger(String name, int age, String from, String to, String compartment, char berthPreference) {
        this.id = idProvider++;
        this.name = name;
        this.age = age;
        this.from = from;
        this.to = to;
        this.compartment = compartment;
        this.berthPreference = berthPreference;
        this.seatLabel = null;
        this.pnr = Ticket.generatePNR();
        this.ticketType = "";
    }

    // getters & setters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getCompartment() { return compartment; }
    public char getBerthPreference() { return berthPreference; }
    public String getSeatLabel() { return seatLabel; }
    public long getPnr() { return pnr; }
    public String getTicketType() { return ticketType; }
    

    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setCompartment(String compartment) { this.compartment = compartment; }
    public void setBerthPreference(char berthPreference) { this.berthPreference = berthPreference; }
    public void setSeatLabel(String seatLabel) { this.seatLabel = seatLabel; }
    public void setTicketType(String ticketType) { this.ticketType = ticketType; }

    @Override
    public String toString() {
        return "-------------------------------" +
               "\nPNR        : " + pnr +
               "\nPassengerID: " + id +
               "\nName       : " + name +
               "\nAge        : " + age +
               "\nFrom       : " + from +
               "\nTo         : " + to +
               "\nTicketType : " + ticketType +
               "\nCompartment: " + compartment +
               "\nBerthPref  : " + berthPreference +
               "\nSeatLabel  : " + (seatLabel == null ? "-" : seatLabel) +
               "\n-------------------------------";
    }
}