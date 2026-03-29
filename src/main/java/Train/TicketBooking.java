package Train;

import java.util.*;

public class TicketBooking {

    private static final List<String> PLACES = Arrays.asList("Chennai", "Bangalore", "Hyderabad", "Mumbai", "Delhi");
    public static final String[] COMPARTMENTS = {"Sleeper", "General"};
    public static final char[] BERTHS = {'U', 'M', 'L'};

    public static final int BERTH_CAPACITY_PER_TYPE = 2;
    public static final int RAC_CAPACITY = 2;
    public static final int WAITING_CAPACITY = 2;

    public static final Map<String, List<Passenger>> confirmedMap = new HashMap<>();
    private static final Map<String, Integer> berthCounters = new HashMap<>();
    public static final Map<String, Queue<Passenger>> racQueues = new HashMap<>();
    public static final Map<String, Queue<Passenger>> waitingQueues = new HashMap<>();
    private static final Map<Integer, Passenger> allPassengers = new HashMap<>();
    private static final Random random = new Random();

    static {
        for (String comp : COMPARTMENTS) {
            for (char b : BERTHS) {
                String key = keyFor(comp, b);
                confirmedMap.put(key, new ArrayList<>());
                berthCounters.put(key, 1);
            }
            racQueues.put(comp, new LinkedList<>());
            waitingQueues.put(comp, new LinkedList<>());
        }
    }

    public static void showPlaces() {
        System.out.println("\nAvailable places:");
        PLACES.forEach(p -> System.out.println("- " + p));
    }

    public static void showBerthOptions() {
        System.out.println("\nBerth options:");
        System.out.println("U -> Upper");
        System.out.println("M -> Middle");
        System.out.println("L -> Lower");
        System.out.println("N -> No berth (auto-random assign)");
    }

    public static void bookTicket(Passenger p) {
        allPassengers.put(p.getId(), p);

        String comp = capitalize(p.getCompartment());
        if (!isValidCompartment(comp)) {
            System.out.println("Invalid compartment. Use 'Sleeper' or 'General'. Booking aborted.");
            allPassengers.remove(p.getId());
            return;
        }

        char pref = Character.toUpperCase(p.getBerthPreference());
        if (pref == 'U' || pref == 'M' || pref == 'L') {
            boolean allocated = tryAllocateSpecificOrAnyInCompartment(p, comp, pref);
            if (!allocated) enqueueRacOrWaiting(p, comp);
        } else if (pref == 'N') {
            boolean allocated = allocateRandomAvailableInCompartment(p, comp);
            if (!allocated) enqueueRacOrWaiting(p, comp);
        } else {
            System.out.println("Invalid berth preference. Use U/M/L/N.");
            allPassengers.remove(p.getId());
        }
    }

    private static boolean tryAllocateSpecificOrAnyInCompartment(Passenger p, String comp, char requested) {
        if (isBerthAvailable(comp, requested)) {
            allocateSeat(p, comp, requested);
            return true;
        }
        List<Character> other = new ArrayList<>();
        for (char b : BERTHS) if (b != requested) other.add(b);
        Collections.shuffle(other);
        for (char b : other) {
            if (isBerthAvailable(comp, b)) {
                allocateSeat(p, comp, b);
                return true;
            }
        }
        return false;
    }

    private static boolean allocateRandomAvailableInCompartment(Passenger p, String comp) {
        List<Character> available = new ArrayList<>();
        for (char b : BERTHS) {
            if (isBerthAvailable(comp, b)) available.add(b);
        }
        if (available.isEmpty()) return false;
        char pick = available.get(random.nextInt(available.size()));
        allocateSeat(p, comp, pick);
        return true;
    }

    private static boolean isBerthAvailable(String comp, char berth) {
        return confirmedMap.get(keyFor(comp, berth)).size() < BERTH_CAPACITY_PER_TYPE;
    }

    private static void allocateSeat(Passenger p, String comp, char berth) {
        String key = keyFor(comp, berth);
        int nextNo = berthCounters.get(key);
        String seatLabel = comp + "-" + berth + nextNo;
        berthCounters.put(key, nextNo + 1);

        p.setTicketType("Confirmed");
        p.setCompartment(comp);
        p.setSeatLabel(seatLabel);
        confirmedMap.get(key).add(p);

        System.out.println("\n✅ Booking Confirmed!");
        System.out.println(p);
    }

    private static void enqueueRacOrWaiting(Passenger p, String comp) {
        Queue<Passenger> rac = racQueues.get(comp);
        Queue<Passenger> waiting = waitingQueues.get(comp);

        if (rac.size() < RAC_CAPACITY) {
            p.setTicketType("RAC");
            rac.add(p);
            System.out.println("\nℹ️  Added to RAC (compartment: " + comp + "). Passenger ID: " + p.getId());
            System.out.println(p);
        } else if (waiting.size() < WAITING_CAPACITY) {
            p.setTicketType("Waiting");
            waiting.add(p);
            System.out.println("\nℹ️  Added to Waiting list (compartment: " + comp + "). Passenger ID: " + p.getId());
            System.out.println(p);
        } else {
            System.out.println("\n❌ No tickets available in " + comp + " (Confirmed, RAC and Waiting are full).");
            allPassengers.remove(p.getId());
        }
    }

