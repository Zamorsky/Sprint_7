import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;


public class CourierCreatingTest {

    private CourierClient courierClient;
    private Courier courier;
    private CourierCreds courierCreds;


    @Before
    public void setup() {
        courierClient = new CourierClient();
        courier = CourierGen.getRandomCourier();
        courierCreds = new CourierCreds(courier.getLogin(), courier.getPassword());
    }


    @Test
    @DisplayName("Проверка - курьера можно создать")
    public void testCreateCourier() {
        // Создаем курьера и проверяем успешное создание
        Response responseCreate = createCourier(courier);
        responseCreate.then().statusCode(201)
                .and()
                .body("ok", equalTo(true));
        // Выполняем логин и проверяем, что он успешен
        Response responseLogin = courierClient.loginCourier(courierCreds);
        responseLogin.then().statusCode(200);
    }


    @Test
    @DisplayName("Проверка - нельзя создать двух одинаковых курьеров")
    public void testCannotCreateDuplicateCouriers() {
        // Создаем курьера и проверяем, что он успешно создан
        createCourier(courier).then().statusCode(201);
        // Пытаемся создать того же курьера снова, ожидаем ошибку
        Response response = createCourier(courier);
        response.then().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Проверка - успешный запрос возвращает код ответа 201")
    public void testSuccessfulRequestReturns201StatusCode() {
        // Отправляем запрос и проверяем, что код ответа 201
        Response response = createCourier(courier);
        assertEquals("Ожидается статус-код 201 при успешном создании курьера.", 201, response.statusCode());
    }

    @Test
    @DisplayName("Проверка - успешный запрос возвращает поле ok: true")
    public void testSuccessfulRequestReturnsOkTrue() {
        // Отправляем запрос и проверяем, что в ответе ok: true
        Response response = createCourier(courier);
        response.then().and().body("ok", equalTo(true));
    }

    // ВНИМАНИЕ РЕВЬЮЕРА -
    // согласно документации https://qa-scooter.praktikum-services.ru/docs/#api-Courier-CreateCourier
    // все три поля при создании курьера обязательны, так как необязательные поля обозначаются как "необязательный" в столбце название.

    @Test
    @DisplayName("Проверка - если пропущено поле имя, возвращается ошибка")
    @Issue("Баг, в котором создается курьер без указания имени")
    public void testMissingNameFieldReturnsError() {
        // Проверяем создание курьера без логина
        courier.setFirstName(null);
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без имени.", 400, response.statusCode());
    }

    @Test
    @DisplayName("Проверка - если пропущено поле пароль, возвращается ошибка")
    public void testMissingPasswordFieldReturnsError() {
        // Проверяем создание курьера без пароля
        courier.setPassword(null);
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без пароля.", 400, response.statusCode());

    }

    @Test
    @DisplayName("Проверка - если пропущено поле логин, возвращается ошибка")
    public void testMissingLoginFieldReturnsError() {
        // Проверяем создание курьера без логина
        courier.setLogin(null);
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без логина.", 400, response.statusCode());
    }


    @Test
    @DisplayName("Проверка - ошибка при создании курьера с уже существующим логином")
    public void testErrorWhenCreatingCourierWithExistingLogin() {
        // Создаем курьера с уникальным логином и проверяем успешное создание
        createCourier(courier); // Первый запрос

        // Создаем нового курьера с тем же логином, но другим паролем и именем
        Courier newCourier = new Courier(courier.getLogin(), "newPassword123", "NewName");

        // Пытаемся создать нового курьера и ожидаем ошибку
        Response response = courierClient.createCourier(newCourier); // Второй запрос, должен вернуть ошибку

        // Проверка: статус 409 и сообщение об ошибке
        response.then().statusCode(409)
                .and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Создать курьера")
    private Response createCourier(Courier courier) {
        return courierClient.createCourier(courier);
    }

    @Step("Получить courierId")
    public int getCourierId(Response responseLogin) {
        // Извлекаем id из JSON-ответа
        return responseLogin.then().extract().path("id");
    }

    @After
    public void tearDown() {
        Response responseLogin = courierClient.loginCourier(courierCreds);
        if (responseLogin.getStatusCode() == 200) {
            // Если курьер существует (успешный логин), удаляем его
            int courierId = getCourierId(responseLogin); // Извлекаем ID курьера
            courierClient.deleteCourier(courierId); // Удаляем курьера
        }
    }
}



