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

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private OrdersClient ordersClient; // клиент для работы с заказами
    private Order order;
    private Response response;
    // Поле для хранения параметра цвета, который будет передан в каждый запуск теста
    private final String[] colors;

    // Конструктор параметризированного теста
    public OrderCreationTest(String[] colors) {
        this.colors = colors;
    }

    // Параметризация - разные комбинации цвета для теста
    @Parameterized.Parameters
    public static Collection<Object[]> colorOptions() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}},        // Только черный
                {new String[]{"GREY"}},         // Только серый
                {new String[]{"BLACK", "GREY"}},// Оба цвета
                {new String[]{}}                // Без цвета
        });
    }

    @Before
    public void setup() {
        ordersClient = new OrdersClient();
        order = OrderGen.generateOrder();
    }


    @Step("Устанавливаем цвет")
    public void setColor(String[] colors) {
        order.setColor(colors);
    }

    @Step("Проверяем поле track")
    public void checkTrack(Response response) {
        response.then().body("track", notNullValue());
    }

    @Step("Проверяем код ответа")
    public void checkCode(Response response) {
        response.then().statusCode(201);
    }

    @Step("Получить track")
    public int getTrack(Response response) {
        // Извлекаем id из JSON-ответа
        return response.then().extract().path("track");
    }



    @Test
    @DisplayName("Проверка - можно указать один, оба или совсем не указывать цвет.")
    public void testCreateOrderWithDifferentColors() {
        setColor(colors); // Устанавливаем цвет, согласно параметрам теста
        // Отправляем запрос на создание заказа
        response = ordersClient.create(order);
        // Проверяем, что код ответа — 201 и в теле присутствует поле track
        checkTrack(response);
        checkCode(response);
    }

    @After //отменяем заказ после проверки
    public void tearDown() {
        if (response != null) { // Проверяем, что `response` не null
            int track = getTrack(response); // Получаем track из ответа
            ordersClient.cancelOrder(track); // Отменяем заказ с этим track
        }
    }


}