    public static String cancelTicketById(int id) {
        Passenger p = allPassengers.get(id);
        if (p == null) return "Invalid Id: No such passenger found.";

        String type = p.getTicketType();
        if (type == null || type.isEmpty()) return "Passenger has no ticket assigned.";

        String comp = p.getCompartment();

        if ("Confirmed".equalsIgnoreCase(type)) {
            String berthKey = findBerthKeyForPassenger(p);
            if (berthKey == null) {
                allPassengers.remove(id);
                return "Error: confirmed passenger record not found; cleaned up.";
            }

            String freedSeatLabel = p.getSeatLabel();
            confirmedMap.get(berthKey).remove(p);
            allPassengers.remove(id);
            System.out.println("\n✅ Confirmed ticket cancelled for Passenger ID: " + id);

            Queue<Passenger> rac = racQueues.get(comp);
            if (!rac.isEmpty()) {
                Passenger racPassenger = rac.poll();
                racPassenger.setTicketType("Confirmed");
                racPassenger.setCompartment(comp);
                racPassenger.setSeatLabel(freedSeatLabel);

                char berthChar = parseBerthCharFromSeatLabel(freedSeatLabel);
                confirmedMap.get(keyFor(comp, berthChar)).add(racPassenger);

                System.out.println("\n🔁 RAC passenger promoted to Confirmed (reused seat):");
                System.out.println(racPassenger);

                Queue<Passenger> waiting = waitingQueues.get(comp);
                if (!waiting.isEmpty()) {
                    Passenger waitingPassenger = waiting.poll();
                    waitingPassenger.setTicketType("RAC");
                    rac.add(waitingPassenger);
                    System.out.println("\n🔁 Waiting passenger moved to RAC:");
                    System.out.println(waitingPassenger);
                }
            } else {
                System.out.println("\n(No RAC to promote; seat is now free for future bookings.)");
            }
            return "Cancellation Successful (Confirmed).";
        }
        else if ("RAC".equalsIgnoreCase(type)) {
            Queue<Passenger> rac = racQueues.get(comp);
            boolean removed = rac.remove(p);
            allPassengers.remove(id);
            System.out.println("\n✅ RAC ticket cancelled for Passenger ID: " + id);

            Queue<Passenger> waiting = waitingQueues.get(comp);
            if (!waiting.isEmpty()) {
                Passenger waitingPassenger = waiting.poll();
                waitingPassenger.setTicketType("RAC");
                rac.add(waitingPassenger);
                System.out.println("\n🔁 Waiting passenger moved to RAC:");
                System.out.println(waitingPassenger);
            }
            return removed ? "Cancellation Successful (RAC)." : "RAC cancellation: record not found in queue.";
        }
        else if ("Waiting".equalsIgnoreCase(type)) {
            Queue<Passenger> waiting = waitingQueues.get(comp);
            boolean removed = waiting.remove(p);
            allPassengers.remove(id);
            System.out.println("\n✅ Waiting ticket cancelled for Passenger ID: " + id);
            return removed ? "Cancellation Successful (Waiting)." : "Waiting cancellation: record not found in queue.";
        }
        else {
            return "Unknown ticket type.";
        }
    }

    private static String findBerthKeyForPassenger(Passenger p) {
        for (Map.Entry<String, List<Passenger>> e : confirmedMap.entrySet()) {
            if (e.getValue().contains(p)) return e.getKey();
        }
        return null;
    }

    private static char parseBerthCharFromSeatLabel(String seatLabel) {
        if (seatLabel == null) return 'U';
        int dash = seatLabel.indexOf('-');
        if (dash >= 0 && dash + 1 < seatLabel.length()) return seatLabel.charAt(dash + 1);
        return 'U';
    }

    public static void displayConfirmed() {
        System.out.println("\n====== Confirmed Passengers (by compartment & berth) ======");
        for (String comp : COMPARTMENTS) {
            System.out.println("\n== " + comp + " ==");
            for (char b : BERTHS) {
                System.out.println("\n-- Berth " + b + " --");
                List<Passenger> list = confirmedMap.get(keyFor(comp, b));
                if (list.isEmpty()) System.out.println("No passengers");
                else list.forEach(System.out::println);
            }
        }
    }

    public static void displayRAC() {
        System.out.println("\n====== RAC Queues ======");
        for (String comp : COMPARTMENTS) {
            System.out.println("\n-- " + comp + " RAC --");
            Queue<Passenger> q = racQueues.get(comp);
            if (q.isEmpty()) System.out.println("RAC empty");
            else q.forEach(System.out::println);
        }
    }

    public static void displayWaiting() {
        System.out.println("\n====== Waiting Queues ======");
        for (String comp : COMPARTMENTS) {
            System.out.println("\n-- " + comp + " Waiting --");
            Queue<Passenger> q = waitingQueues.get(comp);
            if (q.isEmpty()) System.out.println("Waiting empty");
            else q.forEach(System.out::println);
        }
    }

    public static void showAvailability() {
        System.out.println("\n====== Availability Summary ======");
        for (String comp : COMPARTMENTS) {
            System.out.println("\n-- " + comp + " --");
            for (char b : BERTHS) {
                int used = confirmedMap.get(keyFor(comp, b)).size();
                System.out.println("Berth " + b + " : " + used + " / " + BERTH_CAPACITY_PER_TYPE);
            }
            System.out.println("RAC : " + racQueues.get(comp).size() + " / " + RAC_CAPACITY);
            System.out.println("Waiting : " + waitingQueues.get(comp).size() + " / " + WAITING_CAPACITY);
        }
    }

    private static String keyFor(String comp, char berth) { return comp + "-" + berth; }
    private static boolean isValidCompartment(String comp) {
        for (String c : COMPARTMENTS) if (c.equalsIgnoreCase(comp)) return true;
        return false;
    }
    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }

public static Passenger getPassengerById(int id) {
    return allPassengers.get(id);
}
}