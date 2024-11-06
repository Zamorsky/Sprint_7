import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

// Класс OrdersClient выполняет действия, связанные с заказами в системе
public class OrdersClient extends BaseClient {

    private static final String ORDERS_ENDPOINT = "/api/v1/orders";

    @Step("Создаём заказ")
    public Response create(Order order) {
        // Метод создает новый заказ, отправляя POST-запрос с телом, содержащим данные о заказе
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса (заголовки, базовый URL)
                .body(order) // Устанавливаем тело запроса с информацией о заказе
                .when()
                .post(ORDERS_ENDPOINT); // Отправляем POST-запрос на эндпоинт заказов
    }

    @Step("Получаем список заказов")
    public Response getOrderList() {
        // Метод запрашивает список всех заказов, отправляя GET-запрос
        return given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса
                .get(ORDERS_ENDPOINT); // Отправляем GET-запрос на эндпоинт заказов
    }

    @Step("Отменить заказ")
    public void cancelOrder(int track) {
        // Создаем тело запроса с полем "track"
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("track", track);
        given()
                .spec(getBaseSpec()) // Подключаем основную спецификацию для запроса (заголовки, базовый URL)
                .body(requestBody) // Устанавливаем тело запроса с треком заказа для отмены
                .when()
                .put(ORDERS_ENDPOINT + "/cancel");// Отправляем PUT-запрос на эндпоинт для отмены заказа
    }
}
