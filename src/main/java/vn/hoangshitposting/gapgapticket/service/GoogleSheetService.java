package vn.hoangshitposting.gapgapticket.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class GoogleSheetService {

    private static final String APPLICATION_NAME = "Spring Sheets API";
    private static final String SPREADSHEET_ID = "1D-LIdRIvcGN2R4sti-fCSp7tKcSBkXCTQnvmnXOWaCk";

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = ServiceAccountCredentials
                .fromStream(new FileInputStream("service-account.json"))
                .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void appendRow(SpreadSheet sheet, List<Object> rowData) throws IOException, GeneralSecurityException {
        Sheets sheetsService = getSheetsService();

        String range = "Sheet1!A:A"; // Check column A (or the whole sheet)
        ValueRange response = sheetsService.spreadsheets().values()
                .get(sheet.getId(), range)
                .execute();

        LinkedList<Object> linkedList = new LinkedList<>(rowData);
        linkedList.addFirst(response.values().size());

        ValueRange body = new ValueRange()
                .setValues(List.of(linkedList)); // Single row

        AppendValuesResponse result = sheetsService.spreadsheets().values()
                .append(SPREADSHEET_ID, "Sheet1!A1", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .execute();

        System.out.println("Row inserted: " + result.getUpdates().getUpdatedRange());
    }

    @Getter
    public static enum SpreadSheet {
        TICKET("1D-LIdRIvcGN2R4sti-fCSp7tKcSBkXCTQnvmnXOWaCk"),

        MERCH("1OVfjL-hhK3Lihoev9araFSCH5Vowakfwa6_9ccWmTYQ"),

        GALLERY("1hpRtu4etGMsl_pYzwH9W7xf0EZbjM_GVMhsWxb2Hiwo");

        String id;

        private SpreadSheet(String id) {
            this.id = id;
        }
    }


}
