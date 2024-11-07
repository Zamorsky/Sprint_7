import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;

//в этом классе мы генерируем случайные данные для курьера

public class CourierGen {
    @Step("Генерируем случайные данные курьера")
    public static Courier getRandomCourier() {
        Courier courier = new Courier();
        courier.setLogin(RandomStringUtils.randomAlphabetic(12));
        courier.setPassword(RandomStringUtils.randomAlphabetic(9));
        courier.setFirstName(RandomStringUtils.randomAlphabetic(10));
        return courier;
    }
}
