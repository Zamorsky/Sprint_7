import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

//в этом классе пишем запросы которые нужны для взаимодействия с курьерами
public class CourierClient extends BaseClient {
    private static final String COURIER_ENDPOINT = "api/v1/courier/";
    private static final String LOGIN_ENDPOINT = "api/v1/courier/login";

    @Step("Создаём курьера")
    public Response createCourier(Courier courier) {
        // Отправляем POST-запрос для создания курьера и возвращаем Response
        return given()
                .spec(getBaseSpec())
                .body(courier)
                .when()
                .post(COURIER_ENDPOINT);
    }

    @Step("Удаляем курьера")
    public void deleteCourier(int courierId) {
        // Отправляем DELETE-запрос для удаления курьера по его ID
        given()
                .spec(getBaseSpec())
                .delete(COURIER_ENDPOINT + courierId);
    }

    @Step("Логинимся курьером")
    public Response loginCourier(CourierCreds courierCreds) {
        // Отправляем POST-запрос для входа курьера и возвращаем Response
        return given()
                .spec(getBaseSpec())
                .body(courierCreds)
                .when()
                .post(LOGIN_ENDPOINT);
    }
}
