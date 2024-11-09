import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

// Используем @RunWith(Parameterized.class) для параметризации теста, чтобы каждый запуск мог тестировать разные значения цвета
@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final String[] colors;     // массив цветов, используемый для текущего запуска теста
    // Поля классов для инкапсуляции данных и логики
    private OrdersClient ordersClient; // клиентский объект для работы с API заказов
    private Order order;               // объект для хранения данных заказа
    private Response response;         // объект для хранения ответа API на запрос

    // Конструктор параметризированного теста принимает параметр colors, который будет передан в каждый запуск
    public OrderCreationTest(String[] colors) {
        this.colors = colors;
    }

    // Параметры теста: разные комбинации цветов для тестирования разных сценариев заказа
    @Parameterized.Parameters
    public static Collection<Object[]> colorOptions() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},        // Только черный цвет
                {new String[]{"GREY"}},         // Только серый цвет
                {new String[]{"BLACK", "GREY"}},// Оба цвета
                {new String[]{}}                // Без цвета
        });
    }

    // Метод, выполняемый перед каждым тестом для настройки начальных данных
    @Before
    public void setup() {
        // Создаем клиент для API заказов
        ordersClient = new OrdersClient();

        // Генерируем новый заказ с рандомными данными
        order = OrderGen.generateOrder();
    }

    // Шаг для установки цвета заказа. Используем параметризованные данные colors для каждого запуска
    @Step("Устанавливаем цвет")
    public void setColor(String[] colors) {
        order.setColor(colors); // Устанавливаем цвет для текущего заказа
    }

    // Шаг для проверки, что в ответе присутствует поле track (идентификатор заказа)
    @Step("Проверяем поле track")
    public void checkTrack(Response response) {
        response.then().body("track", notNullValue()); // Проверяем, что track не null
    }

    // Шаг для проверки, что код ответа 201, что подтверждает успешное создание заказа
    @Step("Проверяем код ответа")
    public void checkCode(Response response) {
        response.then().statusCode(201); // Ожидаем статус-код 201
    }

    // Шаг для извлечения track из JSON-ответа, чтобы использовать его для отмены заказа в @After
    @Step("Получить track")
    public int getTrack(Response response) {
        return response.then().extract().path("track"); // Извлекаем значение track из ответа
    }

    // Основной тест, проверяющий, что можно создать заказ с одним цветом, двумя цветами или без цвета
    @Test
    @DisplayName("Проверка - можно указать один, оба или совсем не указывать цвет.")
    public void testCreateOrderWithDifferentColors() {
        setColor(colors); // Устанавливаем цвет, согласно текущему параметру colors

        // Отправляем запрос на создание заказа
        response = ordersClient.create(order);

        // Проверяем, что код ответа 201 и в ответе присутствует поле track
        checkTrack(response); // Проверка на наличие track в ответе
        checkCode(response);   // Проверка на корректность статус-кода
    }

    // Метод, выполняемый после каждого теста, чтобы отменить заказ, если он был создан
    @After
    public void tearDown() {
        if (response != null) { // Проверяем, что response не null (т.е. заказ был создан)
            int track = getTrack(response); // Извлекаем track из ответа
            ordersClient.cancelOrder(track); // Отменяем заказ по этому track
        }
    }
}
