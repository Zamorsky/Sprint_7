import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class CourierAuthTest {

    private CourierClient courierClient;
    private Courier courier;
    private CourierCreds courierCreds;

    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = CourierGen.getRandomCourier(); // Генерация уникального курьера
        courierClient.createCourier(courier);
        courierCreds = new CourierCreds(courier.getLogin(), courier.getPassword());// Создание курьера для тестов
    }

    @After
    public void tearDown() {
        // Удаление курьера после всех тестов
        Response responseLogin = courierClient.loginCourier(courierCreds);
        if (responseLogin.getStatusCode() == 200) {
            int courierId = getCourierId(responseLogin);
            courierClient.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Проверка - курьер может авторизоваться и успешный запрос возвращает id")
    public void testCourierCanLogin() {
        // Успешный логин
        Response response = courierClient.loginCourier(courierCreds);
        response.then().statusCode(200)
                .and().body("id", equalTo(getCourierId(response)));
    }

    @Test
    @DisplayName("Проверка - для авторизации нужно передать все обязательные поля")
    public void testAuthorizationRequiresAllMandatoryFields() {
        // Проверка авторизации с пустым логином
        Response responseWithoutLogin = courierClient.loginCourier(new CourierCreds(null, courier.getPassword()));
        assertEquals("Ошибка! Код возврата отличается от 400", 400, responseWithoutLogin.statusCode());

        // Проверка авторизации с пустым паролем
        Response responseWithoutPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), null));
        assertEquals("Ошибка! Код возврата отличается от 400", 400, responseWithoutPassword.statusCode());
    }

    @Test
    @DisplayName("Проверка - система вернёт ошибку, если неправильно указать логин или пароль")
    @Issue("Бекэнд отваливается по таймауту с ошибкой 504.")
    public void testLoginWithInvalidCredentialsReturnsError() {
        // Проверка авторизации с неправильным логином
        Response responseInvalidLogin = courierClient.loginCourier(new CourierCreds("invalidLogin", courier.getPassword()));
        responseInvalidLogin.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));

        // Проверка авторизации с неправильным паролем
        Response responseInvalidPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), "invalidPassword"));
        responseInvalidPassword.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Проверка - если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void testLoginForNonExistingUserReturnsError() {
        // Проверка авторизации для несуществующего пользователя
        Response response = courierClient.loginCourier(new CourierCreds("123nonExistingLogin123", "123anyPassword321"));
        response.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));
    }

    @Step("Получить courierId")
    @DisplayName("Проверка - для авторизации нужно передать все обязательные поля")
    public int getCourierId(Response responseLogin) {
        // Извлечение id из JSON-ответа
        return responseLogin.then().extract().path("id");
    }
}
