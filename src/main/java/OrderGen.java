import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderGen {

    // Метод для создания нового заказа с рандомными данными для всех полей, кроме цвета
    @Step("Генерируем заказ с рандомными значениями полей, кроме цвета")
    public static Order generateOrder() {
        Order order = new Order();

        // Устанавливаем случайные данные для имени, фамилии, адреса, станции метро и комментария
        order.setFirstName(RandomStringUtils.randomAlphabetic(8));
        order.setLastName(RandomStringUtils.randomAlphabetic(10));
        order.setAddress(RandomStringUtils.randomAlphabetic(15));
        order.setMetroStation(RandomStringUtils.randomNumeric(1)); // Пример значения станции метро
        order.setPhone("+7" + RandomStringUtils.randomNumeric(10)); // Российский номер телефона
        order.setRentTime((int) (Math.random() * 10) + 1); // Рандомное число дней аренды (от 1 до 10)

        // Устанавливаем дату доставки на завтра (или другой случайный день)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setDeliveryDate(LocalDate.now().plusDays(1).format(formatter));

        order.setComment("Комментарий " + RandomStringUtils.randomAlphabetic(5)); // Случайный комментарий

        return order;
    }
}