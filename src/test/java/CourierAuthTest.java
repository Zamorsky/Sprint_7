import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

// Класс CourierAuthTest тестирует авторизацию курьера. Этот класс помогает нам удостовериться, что
// все аспекты API авторизации работают корректно и система правильно обрабатывает как корректные, так и ошибочные запросы.
public class CourierAuthTest {

    // Переменные для хранения объектов, необходимых для тестов
    private CourierClient courierClient; // Клиент для взаимодействия с API курьера
    private Courier courier;             // Экземпляр курьера, созданного для тестирования
    private CourierCreds courierCreds;   // Учетные данные курьера для авторизации

    // Метод @Before выполняется перед каждым тестом, создавая необходимые для тестов объекты и данные.
    @Before
    public void setup() {
        courierClient = new CourierClient(); // Инициализируем API-клиент для работы с курьерами

        // Генерация уникального курьера с рандомными данными, который будет использоваться в тестах
        courier = CourierGen.getRandomCourier();

        // Отправка запроса на создание курьера в системе
        courierClient.createCourier(courier);

        // Создание объекта с учетными данными (логин и пароль) для авторизации курьера
        courierCreds = new CourierCreds(courier.getLogin(), courier.getPassword());
    }

    // Метод @After выполняется после каждого теста, чтобы удалить тестовые данные и поддерживать чистоту окружения.
    @After
    public void tearDown() {
        // Логинимся, чтобы получить ID курьера для последующего удаления
        Response responseLogin = courierClient.loginCourier(courierCreds);

        // Если авторизация успешна (код 200), то извлекаем ID и удаляем курьера
        if (responseLogin.getStatusCode() == 200) {
            int courierId = getCourierId(responseLogin);
            courierClient.deleteCourier(courierId);
        }
    }

    // Тест проверяет успешную авторизацию курьера и возвращение ID в ответе на запрос
    @Test
    @DisplayName("Проверка - курьер может авторизоваться и успешный запрос возвращает id")
    public void testCourierCanLogin() {
        // Отправляем запрос на авторизацию с валидными данными
        Response response = courierClient.loginCourier(courierCreds);

        // Проверяем, что статус-код равен 200 и в теле ответа есть идентификатор (id) курьера
        response.then().statusCode(200)
                .and().body("id", equalTo(getCourierId(response)));
    }

    // Тест проверяет, что авторизация невозможна при отсутствии обязательных полей
    @Test
    @DisplayName("Проверка - для авторизации нужно передать все обязательные поля")
    public void testAuthorizationRequiresAllMandatoryFields() {
        // Создаем учетные данные с пустым логином и отправляем запрос на авторизацию
        Response responseWithoutLogin = courierClient.loginCourier(new CourierCreds(null, courier.getPassword()));

        // Проверяем, что API возвращает ошибку 400
        assertEquals("Ошибка! Код возврата отличается от 400", 400, responseWithoutLogin.statusCode());

        // Создаем учетные данные с пустым паролем и отправляем запрос на авторизацию
        Response responseWithoutPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), null));

        // Проверяем, что API возвращает ошибку 400
        assertEquals("Ошибка! Код возврата отличается от 400", 400, responseWithoutPassword.statusCode());
    }

    // Тест проверяет, что при указании неправильного логина или пароля система вернет ошибку
    @Test
    @DisplayName("Проверка - система вернёт ошибку, если неправильно указать логин или пароль")
    @Issue("Бекэнд отваливается по таймауту с ошибкой 504.")
    public void testLoginWithInvalidCredentialsReturnsError() {
        // Пробуем авторизоваться с неправильным логином
        Response responseInvalidLogin = courierClient.loginCourier(new CourierCreds("invalidLogin", courier.getPassword()));

        // Проверяем, что возвращается код ошибки 404 и сообщение об ошибке
        responseInvalidLogin.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));

        // Пробуем авторизоваться с неправильным паролем
        Response responseInvalidPassword = courierClient.loginCourier(new CourierCreds(courier.getLogin(), "invalidPassword"));

        // Проверяем, что возвращается код ошибки 404 и сообщение об ошибке
        responseInvalidPassword.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));
    }

    // Тест проверяет, что система вернет ошибку для авторизации под несуществующим пользователем
    @Test
    @DisplayName("Проверка - если авторизоваться под несуществующим пользователем, запрос возвращает ошибку")
    public void testLoginForNonExistingUserReturnsError() {
        // Пробуем авторизоваться под несуществующими учетными данными
        Response response = courierClient.loginCourier(new CourierCreds("123nonExistingLogin123", "123anyPassword321"));

        // Проверяем, что возвращается код ошибки 404 и сообщение об ошибке
        response.then().statusCode(404)
                .and().body("message", equalTo("Учетная запись не найдена"));
    }

    // Вспомогательный метод для извлечения ID курьера из ответа на авторизацию
    @Step("Получить courierId")
    @DisplayName("Проверка - для авторизации нужно передать все обязательные поля")
    public int getCourierId(Response responseLogin) {
        // Извлекаем значение id из JSON-ответа с помощью RestAssured
        return responseLogin.then().extract().path("id");
    }
}

/*
Пояснение принципов ООП и API-тестирования в коде:
Инкапсуляция: Вся логика для создания, авторизации и удаления курьера сосредоточена в классе CourierClient. Это делает тесты в CourierAuthTest проще и понятнее, поскольку нам нужно только вызвать соответствующий метод клиента для выполнения конкретного действия (например, loginCourier() для авторизации). Подход минимизирует повторение кода.

Абстракция: Благодаря инкапсуляции логики взаимодействия с API в классе CourierClient, детали HTTP-запросов скрыты от тестов. Это позволяет сосредоточиться на проверках, не вникая в технические аспекты формирования запросов.

Переиспользование: Класс CourierGen служит для генерации тестовых данных, позволяя многократно создавать уникальных курьеров, не переписывая каждый раз один и тот же код. Это упрощает генерацию случайных данных, обеспечивая чистоту и независимость тестов.

Легкость модификации: Благодаря использованию классов для курьеров (Courier, CourierCreds, CourierClient), тестовые данные и функциональность легко изменять без вмешательства в сами тесты. Например, изменение API можно будет реализовать, модифицируя только CourierClient, что повысит адаптивность тестов.

Тестирование API: Использование RestAssured для проверки ответа сервера позволяет уверенно тестировать состояние и поведение API. Проверки, такие как assertEquals и response.then().statusCode(200), помогают удостовериться, что API корректно обрабатывает запросы и возвращает ожидаемые ответы.
 */