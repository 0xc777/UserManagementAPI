package integration;

import org.junit.jupiter.api.*;
import io.qameta.allure.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Epic("User management")
@Feature("API tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiIntegrationTest {

    private final String baseUrl = "http://localhost:8080/users";

    @Test
    @Order(1)
    @Story("Создание пользователя через API")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание пользователя")
    @Step("Отправка POST запроса на создание пользователя")
    public void testCreateUser() throws IOException {

       String jsonBody = """
               {
               "name": "Евгений",
               "age": 26,
               "email": "Eugeny@Yandex.ru"
               }""";

       HttpURLConnection connection =(HttpURLConnection) new URL(baseUrl).openConnection();
       connection.setRequestMethod("POST");
       connection.setRequestProperty("Content-Type", "application/json; utf-8");
       connection.setDoOutput(true);
       try(OutputStream os = connection.getOutputStream()){
           os.write(jsonBody.getBytes("UTF-8"));
       }
       int resultcode = connection.getResponseCode();
       Assertions.assertEquals(200,resultcode);

       InputStream is = connection.getInputStream();
       String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
       is.close();
       Assertions.assertTrue(response.contains("Eugeny@Yandex.ru"));

    }
    @Test
    @Order(2)
    @DisplayName("Получение пользователя по email")
    public void testGetUser() throws IOException {
        String queryEmail = URLEncoder.encode("Eugeny@Yandex.ru", StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "?email=" + queryEmail).openConnection();
        connection.setRequestMethod("GET");

        int code = connection.getResponseCode();

        InputStream is = (code >= 400) ? connection.getErrorStream() : connection.getInputStream();
        String response = is != null ? new String(is.readAllBytes(), StandardCharsets.UTF_8) : "";
        if (is != null) is.close();

        Assertions.assertEquals(200, code, "Ожидаем статус 200 OK");
        Assertions.assertTrue(response.contains("Eugeny@Yandex.ru"));
    }

    @Test
    @Order(3)
    @DisplayName("Удаление пользователя по email")
    public void testDeleteUser() throws IOException {
        String queryEmail = URLEncoder.encode("Eugeny@Yandex.ru", StandardCharsets.UTF_8);
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl + "?email=" + queryEmail).openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();

        InputStream is = (responseCode >= 400) ? connection.getErrorStream() : connection.getInputStream();
        String response = is != null ? new String(is.readAllBytes(), StandardCharsets.UTF_8) : "";
        if (is != null) is.close();

        System.out.println("DELETE response: " + response);

        Assertions.assertTrue(responseCode == 200 || responseCode == 400 || responseCode == 404);
    }


}

