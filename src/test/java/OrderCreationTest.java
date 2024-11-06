import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import io.restassured.response.Response;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private OrdersClient ordersClient; // клиент для работы с заказами

    // Поле для хранения параметра цвета, который будет передан в каждый запуск теста
    private final String[] colors;

    // Конструктор параметризированного теста
    public OrderCreationTest(String[] colors) {
        this.colors = colors;
    }

    // Параметризация - разные комбинации цвета для теста
    @Parameterized.Parameters(name = "Тест с цветами: {0}")
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
    }

    @After
    public void tearDown() {
        // Здесь можно добавить очистку данных, если требуется
    }

    @Test
    @DisplayName("Создание заказа с разными цветами")
    public void testCreateOrderWithDifferentColors() {
        // Создаем объект Order и передаем параметры
        Order order = new Order();
        order.setFirstName("Иван");
        order.setLastName("Иванов");
        order.setAddress("ул. Пушкина, д. 1");
        order.setMetroStation("Пушкинская");
        order.setPhone("+79111234567");
        order.setRentTime(5);
        order.setDeliveryDate("2024-12-01");
        order.setComment("Тестовый заказ");
        order.setColor(colors); // Устанавливаем цвет, согласно параметрам теста

        // Отправляем запрос на создание заказа
        Response response = ordersClient.create(order);

        // Проверяем, что код ответа — 201 и в теле присутствует поле track
        response.then().statusCode(201)
                .and().body("track", notNullValue());
    }
}
