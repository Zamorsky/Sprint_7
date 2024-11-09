import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class OrderListTest {

    // Создаем экземпляр OrdersClient - объект для взаимодействия с API заказов.
    // Этот класс используется для отправки запросов к API и проверки результатов.
    private OrdersClient ordersClient;

    // Метод, выполняемый перед каждым тестом, чтобы подготовить нужные данные
    @Before
    public void setup() {
        ordersClient = new OrdersClient(); // Инициализируем объект OrdersClient
        // Здесь можно было бы создать заказы, чтобы убедиться, что список не пуст (подготовка данных)
    }

    // Основной тест для проверки получения списка заказов
    @Test
    @DisplayName("Проверка получения списка заказов")
    public void testGetOrderList() {
        // Отправляем запрос на получение списка заказов через метод getOrderList() класса OrdersClient
        Response response = ordersClient.getOrderList();

        // Проверяем, что код ответа 200 (успешный запрос)
        checkStatusCode200(response);

        // Проверяем, что в ответе присутствует непустой список заказов
        checkBodyOrders(response);
    }

    // Шаг для проверки, что ответ API вернул код 200
    @Step("Проверяем, что ответ с кодом 200")
    public void checkStatusCode200(Response response) {
        response.then().statusCode(200); // Ожидаем статус-код 200
    }

    // Шаг для проверки, что тело ответа содержит список заказов
    @Step("Проверяем, что в ответе есть список заказов")
    public void checkBodyOrders(Response response) {
        response.then().body("orders", is(not(empty()))); // Проверка, что список заказов не пуст
    }
}
