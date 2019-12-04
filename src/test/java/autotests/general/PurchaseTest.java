package autotests.general;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.api_manager.json.response.data.Tag;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import hub_emulator.response.enums.TypeResponseExPurchase;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.CLC_SERVICE_SIGN;
import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASH;
import static org.testng.Assert.*;

public class PurchaseTest extends BaseTestClass {

    private final Random random = new Random();
    private int initFdNum;
    private String cashierName;

    @BeforeClass
    public void beforePurchaseTests() {
        initCashierName();

        regNum = manager.getRegNumKKT();
        manager.getEventsContainer().clearLcdEvents();

        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.shift().openShift();
    }

    private void initCashierName() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            cashierName = "Администратор";
        } else {
            cashierName = "Админ";
        }
    }

    @AfterClass
    public void afterPurchaseTests() {
        manager.iAmHub(false);
    }

    @Test
    public void testFreePriceSale() {
        steps.step().goToFreeMode();

        int numFd = manager.getLastFdNum();
        for (int i = 1; i <= 5; i++) {
            steps.payment().addPositions(Integer.toString(i), i);
            steps.payment().completePurchase();

            int lastFdNum = manager.getLastFdNum();
            assertEquals(++numFd, lastFdNum, "Количество документов в ФН не изменилось после пробития чека");

            //FIXME когда будет выгрузка документов из фн на КАССЕ Ф
            if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                List<Tag> docFromFs = manager.getDocFromFs(lastFdNum);
                assertEquals(getCountTag(docFromFs, 1079), i, "Количество тэгов 1079 не равно количеству позиций");
                assertTrue(docFromFs.contains(Tag.builder().tag(1031).value(Double.valueOf(i * i * 100)).build()),
                        "Тэг 1031 имеет неверное значение (либо отсутствует)");
                assertTrue(docFromFs.contains(Tag.builder().tag(1020).value(Double.valueOf(i * i * 100)).build()),
                        "Тэг 1020 имеет неверное значение (либо отсутствует)");
            }
        }
    }

    @Test
    public void prepayment100() {
        initFdNum = manager.getLastFdNum();
        server.clearRequests();
        steps.step().goToFreeMode();
        int money = setPosition();

        setPrepayment();
        //TODO убрать условие когда будет доступна выгрузка документа из ФН на кассу-ф
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            checkDocumentFromFs(money);
        }
        checkReceivedRequest(money);
    }

    @Test
    public void partPrepayment() {
        int numLastPurchase = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        addPrepayment("50");
        steps.payment().completePurchase();

        int curNumLastPurchase = manager.getLastFdNum();
        assertEquals(curNumLastPurchase - numLastPurchase,
                1, "Документ не записался в ФН (было - " + numLastPurchase + ", стало - " + curNumLastPurchase);

        //TODO убрать условие когда будет доступна выгрузка документа из ФН на кассу-ф
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(curNumLastPurchase);
            System.out.println();

            Map<String, Object> tags = getTagsFromList(docFromFs);

            SoftAssert softly = new SoftAssert();
            softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК", "Неверное значение тега - 1000");
            softly.assertEquals(tags.get("1041"), manager.getConfigFields(ConfigFieldsEnum.FS_NUMBER).get(ConfigFieldsEnum.FS_NUMBER), "Неверное значение тега - 1041");
            softly.assertEquals(tags.get("1018"), "7802870820  ", "Неверное значение тега - 1018");
            softly.assertNotNull(tags.get("1040"));
            softly.assertNotNull(tags.get("1012"));
            softly.assertNotNull(tags.get("1077"));
            softly.assertNotNull(tags.get("1038"));
            softly.assertNotNull(tags.get("1042"));
            softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тега - 1054");
            softly.assertEquals(tags.get("1020"), 10000.0, "Неверное значение тега - 1020");
            softly.assertEquals(tags.get("1209"), 2.0, "Неверное значение тега - 1209");
            softly.assertEquals(tags.get("1059"), null, "Неверное значение тега - 1059");
            softly.assertEquals(tags.get("1030"), "Товар", "Неверное значение тега - 1030");
            softly.assertEquals(tags.get("1214"), 2.0, "Неверное значение тега - 1214");
            softly.assertEquals(tags.get("1079"), 10000.0, "Неверное значение тега - 1079");
            softly.assertEquals(tags.get("1023"), "1,0", "Неверное значение тега - 1023");
            softly.assertEquals(tags.get("1212"), 1.0, "Неверное значение тега - 1212");
            softly.assertEquals(tags.get("1043"), 10000.0, "Неверное значение тега - 1043");
            softly.assertEquals(tags.get("1199"), 6.0, "Неверное значение тега - 1199");
            softly.assertEquals(tags.get("1031"), 5000.0, "Неверное значение тега - 1031");
            softly.assertEquals(tags.get("1081"), 0.0, "Неверное значение тега - 1081");
            softly.assertEquals(tags.get("1215"), 0.0, "Неверное значение тега - 1215");
            softly.assertEquals(tags.get("1216"), 5000.0, "Неверное значение тега - 1216");
            softly.assertEquals(tags.get("1217"), 0.0, "Неверное значение тега - 1217");
            softly.assertEquals(tags.get("1102"), 0.0, "Неверное значение тега - 1102");
            softly.assertEquals(tags.get("1103"), 0.0, "Неверное значение тега - 1103");
            softly.assertEquals(tags.get("1104"), 0.0, "Неверное значение тега - 1104");
            softly.assertEquals(tags.get("1105"), 10000.0, "Неверное значение тега - 1105");
            softly.assertEquals(tags.get("1106"), 0.0, "Неверное значение тега - 1106");
            softly.assertEquals(tags.get("1107"), 0.0, "Неверное значение тега - 1107");
            //softly.assertEquals(tags.get("1117"), "www.kassa@dreamkas.ru", "Неверное значение тега - 1117");
            softly.assertAll();
        }

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest);
    }

    @Test
    public void advanceBuyCertificate() {
        int numLastPurchase = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);

        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key4);
        manager.sendCommands();

        steps.payment().completePurchase();

        int curNumLastPurchase = manager.getLastFdNum();
        assertEquals(curNumLastPurchase - numLastPurchase,
                1, "Документ не записался в ФН (было - " + numLastPurchase + ", стало - " + curNumLastPurchase);

        //TODO убрать условие когда будет доступна выгрузка документа из ФН на кассу-ф
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(curNumLastPurchase);

            Map<String, Object> tags = getTagsFromList(docFromFs);

            SoftAssert softly = new SoftAssert();
            softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК", "Неверное значение тега - 1000");
            softly.assertEquals(tags.get("1041"), manager.getConfigFields(ConfigFieldsEnum.FS_NUMBER).get(ConfigFieldsEnum.FS_NUMBER), "Неверное значение тега - 1041");
            softly.assertEquals(tags.get("1018"), "7802870820  ", "Неверное значение тега - 1018");
            softly.assertNotNull(tags.get("1040"));
            softly.assertNotNull(tags.get("1012"));
            softly.assertNotNull(tags.get("1077"));
            softly.assertNotNull(tags.get("1038"));
            softly.assertNotNull(tags.get("1042"));
            softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тега - 1054");
            softly.assertEquals(tags.get("1020"), 10000.0, "Неверное значение тега - 1020");
            softly.assertEquals(tags.get("1209"), 2.0, "Неверное значение тега - 1209");
            softly.assertEquals(tags.get("1059"), null, "Неверное значение тега - 1059");
            softly.assertEquals(tags.get("1030"), "Товар", "Неверное значение тега - 1030");
            softly.assertEquals(tags.get("1214"), 3.0, "Неверное значение тега - 1214");
            softly.assertEquals(tags.get("1079"), 10000.0, "Неверное значение тега - 1079");
            softly.assertEquals(tags.get("1023"), "1,0", "Неверное значение тега - 1023");
            softly.assertEquals(tags.get("1212"), 1.0, "Неверное значение тега - 1212");
            softly.assertEquals(tags.get("1043"), 10000.0, "Неверное значение тега - 1043");
            softly.assertEquals(tags.get("1199"), 6.0, "Неверное значение тега - 1199");
            softly.assertEquals(tags.get("1031"), 10000.0, "Неверное значение тега - 1031");
            softly.assertEquals(tags.get("1081"), 0.0, "Неверное значение тега - 1081");
            softly.assertEquals(tags.get("1215"), 0.0, "Неверное значение тега - 1215");
            softly.assertEquals(tags.get("1216"), 0.0, "Неверное значение тега - 1216");
            softly.assertEquals(tags.get("1217"), 0.0, "Неверное значение тега - 1217");
            softly.assertEquals(tags.get("1102"), 0.0, "Неверное значение тега - 1102");
            softly.assertEquals(tags.get("1103"), 0.0, "Неверное значение тега - 1103");
            softly.assertEquals(tags.get("1104"), 0.0, "Неверное значение тега - 1104");
            softly.assertEquals(tags.get("1105"), 10000.0, "Неверное значение тега - 1105");
            softly.assertEquals(tags.get("1106"), 0.0, "Неверное значение тега - 1106");
            softly.assertEquals(tags.get("1107"), 0.0, "Неверное значение тега - 1107");
            //softly.assertEquals(tags.get("1117"), "www.kassa@dreamkas.ru", "Неверное значение тега - 1117");
            softly.assertAll();

        }

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest);
    }

    @Test
    public void advanceOverflow() {
        int numLastPurchase = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);

        addAdvance("200");

        steps.payment().completePurchase();

        int curNumLastPurchase = manager.getLastFdNum();
        assertEquals(curNumLastPurchase - numLastPurchase,
                1, "Документ не записался в ФН (было - " + numLastPurchase + ", стало - " + curNumLastPurchase);

        //TODO убрать условие когда будет доступна выгрузка документа из ФН на кассу-ф
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(curNumLastPurchase);
            System.out.println();

            Map<String, Object> tags = getTagsFromList(docFromFs);

            SoftAssert softly = new SoftAssert();
            softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК", "Неверное значение тега - 1000");
            softly.assertEquals(tags.get("1041"), manager.getConfigFields(ConfigFieldsEnum.FS_NUMBER).get(ConfigFieldsEnum.FS_NUMBER), "Неверное значение тега - 1041");
            softly.assertEquals(tags.get("1018"), "7802870820  ", "Неверное значение тега - 1018");
            softly.assertNotNull(tags.get("1040"));
            softly.assertNotNull(tags.get("1012"));
            softly.assertNotNull(tags.get("1077"));
            softly.assertNotNull(tags.get("1038"));
            softly.assertNotNull(tags.get("1042"));
            softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тега - 1054");
            softly.assertEquals(tags.get("1020"), 20000.0, "Неверное значение тега - 1020");
            softly.assertEquals(tags.get("1209"), 2.0, "Неверное значение тега - 1209");
            softly.assertEquals(tags.get("1059"), null, "Неверное значение тега - 1059");
            softly.assertEquals(tags.get("1030"), "Превышение номинальной стоимости аванса (предоплаты) над продажной ценой товара", "Неверное значение тега - 1030");
            softly.assertEquals(tags.get("1214"), 4.0, "Неверное значение тега - 1214");
            softly.assertEquals(tags.get("1079"), 10000.0, "Неверное значение тега - 1079");
            softly.assertEquals(tags.get("1023"), "1,0", "Неверное значение тега - 1023");
            softly.assertEquals(tags.get("1212"), 1.0, "Неверное значение тега - 1212");
            softly.assertEquals(tags.get("1043"), 10000.0, "Неверное значение тега - 1043");
            softly.assertEquals(tags.get("1199"), 1.0, "Неверное значение тега - 1199");
            softly.assertEquals(tags.get("1031"), 0.0, "Неверное значение тега - 1031");
            softly.assertEquals(tags.get("1081"), 0.0, "Неверное значение тега - 1081");
            softly.assertEquals(tags.get("1215"), 20000.0, "Неверное значение тега - 1215");
            softly.assertEquals(tags.get("1216"), 0.0, "Неверное значение тега - 1216");
            softly.assertEquals(tags.get("1217"), 0.0, "Неверное значение тега - 1217");
            softly.assertEquals(tags.get("1103"), 0.0, "Неверное значение тега - 1103");
            softly.assertEquals(tags.get("1104"), 0.0, "Неверное значение тега - 1104");
            softly.assertEquals(tags.get("1105"), 10000.0, "Неверное значение тега - 1105");
            softly.assertEquals(tags.get("1106"), 0.0, "Неверное значение тега - 1106");
            softly.assertEquals(tags.get("1107"), 0.0, "Неверное значение тега - 1107");

            softly.assertAll();
        }

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest);
    }

    @Test
    public void advancePaymentCertificate() {
        int numLastPurchase = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);

        addAdvance("100");

        steps.payment().completePurchase();

        int curNumLastPurchase = manager.getLastFdNum();
        assertEquals(curNumLastPurchase - numLastPurchase,
                1, "Документ не записался в ФН (было - " + numLastPurchase + ", стало - " + curNumLastPurchase);

        //TODO убрать условие когда будет доступна выгрузка документа из ФН на кассу-ф
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(curNumLastPurchase);
            System.out.println();

            Map<String, Object> tags = getTagsFromList(docFromFs);

            SoftAssert softly = new SoftAssert();
            softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК", "Неверное значение тега - 1000");
            softly.assertEquals(tags.get("1041"), manager.getConfigFields(ConfigFieldsEnum.FS_NUMBER).get(ConfigFieldsEnum.FS_NUMBER), "Неверное значение тега - 1041");
            softly.assertEquals(tags.get("1018"), "7802870820  ", "Неверное значение тега - 1018");
            softly.assertNotNull(tags.get("1040"));
            softly.assertNotNull(tags.get("1012"));
            softly.assertNotNull(tags.get("1077"));
            softly.assertNotNull(tags.get("1038"));
            softly.assertNotNull(tags.get("1042"));
            softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тега - 1054");
            softly.assertEquals(tags.get("1020"), 10000.0, "Неверное значение тега - 1020");
            softly.assertEquals(tags.get("1209"), 2.0, "Неверное значение тега - 1209");
            softly.assertEquals(tags.get("1059"), null, "Неверное значение тега - 1059");
            softly.assertEquals(tags.get("1030"), "Товар", "Неверное значение тега - 1030");
            softly.assertEquals(tags.get("1214"), 4.0, "Неверное значение тега - 1214");
            softly.assertEquals(tags.get("1079"), 10000.0, "Неверное значение тега - 1079");
            softly.assertEquals(tags.get("1023"), "1,0", "Неверное значение тега - 1023");
            softly.assertEquals(tags.get("1212"), 1.0, "Неверное значение тега - 1212");
            softly.assertEquals(tags.get("1043"), 10000.0, "Неверное значение тега - 1043");
            softly.assertEquals(tags.get("1199"), 6.0, "Неверное значение тега - 1199");
            softly.assertEquals(tags.get("1031"), 0.0, "Неверное значение тега - 1031");
            softly.assertEquals(tags.get("1081"), 0.0, "Неверное значение тега - 1081");
            softly.assertEquals(tags.get("1215"), 10000.0, "Неверное значение тега - 1215");
            softly.assertEquals(tags.get("1216"), 0.0, "Неверное значение тега - 1216");
            softly.assertEquals(tags.get("1217"), 0.0, "Неверное значение тега - 1217");
            softly.assertEquals(tags.get("1102"), 0.0, "Неверное значение тега - 1102");
            softly.assertEquals(tags.get("1103"), 0.0, "Неверное значение тега - 1103");
            softly.assertEquals(tags.get("1104"), 0.0, "Неверное значение тега - 1104");
            softly.assertEquals(tags.get("1105"), 10000.0, "Неверное значение тега - 1105");
            softly.assertEquals(tags.get("1106"), 0.0, "Неверное значение тега - 1106");
            softly.assertEquals(tags.get("1107"), 0.0, "Неверное значение тега - 1107");

            softly.assertAll();
        }

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest);
    }

    //TODO добавить в тест добавление товара в бд или через кабинет
    @Test(enabled = false)
    public void withoutServiceTest() {
        server.clearRequests();
        steps.shift().closeShift();
        if (!manager.getConfigFields(CLC_SERVICE_SIGN).get(CLC_SERVICE_SIGN).equals("0")) {
            hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithoutServices(regNum));
            steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        }
        steps.shift().openShift();
        initFdNum = manager.getLastFdNum();
        steps.step().goToFreeMode();
        setPositionFromDb();
        manager.sleepPlease(5000);

        SoftAssert softly = new SoftAssert();
        hub_emulator.json.purchase.Purchase receipt = server.getLastRequest(PURCHASE_DOCUMENT_REPORT).getData().getPurchases()[0];
        softly.assertEquals(receipt.getCashier().getName(), cashierName);
        softly.assertEquals(receipt.getDiscountSum(), "0");
        softly.assertEquals(receipt.getNumberFd(), Integer.toString(initFdNum + 1));
        softly.assertEquals(receipt.getPayments()[0].getSum().toString(), "1111");
        softly.assertEquals(receipt.getPayments()[0].getType().toString(), "CASH");
        softly.assertEquals(receipt.getPositions()[0].getArticle(), "124563");
        softly.assertEquals(receipt.getPositions()[0].getDiscount(), 0);
        softly.assertEquals(receipt.getPositions()[0].getPrice(), 1111);
        softly.assertEquals(receipt.getPositions()[0].getProduct().getIndex(), "V_124563");
        softly.assertEquals(receipt.getPositions()[0].getProduct().getMeta().getArticles().get(0), "124563");
        softly.assertEquals(receipt.getPositions()[0].getProduct().getMeta().getPrice().toString(), "1111");
        softly.assertEquals(receipt.getPositions()[0].getProduct().getMeta().getTax(), "NDS_NO_TAX");
        softly.assertEquals(receipt.getPositions()[0].getProduct().getMeta().getType(), "SERVICE");
        softly.assertEquals(receipt.getPositions()[0].getQuantity(), 1000);
        softly.assertEquals(receipt.getPositions()[0].getTax(), "NDS_NO_TAX");
        softly.assertEquals(receipt.getSumWithouDiscounts(), "1111");
        softly.assertEquals(receipt.getTotalSum(), "1111");
        softly.assertEquals(receipt.getType(), "SALE");
        softly.assertAll();

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());

            Map<String, Object> tags = getTagsFromList(docFromFs);

            softly = new SoftAssert();
            softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК", "Неверное значение тега - 1000");
            softly.assertEquals(tags.get("1048"), "ООО \"Образец\"", "Неверное значение тега - 1048");
            softly.assertEquals(tags.get("1021"), "Администратор", "Неверное значение тега - 1021");
            softly.assertEquals(tags.get("1043"), "1111", "Неверное значение тега - 1043");
            softly.assertEquals(tags.get("1020"), "1111", "Неверное значение тега - 1020");
            softly.assertEquals(tags.get("1079"), "1111", "Неверное значение тега - 1079");
            softly.assertEquals(tags.get("1031"), "1111", "Неверное значение тега - 1031");
            softly.assertEquals(tags.get("1105"), "1111", "Неверное значение тега - 1105");
            softly.assertEquals(tags.get("1042"), 1.0, "Неверное значение тега - 1042");
            softly.assertEquals(tags.get("1055"), 1.0, "Неверное значение тега - 1055");
            softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тега - 1054");
            softly.assertEquals(tags.get("1081"), 0.0, "Неверное значение тега - 1081");
            softly.assertEquals(tags.get("1217"), 0.0, "Неверное значение тега - 1217");
            softly.assertEquals(tags.get("1216"), 0.0, "Неверное значение тега - 1216");
            softly.assertEquals(tags.get("1215"), 0.0, "Неверное значение тега - 1215");
            softly.assertEquals(tags.get("1107"), 0.0, "Неверное значение тега - 1107");
            softly.assertEquals(tags.get("1106"), 0.0, "Неверное значение тега - 1106");
            softly.assertEquals(tags.get("1104"), 0.0, "Неверное значение тега - 1104");
            softly.assertEquals(tags.get("1103"), 0.0, "Неверное значение тега - 1103");
            softly.assertEquals(tags.get("1102"), 0.0, "Неверное значение тега - 1102");
            softly.assertEquals(tags.get("1214"), "4", "Неверное значение тега - 1214");
            softly.assertEquals(tags.get("1212"), "4", "Неверное значение тега - 1212");
            softly.assertEquals(tags.get("1059"), null, "Неверное значение тега - 1059");
            softly.assertEquals(tags.get("1199"), "6", "Неверное значение тега - 1199");
            softly.assertEquals(tags.get("1030"), "cheburek", "Неверное значение тега - 1030");
            softly.assertEquals(tags.get("1023"), "1,0", "Неверное значение тега - 1023");
            softly.assertEquals(tags.get("1209"), "2", "Неверное значение тега - 1209");
            softly.assertEquals(tags.get("1040"), Integer.toString(initFdNum + 1), "Неверное значение тега - 1040");
            softly.assertAll();
        }

    }

    //---------------------------------------------- STEPS -------------------------------------------------------------

    private int getCountTag(List<Tag> docFromFs, Integer tagNum) {
        int count = 0;
        for (Tag tag : docFromFs) {
            if (tag.getOfdQuittance() != null) {
                continue;
            }
            if (tag.getTag().equals(tagNum)) {
                count++;
            }
        }
        return count;
    }

    @Step("Установка денежнего эквивалента, возвращает в копейках")
    private int setPosition() {
        String money = String.valueOf(random.nextInt(9) + 1);
        manager.pressKey(KeyEnum.valueOf("key" + money));

        manager.pressKey(KeyEnum.keyComma);

        String second = String.valueOf(random.nextInt(10));
        manager.pressKey(KeyEnum.valueOf("key" + second));
        money = money + second;
        String last = String.valueOf(random.nextInt(9) + 1);
        manager.pressKey(KeyEnum.valueOf("key" + last));
        money = money + last;

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        return Integer.valueOf(money);
    }

    @Step("Установка позиции из ДБ")
    private void setPositionFromDb() {
        manager.pressKey(KeyEnum.keyGoods);
        manager.pressKey("124563");
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter, 3);
        manager.sendCommands();
    }

    @Step("Установка предоплаты 100%")
    private void setPrepayment() {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.sendCommands();

        manager.sleepPlease(5000);
    }


    @Step("Проверка документа из ФН для предоплаты 100")
    private void checkDocumentFromFs(int money) {
        int fdNum = manager.getLastFdNum();
        assertEquals(fdNum - initFdNum, 1);
        List<Tag> docFromFs = manager.getDocFromFs(fdNum);
        Map<String, Object> tags = getTagsFromList(docFromFs);
        assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК");
        assertEquals(tags.get("1040"), Double.valueOf(fdNum));
        assertEquals(tags.get("1054"), 1.0);
        assertEquals(tags.get("1020"), Double.valueOf(money));
        assertEquals(tags.get("1030"), "Товар");
        assertEquals(tags.get("1214"), 1.0);
        assertEquals(tags.get("1079"), Double.valueOf(money));
        assertEquals(tags.get("1023"), "1,0");
        assertEquals(tags.get("1212"), 1.0);
        assertEquals(tags.get("1043"), Double.valueOf(money));
        assertEquals(tags.get("1031"), Double.valueOf(money));
        assertEquals(tags.get("1081"), 0.0);
        assertEquals(tags.get("1215"), 0.0);
        assertEquals(tags.get("1216"), 0.0);
        assertEquals(tags.get("1217"), 0.0);
        assertEquals(tags.get("1102"), 0.0);
        assertEquals(tags.get("1103"), 0.0);
        assertEquals(tags.get("1104"), 0.0);
        assertEquals(tags.get("1105"), Double.valueOf(money));
        assertEquals(tags.get("1106"), 0.0);
        assertEquals(tags.get("1107"), 0.0);
    }

    @Step("Проверка документа из ФН для предоплаты 100")
    private void checkReceivedRequest(int money) {
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        hub_emulator.json.purchase.Purchase purch;
        purch = server.getLastRequest(PURCHASE_DOCUMENT_REPORT).getData().getPurchases()[0];
        assertEquals(purch.getDiscountSum(), "0");
        assertEquals(purch.getPayments()[0].getType(), TypeResponseExPurchase.CASH);
        assertEquals(purch.getPayments()[0].getSum(), Integer.valueOf(money));
        assertEquals(purch.getPositions()[0].getNumber(), 1);
        assertEquals(purch.getPositions()[0].getQuantity(), 1000);
        assertEquals(purch.getPositions()[0].getPrice(), money);
        assertEquals(purch.getPositions()[0].getDiscount(), 0);
        assertEquals(purch.getPositions()[0].getTotal(), 0);
        assertEquals(purch.getPositions()[0].getTaxSum(), 0);
        assertEquals(purch.getPositions()[0].getProduct().getMeta().getPrice(), Integer.valueOf(money));
        assertEquals(purch.getSumWithouDiscounts(), Integer.toString(money));
        assertEquals(purch.getTotalSum(), Integer.toString(money));
    }

    private void checkPurchase(HubRequest hubRequest) {
        assertNotNull(hubRequest);
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData().getPurchases());
        assertEquals(hubRequest.getData().getPurchases().length, 1);

        assertNotNull(hubRequest.getData().getPurchases()[0].getCashier());
        assertEquals(hubRequest.getData().getPurchases()[0].getCashier().getName(), cashierName);

        assertEquals(hubRequest.getData().getPurchases()[0].getDiscountSum(), "0");

        assertNotNull(hubRequest.getData().getPurchases()[0].getPayments());
        assertEquals(hubRequest.getData().getPurchases()[0].getPayments()[0].getType(), CASH);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getQuantity(), 1000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getPrice(), 10000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTax(), "NDS_NO_TAX");
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTaxSum(), 0);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct());
        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getName(), "Товар");
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getPrice(), Integer.valueOf(10000));
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax(), "NDS_NO_TAX");
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getType(), "COUNTABLE");
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getScale(), Boolean.FALSE);

        assertNotNull(hubRequest.getData().getPurchases()[0].getSumWithouDiscounts(), "10000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getTotalSum(), "10000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getType(), "SALE");
    }

    private void addPrepayment(String sum) {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key3);
        manager.sendCommands();
        manager.pressKey(sum);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    private void addAdvance(String sum) {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key1);
        manager.sendCommands();
        manager.pressKey(sum);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    private Map<String, Object> getTagsFromList(List<Tag> docFromFs) {
        Map<String, Object> tags = new HashMap<>();
        for (Tag tag : docFromFs) {
            if (tag.getOfdQuittance() != null) {
                continue;
            }
            if ((tag.getTag() == 1217) && tags.containsKey("1217")) {
                fail();
            }
            tags.put(tag.getTag().toString(), tag.getValue());
        }
        return tags;
    }
}