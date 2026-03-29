package Train;

import java.util.Random;

/**
 * Small utility for PNR generation.
 */
public class Ticket {

    public static long generatePNR() {
        Random r = new Random();
        // 9-digit PNR for readability
        return 100_000_000L + r.nextInt(900_000_000);
    }
}