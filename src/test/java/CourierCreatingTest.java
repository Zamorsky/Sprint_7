import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

// Класс CourierCreatingTest предназначен для тестирования создания курьеров через API.
// Тесты проверяют различные сценарии успешного и неудачного создания, соответствие
// ответа требованиям API и его реакцию на отсутствующие обязательные поля.
public class CourierCreatingTest {

    // Поля для объектов, нужных для тестирования
    private CourierClient courierClient; // Класс для взаимодействия с API курьеров
    private Courier courier;             // Класс, представляющий данные конкретного курьера
    private CourierCreds courierCreds;   // Класс для хранения учетных данных курьера (логин, пароль)

    // Метод @Before инициализирует объекты, которые понадобятся перед каждым тестом.
    @Before
    public void setup() {
        courierClient = new CourierClient(); // Создаем экземпляр API клиента для работы с курьерами

        // Создаем уникального курьера с рандомными данными
        courier = CourierGen.getRandomCourier();

        // Создаем учетные данные для авторизации созданного курьера
        courierCreds = new CourierCreds(courier.getLogin(), courier.getPassword());
    }

    // Тест проверяет успешное создание нового курьера
    @Test
    @DisplayName("Проверка - курьера можно создать")
    public void testCreateCourier() {
        // Отправляем запрос на создание курьера и проверяем успешность операции
        Response responseCreate = createCourier(courier);

        // Проверяем, что статус-код 201 и в теле ответа ok: true
        responseCreate.then().statusCode(201)
                .and()
                .body("ok", equalTo(true));

        // Выполняем авторизацию и проверяем, что логин успешен (код 200)
        Response responseLogin = courierClient.loginCourier(courierCreds);
        responseLogin.then().statusCode(200);
    }

    // Тест проверяет, что нельзя создать курьера с уже существующим логином
    @Test
    @DisplayName("Проверка - нельзя создать двух одинаковых курьеров")
    public void testCannotCreateDuplicateCouriers() {
        // Создаем курьера и проверяем успешное создание
        createCourier(courier).then().statusCode(201);

        // Пытаемся создать курьера с тем же логином и проверяем, что возвращается ошибка 409
        Response response = createCourier(courier);
        response.then().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    // Проверка, что успешное создание курьера возвращает статус 201
    @Test
    @DisplayName("Проверка - успешный запрос возвращает код ответа 201")
    public void testSuccessfulRequestReturns201StatusCode() {
        Response response = createCourier(courier);
        assertEquals("Ожидается статус-код 201 при успешном создании курьера.", 201, response.statusCode());
    }

    // Проверка, что успешный запрос создания возвращает ok: true
    @Test
    @DisplayName("Проверка - успешный запрос возвращает поле ok: true")
    public void testSuccessfulRequestReturnsOkTrue() {
        Response response = createCourier(courier);
        response.then().and().body("ok", equalTo(true));
    }

    // Проверка, что API возвращает ошибку при отсутствии имени курьера
    @Test
    @DisplayName("Проверка - если пропущено поле имя, возвращается ошибка")
    @Issue("Баг, в котором создается курьер без указания имени")
    public void testMissingNameFieldReturnsError() {
        // Убираем имя курьера и отправляем запрос
        courier.setFirstName(null);
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без имени.", 400, response.statusCode());
    }

    // Проверка, что API возвращает ошибку при отсутствии пароля
    @Test
    @DisplayName("Проверка - если пропущено поле пароль, возвращается ошибка")
    public void testMissingPasswordFieldReturnsError() {
        courier.setPassword(null); // Убираем пароль курьера и отправляем запрос
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без пароля.", 400, response.statusCode());
    }

    // Проверка, что API возвращает ошибку при отсутствии логина
    @Test
    @DisplayName("Проверка - если пропущено поле логин, возвращается ошибка")
    public void testMissingLoginFieldReturnsError() {
        courier.setLogin(null); // Убираем логин курьера и отправляем запрос
        Response response = createCourier(courier);
        assertEquals("Ошибка! Курьер создался без логина.", 400, response.statusCode());
    }

    // Проверка, что при создании курьера с уже существующим логином возвращается ошибка
    @Test
    @DisplayName("Проверка - ошибка при создании курьера с уже существующим логином")
    public void testErrorWhenCreatingCourierWithExistingLogin() {
        createCourier(courier); // Создаем первого курьера

        // Создаем второго курьера с тем же логином, но другими данными
        Courier newCourier = new Courier(courier.getLogin(), "newPassword123", "NewName");
        Response response = courierClient.createCourier(newCourier);

        // Проверяем, что возвращается код 409 и сообщение об ошибке
        response.then().statusCode(409)
                .and().body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    // Метод для создания курьера
    @Step("Создать курьера")
    private Response createCourier(Courier courier) {
        return courierClient.createCourier(courier);
    }

    // Метод для извлечения id курьера после авторизации
    @Step("Получить courierId")
    public int getCourierId(Response responseLogin) {
        return responseLogin.then().extract().path("id");
    }
}


