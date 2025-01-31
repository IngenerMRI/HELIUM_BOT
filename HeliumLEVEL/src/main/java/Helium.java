import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;

public class Helium extends TelegramLongPollingBot {

    private static final String SPREADSHEET_ID = "1_3ipM7yV6UDv7UVa1xaHDapmBgjWOWFnzdkGKYFm1-c"; // Замените на ID вашей таблицы
    private static final String RANGE = "Sheet1!A1:Z"; // Замените на ваш диапазон

    private Sheets sheetsService;

    public Helium() throws GeneralSecurityException, IOException {
        sheetsService = getSheetsService();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String city = update.getMessage().getFrom().getUserName();
            String messageText = update.getMessage().getText();

            // Запись данных в Google Sheets
            try {
                writeDataToSheet(city, messageText);
                sendResponse(update.getMessage().getChatId(), "Данные успешно записаны!");
            } catch (IOException e) {
                e.printStackTrace();
                sendResponse(update.getMessage().getChatId(), "Ошибка записи данных.");
            }
        }
    }

    private void sendResponse(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDataToSheet(String city, String level) throws IOException {
        // Получаем текущую дату
        String date = java.time.LocalDate.now().toString();

        // Определяем колонку по городу
        int columnIndex = getColumnIndex(city);
        if (columnIndex == -1) return; // Город не найден

        // Записываем данные
        ValueRange body = new ValueRange()
                .setValues(Collections.singletonList(
                        Arrays.asList(date, level)
                ));
        sheetsService.spreadsheets().values().append(SPREADSHEET_ID, RANGE + columnIndex, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private int getColumnIndex(String city) {
        switch (city) {
            case "Москва": return 1;
            case "Казань": return 2;
            case "Чебоксары": return 3;
            case "Ульяновск": return 4;
            case "Самара": return 5;
            case "Уфа": return 6;
            case "Ижевск": return 7;
            case "Глазов": return 8;
            case "Пермь": return 9;
            case "Пермь ТРИО": return 10;
            case "Екатеринбург 1": return 11;
            case "Екатеринбург 2": return 12;
            case "Екатеринбург ПРИЗМА": return 13;
            case "Челябинск": return 14;
            case "Тюмень": return 15;
            case "Омск": return 16;
            case "Новосибирск": return 17;
            default: return -1; // Город не найден
        }
    }

    @Override
    public String getBotUsername() {
        return "HELIUMmriEXPRESS_bot"; // Ваше имя бота
    }

    @Override
    public String getBotToken() {
        return "7735363342:AAEM034-183sngwTYZ569PxwJPaGQsjy4pY"; // Токен вашего бота
    }

    private Sheets getSheetsService() throws GeneralSecurityException, IOException {
        // Здесь необходимо добавить код для инициализации Google Sheets API
        // Используйте credentials.json для авторизации

        // Пример кода для инициализации Google Sheets API
        FileInputStream serviceAccountStream = new FileInputStream("credentials.json");
        GoogleCredential credential = GoogleCredential.fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential)
                .setApplicationName("Helium Level Bot")
                .build();
    }
}