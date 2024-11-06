import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import static io.restassured.http.ContentType.JSON;


//создаем спецификацию запросов и ответов чтобы по сто раз не писать одно и тоже в коде
public class BaseClient {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/"; //все запросы используют этот урл

    protected static RequestSpecification getBaseSpec() { //метод getBaseSpec возвращает спецификацию базового HTTP-запроса, которая включает в себя базовый URL и тип контента JSON.

        return new RequestSpecBuilder()
                .setContentType(JSON) //устанавливает тип контента запроса (JSON).
                .setBaseUri(BASE_URI) //задаёт базовый URI для всех запросов, построенных с этой спецификацией.
                .build(); //завершает создание спецификации и возвращает объект RequestSpecification
    }
}