import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName; // импорт DisplayName
import io.qameta.allure.Description; // импорт Description
import io.qameta.allure.Step; // импорт Step

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreatingTest {

    private CourierClient courierClient;
    private Courier courier;


    @Before
    public void setup() {
        courierClient = new CourierClient();
    }
}
