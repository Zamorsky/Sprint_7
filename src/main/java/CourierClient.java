package Clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class CourierClient extends BaseClient {
    private static final String COURIER_ENDPOINT = "api/v1/courier/";
    private static final String LOGIN_ENDPOINT = "api/v1/courier/login";

    @Step("Создаём курьера")
    public Response create(Courier courier) {
        // Отправляем POST-запрос для создания курьера и возвращаем Response
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post(COURIER_ENDPOINT);
    }

    @Step("Удаляем курьера")

    @Step("Логинимся курьером")

}
