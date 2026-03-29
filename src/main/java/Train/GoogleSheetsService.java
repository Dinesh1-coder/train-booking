package Train;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleSheetsService {

    private static final String SHEET_ID = "1lXTDJrJTZBg-_0x_7qz_jHxdXY4NV8XDGFpenB4WrKE";
    private static final String RANGE = "Sheet1!A:H";
    private static final String APP_NAME = "TrainTicketBooking";

    private Sheets getSheetsService() throws Exception {
        String credentialsJson = System.getenv("GOOGLE_CREDENTIALS");
        GoogleCredentials credentials;
        if (credentialsJson != null) {
            // Render cloud - environment variable use பண்ணு
            credentials = GoogleCredentials
                    .fromStream(new java.io.ByteArrayInputStream(credentialsJson.getBytes()))
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        } else {
            // Local - credentials.json file use பண்ணு
            InputStream in = getClass().getResourceAsStream("/credentials.json");
            credentials = GoogleCredentials
                    .fromStream(in)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        }
        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APP_NAME)
                .build();
    }

    public void appendBooking(Passenger p) {
        try {
            Sheets service = getSheetsService();
            List<Object> row = Arrays.asList(
                    p.getPnr(), p.getId(), p.getName(), p.getAge(),
                    p.getFrom(), p.getTo(), p.getCompartment(), p.getTicketType()
            );
            ValueRange body = new ValueRange().setValues(Collections.singletonList(row));
            service.spreadsheets().values()
                    .append(SHEET_ID, RANGE, body)
                    .setValueInputOption("RAW")
                    .execute();
            System.out.println("Saved to Google Sheets: " + p.getName());
        } catch (Exception e) {
            System.out.println("Google Sheets error: " + e.getMessage());
        }
    }

    public void deleteBooking(long pnr) {
        try {
            Sheets service = getSheetsService();
            ValueRange response = service.spreadsheets().values()
                    .get(SHEET_ID, RANGE)
                    .execute();
            List<List<Object>> rows = response.getValues();
            if (rows == null) return;
            for (int i = 0; i < rows.size(); i++) {
                if (!rows.get(i).isEmpty() &&
                    rows.get(i).get(0).toString().equals(String.valueOf(pnr))) {
                    List<Object> emptyRow = Arrays.asList("", "", "", "", "", "", "", "");
                    ValueRange body = new ValueRange()
                            .setValues(Collections.singletonList(emptyRow));
                    String clearRange = "Sheet1!A" + (i+1) + ":H" + (i+1);
                    service.spreadsheets().values()
                            .update(SHEET_ID, clearRange, body)
                            .setValueInputOption("RAW")
                            .execute();
                    System.out.println("Deleted from Sheets: PNR " + pnr);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Sheets delete error: " + e.getMessage());
        }
    }
}