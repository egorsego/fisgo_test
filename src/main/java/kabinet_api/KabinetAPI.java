package kabinet_api;

import com.google.gson.Gson;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import kabinet_api.json.Receipt;

import static io.restassured.RestAssured.given;

public class KabinetAPI {

    private static final String TOKEN_FOR_BETA = "a37c95ac-3313-4d6b-9131-a5668d9bffad";

    //private static final String URL_KABINET = "https://kabinet.dreamkas.ru";

    private static final String URL_KABINET = "https://kabinet-beta.dreamkas.ru";
    private static final String URL_USER_PIN = URL_KABINET + "/api/users/0/pin";
    private static final String URL_DEVICES = URL_KABINET + "/api/devices";
    private static final String URL_SEND_RECEIPT = URL_KABINET + "/api/receipts";
    private static final String URL_OPERATION_STATUS = URL_KABINET + "/api/operations/";

    /**
     * Метод возвращает код необходимый для подключения кассы к Кабинету
     */
    public String getCodeFromKabinet() {
        Response response = authByUserToken(TOKEN_FOR_BETA).when().get(URL_USER_PIN).then().
                contentType(ContentType.JSON).extract().response();
        return response.path("code").toString();
    }

    /**
     * Метод возвращает id кассы в Кабинете (необходимо, например, для отправки чека на фискализацию)
     */
    public Integer getDeviceId() {
        Response response = authByUserToken(TOKEN_FOR_BETA).when().get(URL_DEVICES).then().
                contentType(ContentType.JSON).extract().response();

        return Integer.valueOf(response.getBody().jsonPath().getString("id[0]"));
    }

    /**
     * Авторизация по токену для работы с кабинетом
     *
     * @param token - токен полученный в кабинете.
     */
    private RequestSpecification authByUserToken(String token) {
        return given().header("Authorization", "Bearer " + token);
    }

    /**
     * Добавление чека в очередь на фискализацию
     *
     * @param receipt
     * @return String - если чек успешно добавился в очередь, операции присваивается id.
     * null  - если возникла ошибка.
     */
    public String sendReceipt(Receipt receipt) {
        Response response = authByUserToken(TOKEN_FOR_BETA).with().contentType(ContentType.JSON).with().body(new Gson().toJson(receipt))
                .when().post(URL_SEND_RECEIPT).then().contentType(ContentType.JSON).extract().response();

        if (response.getBody().jsonPath().getString("status").equals("ERROR")) {
            return null;
        }
        return response.getBody().jsonPath().getString("id");
    }

    /**
     * Метод возвращает статус операции.
     *
     * @param idOperation - Id операции
     * @return PENDING - В очереди
     * IN_PROGRESS - Выполняется
     * SUCCESS - Завершено успешно
     * ERROR - Завершено с ошибкой)
     */
    public String getOperationStatus(String idOperation) {
        Response response = authByUserToken(TOKEN_FOR_BETA).when().get(URL_OPERATION_STATUS + idOperation).then().extract().response();

        //response.getBody().prettyPrint();

        return response.getBody().jsonPath().getString("status");
    }

}
