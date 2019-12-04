package autotests.pulse;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.*;
import static hub_emulator.response.enums.MethodsEnum.*;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class FactoryPulseTest extends BaseTestClass {

    private String znKkt = "0498000019";
    private String article = "4680019030921";
    private String keyActivate = "MSwxOzM7NTs0OzYsG5e8HGbYPJUXVHi0H8IUyYxTGqTgAxLKssEq8ONwBSOkGxaDPIqQt3QB8P1j3aoA2f" +
            "Nf5svYrHKzT0a5846R+iUi34D1sMAnnW02RLW41vnjeauhYhbZHsAd5tPzv/r4oWj1GWzhU8nf3z9bMeGXMY8jMJv9m21s/N4lilBi" +
            "Cm5+4WX8RZtS+rm432FX6Cd2ayJcxfAxnCV988pcrQ5XLoeQiAxgCRYp5lULYIyE9krgulwifitf0FlY8QpWe19wJWZxZTEVNfl986" +
            "pS4UBH556dwm9s0OLapAp0ugaSugnJPp/I+R+pwcKn9lA7H310gFzyNry6fqodR4uwjGHOAw==";
    private String commandClearFactoryFields = "echo \"attach '/FisGo/configDb.db' as CONFIG;" +
            "update application_config.CONFIG set ARTICLE = '';" +
            "update application_config.CONFIG set UUID = '';" +
            "update application_config.CONFIG set KKT_PLANT_NUM = '';\" | sqlite3 configDb.db";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        steps.step().fastTechnicalZero();

        manager.executeSshCommand(commandClearFactoryFields);
        manager.rebootFiscat();
        manager.iAmHub(true);
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    /**
     * Тест проверяет процесс производства кассы (пульс-фа).
     * 1. Дождаться экран "Отсканируйте код авторизации"
     * 2. Очистить events экрана
     * 3. Сканировать код авторизации
     * 4. Проверить, что появился экран "Отсканируйте ЗН ККТ и артикул"
     * 5. Очистить events экрана
     * 6. Сканировать заводской номер
     * 7. Проверить экран "ЗН ККТ: номер зн"
     * 8. Установить ответы на запросы START/FINISH/ACTIVATE_KEY
     * 9. Сканировать артикул
     * 10. Проверить, что пришли запросы START/FINISH/ACTIVATE_KEY
     * 11. Проверить экран "Подписка активирована"
     * 12. Проверить экран "Производство завершено"
     * 13. Дождаться экран "Перезагрузка"
     * 14. Перезапустить кассоевое ПО
     * 15. Очистить все запросы на тестовый сервер
     * 16. Проверить в json статистики поле лицензии
     * 17. Проверить поля json START/FINISH/ACTIVATE_KEY
     * 18. Проверить поля конфига KKT_PLANT_NUM, UUID, ARTICLE
     */
    @Test
    public void testProductWithBarcode() {
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Отсканируйте", "код авторизации");
        manager.getEventsContainer().clearLcdEvents();
        manager.clickScanner("944089101179440");
        steps.expectation().waitExpectedLcd("Отсканируйте", "зав. номер", "и артикул");
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Отсканируйте", "ЗН ККТ и артикул");
        manager.getEventsContainer().clearLcdEvents();

        //проверка ввода требований начиная с зн ккт
        manager.clickScanner(znKkt);
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "ЗН ККТ:", znKkt);

        //Установить ответы на запросы START/FINISH/ACTIVATE_KEY

        hubQueue.addResponse(FACTORY_START, HubRequest.builder().status(0).build());
        hubQueue.addResponse(FACTORY_ACTIVATE_KEY, HubRequest.builder().key(keyActivate).build());

        manager.clickScanner(article);

        checkReceivedFactoryRequests();

        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Процесс", "активации ключа");

        manager.sleepPlease(15_000);

        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Подписка", "активирована");
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Производство", "завершено");
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Перезагрузка");

        manager.rebootFiscat();
        manager.iAmHub(true);

        checkLicenseInStats();

        checkFieldsFactoryStart();
        checkFieldsFactoryFinish();
        checkFieldsConfig();
    }

    /**
     * Тест выполняется после успешного прохождения теста testProductWithBarcode(). Исходные данные - касса произведенна,
     * на кассе активирован ключ.
     * <p>
     * 1. Очистить все полученные запросы на сервер
     * 2. Установить ответ на WHO_I_AM для подключения к кабинету
     * 3. Ответить на POLL регистрацией
     * 4. Отправить на кассу внешний чек с товаром с ндс 20%
     * 5. Проверить, что поле result в запросе poll - SUCCESS.
     * 6. Проверить что на сервер пришел запрос PURCHASE_DOCUMENT_REPORT
     * 7. Проверить поля с ндс в json PURCHASE_DOCUMENT_REPORT
     */
    @Test(dependsOnMethods = "testProductWithBarcode")
    public void testNds20() {
        regNum = manager.getRegNumKKT();
        server.clearRequests();
        manager.setPollTime(10);

        //Ответить на POLL регистрацией
        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegForPulse(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
        checkResultPoll();

        steps.hub().answerPoll(getPurchaseWithNds20());

        checkResultPoll();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        checkPurchaseDocumentReport();

        //FIXME после выгрузки тегов всё ломается на кассе (*((((()()()(((((((
//        ListScreen<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());
//        assertTrue(docFromFs.contains(Tag.builder().tag(1102).value("333").build()), "Неверное значение тэга 1102");
    }

    //__________________________________________ STEPS _________________________________________________________________

    @Step("Проверка полей json PURCHASE_DOCUMENT_REPORT")
    private void checkPurchaseDocumentReport() {
        String expectedTax = "NDS_20";
        HubRequest requestExPur = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        assertNotNull(requestExPur);
        assertNotNull(requestExPur.getData());
        assertNotNull(requestExPur.getData().getPurchases());
        assertEquals(1, requestExPur.getData().getPurchases().length);
        assertNotNull(requestExPur.getData().getPurchases()[0]);
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions());
        assertEquals(1, requestExPur.getData().getPurchases()[0].getPositions().length);
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions()[0]);
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions()[0].getProduct());
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta());
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax());
        assertEquals(expectedTax, requestExPur.getData().getPurchases()[0].getPositions()[0]
                .getProduct().getMeta().getTax());
        assertNotNull(requestExPur.getData().getPurchases()[0].getPositions()[0].getTax());
        assertEquals(expectedTax, requestExPur.getData().getPurchases()[0].getPositions()[0].getTax());
    }

    @Step("Проверка полей json poll с результатом о выполнении тасок")
    private void checkResultPoll() {
        HubRequest pollResult = server.getLastRequest(POLL);
        assertNotNull(pollResult);
        assertNotNull(pollResult.getData());
        assertNotNull(pollResult.getData().getTaskResults());
        assertEquals(1, pollResult.getData().getTaskResults().length);
        assertNotNull(pollResult.getData().getTaskResults()[0]);
        assertNotNull(pollResult.getData().getTaskResults()[0].getResult());
        assertNotNull(pollResult.getData().getTaskResults()[0].getTaskId());
        assertEquals("SUCCESS", pollResult.getData().getTaskResults()[0].getResult());
        assertEquals(Integer.valueOf(1), pollResult.getData().getTaskResults()[0].getTaskId());
    }

    @Step("Проверка лицензии в json статистики")
    private void checkLicenseInStats() {
        assertTrue(server.checkReceivedRequest(STATS, 1));
        HubRequest requestStats = server.getLastRequest(STATS);
        assertNotNull(requestStats);
        assertNotNull(requestStats.getData());
        assertNotNull(requestStats.getData().getLicense());
        assertNotNull(requestStats.getData().getLicense().getServices());
        assertEquals(5, requestStats.getData().getLicense().getServices().length);
        for (int i = 0; i < requestStats.getData().getLicense().getServices().length; i++) {
            assertNotNull(requestStats.getData().getLicense().getServices()[i]);
            assertNotNull(requestStats.getData().getLicense().getServices()[i].getCode());
        }
    }

    @Step("Проверка что JSON: FACTORY_START, FACTORY_FINISH, FACTORY_ACTIVATE_KEY были доставлены")
    private void checkReceivedFactoryRequests() {
        assertTrue(server.checkReceivedRequest(FACTORY_START, 1));
        assertTrue(server.checkReceivedRequest(MethodsEnum.FACTORY_FINISH, 1));
        assertTrue(server.checkReceivedRequest(MethodsEnum.FACTORY_ACTIVATE_KEY, 1));
    }

    @Step("Проверка полей JSON - FACTORY_START")
    private void checkFieldsFactoryStart() {
        HubRequest requestFactoryStart = server.getLastRequest(FACTORY_START);
        assertNotNull(requestFactoryStart);
        assertNotNull(requestFactoryStart.getPartNumber(), "Отсутствует поле partNumber");
        assertNotNull(requestFactoryStart.getSerialNumber(), "Отсутствует поле serialNumber");
        assertEquals(article, requestFactoryStart.getPartNumber(), "Поле partNumber не соответствует ожидаемому");
        assertEquals(znKkt, requestFactoryStart.getSerialNumber(), "Поле serialNumber не соответствует ожидаемому");
    }

    @Step("Проверка полей JSON - FACTORY_FINISH")
    private void checkFieldsFactoryFinish() {
        HubRequest requestFactoryFinish = server.getLastRequest(FACTORY_FINISH);
        assertNotNull(requestFactoryFinish);
        assertNotNull(requestFactoryFinish.getSerialNumber(), "Отсутствует поле serialNumber");
        assertNotNull(requestFactoryFinish.getPartNumber(), "Отсутствует поле partNumber");
        assertNotNull(requestFactoryFinish.getFields(), "Отсутствует поле fields");
        assertNotNull(requestFactoryFinish.getFields().getEthMac(), "Отсутствует поле EthMac");
        assertEquals(article, requestFactoryFinish.getPartNumber(), "Поле partNumber не соответствует ожидаемому");
        assertEquals(znKkt, requestFactoryFinish.getSerialNumber(), "Поле serialNumber не соответствует ожидаемому");
        assertNotNull(requestFactoryFinish.getFields().getDeviceUuid(), "Отсутствует поле DEVICE_UUID");
        assertTrue(isValidUUID(requestFactoryFinish.getFields().getDeviceUuid()), "Невалидный UUID");
    }

    @Step("Проверка полей конфига после выполнения процесса производства кассы")
    private void checkFieldsConfig() {
        Map<ConfigFieldsEnum, String> configFields = manager.getConfigFields(KKT_PLANT_NUM, ARTICLE, UUID);
        assertEquals(znKkt, configFields.get(KKT_PLANT_NUM), "Поле KKT_PLANT_NUM в конфиге не равно ожидаемому");
        assertEquals(article, configFields.get(ARTICLE), "Поле ARTICLE в конфиге не равно ожидаемому");
        assertEquals(server.getLastRequest(FACTORY_FINISH).getFields().getDeviceUuid(), configFields.get(UUID),
                "Поле UUID в конфиге не равно ожидаемому");
    }

    @Step("Проверка валидности UUID")
    private boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    private HubRequest getPurchaseWithNds20() {
        ArrayList<Positions> positions = new ArrayList<>();

        positions.add(Positions.builder()
                .name("Товар1")
                .type("COUNTABLE")
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax("NDS_20")
                .taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        for (int i = 1; i < tasks.length + 1; i++) {
            tasks[i - 1] = TaskResults.builder().taskId(i)
                    .data(getPollTaskData(positions, total, "54651022bffebc03098b456" + i))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(CASHLESS).build());
        return PollTaskData.builder()
                .remId(remId)
                .taxMode("DEFAULT")
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email("g.glushkov@dreamkas.ru").build())
                .total(total)
                .build();
    }

}
