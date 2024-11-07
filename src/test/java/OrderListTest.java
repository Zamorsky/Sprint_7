import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class OrderListTest {

    private OrdersClient ordersClient;

    @Before
    public void setup() {
        ordersClient = new OrdersClient();
        // Генерируем новый заказ с случайными данными

    }


    @Test
    @DisplayName("Проверка получения списка заказов")
    public void testGetOrderList() {
        // Теперь получаем список заказов
        Response response = ordersClient.getOrderList();
        checkStatusCode200(response);
        checkBodyOrders(response);
    }

    @Step("Проверяем, что ответ с кодом 200")
    public void checkStatusCode200(Response response) {
        response.then().statusCode(200);
    }


    @Step("Проверяем, что в ответе есть список заказов")
    public void checkBodyOrders(Response response) {
        response.then().body("orders", is(not(empty())));
    }
}