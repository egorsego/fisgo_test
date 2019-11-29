package autotests.kassaf;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.enums.DisplayType;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.MethodsEnum;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.ARTICLE;
import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.KKT_PLANT_NUM;
import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.UUID;
import static hub_emulator.response.enums.MethodsEnum.*;
import static hub_emulator.response.enums.MethodsEnum.FACTORY_FINISH;
import static hub_emulator.response.enums.MethodsEnum.STATS;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;
import static org.testng.Assert.*;

public class FactoryTest extends BaseTestClass {

    private String znKkt = "0497124323";
    private String article = "4680019030921";

    private String keyActivate = "MSwxOzVfMjcwNTIxOzQ7MyyT7OO5idMilVYWhS/yn4RdxTQoMZC7LYlxDfflGyzTfTniQqRDIIU36k23eMgiRA" +
            "51SWkRhnBypEIOWg/QFrzLrcjc/VSVpFOeYGx9xbn2Yc0mA4JzgUrZ3lV26i/Cme5J3AE/924/gj98GggNa3EkccV21Zl1r+wUEp3zgA/ac1" +
            "rvggpC/glby1mnoMy/ioOG50o3uF1m3J1ry0xc3qVCGW62SFx863zk0hqa4dCaM+g+STtDx1AIuqAOhHaZClsSpVP0BZ0qi+BNU6utq7ce" +
            "8llNK1WyDoVeDqSGhb3VRaWwh1+T7QBfV11d4l0bts0yMpKhNnse+lrTWo7qGpgG";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        manager.executeSshCommand("echo \"attach '/FisGo/configDb.db' as CONFIG;" +
                "update application_config.CONFIG set ARTICLE = '';" +
                "update application_config.CONFIG set UUID = '';" +
                "update application_config.CONFIG set KKT_PLANT_NUM = '';\" | sqlite3 configDb.db");
        manager.rebootFiscat();
        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();
        steps.expectation().waitExpectedLcd("Тест", "терминала");
        manager.iAmHub(true);
        manager.pressKey(KeyEnum.keyCancel, 6);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @AfterClass
    public void after() {
    }

    @Test
    public void testProductWithBarcode() {
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "Отсканируйте", "код авторизации");
        manager.getEventsContainer().clearLcdEvents();
        manager.clickScanner("944089101179440");
        assertTrue(manager.getEventsContainer().isContainsLcdEvents("Отсканируйте", "зав. номер", "и артикул"));
        assertTrue(manager.getEventsContainer().isContainsLcdEvents(DisplayType.DISPLAY_BUYER, "Отсканируйте", "ЗН ККТ и артикул"));
        manager.getEventsContainer().clearLcdEvents();

        //проверка ввода требований начиная с зн ккт
        manager.clickScanner(znKkt);
        steps.expectation().waitExpectedLcd(DisplayType.DISPLAY_BUYER, "ЗН ККТ:", znKkt);

        //Установить ответы на запросы START/FINISH/ACTIVATE_KEY

        hubQueue.addResponse(FACTORY_START, HubRequest.builder().status(0).build());
        hubQueue.addResponse(FACTORY_ACTIVATE_KEY, HubRequest.builder().key(keyActivate).build());

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_BUS)) {
            article = "xxxxxxxxxx";
        }

        manager.clickScanner(article);

        checkReceivedFactoryRequests();

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
