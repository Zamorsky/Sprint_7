import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class CourierAuthTest {

    private CourierClient courierClient;
    private Courier courier;

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = CourierGen.getRandomCourier(); // Генерация уникального курьера
        courierClient.createCourier(courier); // Создание курьера для тестов
    }

    @After
    public void tearDown() {
        // Удаление курьера после всех тестов
        Response responseLogin = courierClient.loginCourier(new CourierCreds(courier.getLogin(), courier.getPassword()));
        if (responseLogin.getStatusCode() == 200) {
            int courierId = getCourierId(responseLogin);
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    public void testCourierCanLogin() {
        // Успешный логин
        Response response = courierClient.loginCourier(new CourierCreds(courier.getLogin(), courier.getPassword()));
        response.then().statusCode(200)
                .and().body("id", equalTo(getCourierId(response)));
    }

    @Test
    public void testAuthorizationRequiresAllMandatoryFields() {
        // Проверка авторизации с пустым логином
        Response responseWithoutLogin = courierClient.loginCourier(new CourierCreds(null, courier.getPassword()));
        assertEquals("Ошибка! Запрос прошел, хотя не должно быть.", 400, responseWithoutLogin.statusCode());

        // Проверка авторизации с пустым паролем
        Response responseWithoutPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), null));
        assertEquals("Ошибка! Запрос прошел, хотя не должно быть.", 400, responseWithoutPassword.statusCode());
    }

    @Test
    public void testLoginWithInvalidCredentialsReturnsError() {
        // Проверка авторизации с неправильным логином
        Response responseInvalidLogin = courierClient.loginCourier(new CourierCreds("invalidLogin", courier.getPassword()));
        responseInvalidLogin.then().statusCode(401)
                .and().body("message", equalTo("Учетные данные неверны"));

        // Проверка авторизации с неправильным паролем
        Response responseInvalidPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), "invalidPassword"));
        responseInvalidPassword.then().statusCode(401)
                .and().body("message", equalTo("Учетные данные неверны"));
    }

    @Test
    public void testLoginForNonExistingUserReturnsError() {
        // Проверка авторизации для несуществующего пользователя
        Response response = courierClient.loginCourier(new CourierCreds("nonExistingLogin", "anyPassword"));
        response.then().statusCode(404)
                .and().body("message", equalTo("Курьер не найден"));
    }

    @Step("Получить courierId")
    public int getCourierId(Response responseLogin) {
        // Извлечение id из JSON-ответа
        return responseLogin.then().extract().path("id");
    }
}
