package autotests.kassarb;

import application_manager.api_manager.json.response.data.CountersResponse;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.MethodsEnum;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static application_manager.cashbox.KeyEnum.*;
import static application_manager.api_manager.json.request.data.enums.CountersFieldsEnum.*;
import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;

public class CancellationTests extends BaseTestClass {

    private final static int COUNT_PAY_TYPES = 16;

    @BeforeClass
    public void setUp() {
        hubQueue.addResponse(MethodsEnum.WHO_AM_I, HubRequest.builder().result("OK").build());

        manager.iAmHub(true);
        manager.setPollTime(10);

        //подключить к кабинету
        manager.pressKey(keyMenu);
        manager.pressKey(key4);
        manager.pressKey(key6);
        manager.pressKey(key1, 6);
        manager.sendCommands();

        steps.expectation().waitLoader();
        steps.expectation().waitExpectedLcd("Касса", "успешно", "подключена");

        //GO TO FREE MODE
        manager.pressKey(keyCancel, 4);
        manager.sendCommands();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test(enabled = false)
    public void cancelChequeTest() {
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        SoftAssert softly = new SoftAssert();

        CountersResponse counters = manager.getCounters(NEXT_REC_NUM, REC_CNTS, REC_SUMS);
        int initRecNum = counters.getNextRecNum();
        double initCanceled = Double.parseDouble(counters.getRecSums().getCanceled());
        int initRecCounts = Integer.parseInt(counters.getRecCounts().get("CANCELED"));

        int sumOnOneType = 10;
        payDifferentTypesOfPayment(sumOnOneType);

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);

        cancelCheque(String.valueOf(initRecNum));

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 2);

        counters = manager.getCounters(NEXT_REC_NUM, REC_CNTS, REC_SUMS);
        double lastCanceled = Double.parseDouble(counters.getRecSums().getCanceled());
        int lastRecCounts = Integer.parseInt(counters.getRecCounts().get("CANCELED"));

        softly.assertEquals(initCanceled, lastCanceled - sumOnOneType * 16,
                "REC_SUMS - CANCELED error");
        softly.assertEquals(initRecCounts, lastRecCounts - 1,
                "REC_COUNT - CANCELED error");
        softly.assertAll();

        manager.pressKey(keyCancel);
        manager.sendCommands();
    }

    @Test
    public void cancelScalableGoodIntegerWeightTest() {
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        payScalableGood("11112", "вес1            ", "10");
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);

        CountersResponse counters = manager.getCounters(NEXT_REC_NUM);
        int initRecNum = counters.getNextRecNum();
        cancelCheque(String.valueOf(initRecNum - 1));

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 2);

        manager.pressKey(keyMenu);
        manager.pressKey(keyCancel, 2);
        manager.sendCommands();
    }

    @Test
    public void cancelScalableGoodNonIntegerWeightTest() {
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        payScalableGood("11112", "вес1            ", "1.9");
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);

        CountersResponse counters = manager.getCounters(NEXT_REC_NUM);
        int initRecNum = counters.getNextRecNum();
        cancelCheque(String.valueOf(initRecNum - 1));

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 2);

        manager.pressKey(keyMenu);
        manager.pressKey(keyCancel, 2);
        manager.sendCommands();
    }

    @Test
    public void cancelService() {
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        payGoodsFromDb("77777777", "1.Усл11");
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);

        CountersResponse counters = manager.getCounters(NEXT_REC_NUM);
        int initRecNum = counters.getNextRecNum();
        cancelCheque(String.valueOf(initRecNum - 1));

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 2);

        manager.pressKey(keyMenu);
        manager.pressKey(keyCancel, 2);
        manager.sendCommands();
    }

    @Test
    public void cancelCountableGood() {
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        manager.pressKey(keyGoods);
        manager.pressKey(keyEnter);
        manager.pressKey("222");

        manager.pressKey(keyEnter);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd("good33");

        manager.pressKey("100");

        manager.pressKey(keyEnter, 4);
        manager.sendCommands();

        steps.expectation().waitLoader();
        manager.pressKey(keyEnter);
        manager.sendCommands();

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);

        CountersResponse counters = manager.getCounters(NEXT_REC_NUM);
        int initRecNum = counters.getNextRecNum();
        cancelCheque(String.valueOf(initRecNum - 1));

        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 2);

        manager.pressKey(keyMenu);
        manager.pressKey(keyCancel, 2);
        manager.sendCommands();
    }

    @Test(enabled = false)
    public void cancelChequeWithChange() {
        CountersResponse counters = manager.getCounters(NEXT_REC_NUM, REC_CNTS, REC_SUMS);
        int initRecNum = counters.getNextRecNum();
        double initCanceled = Double.parseDouble(counters.getRecSums().getCanceled());
        int initRecCounts = Integer.parseInt(counters.getRecCounts().get("CANCELED"));

        int priceWithoutChange = 10;
        int sumWithChange = 100;

        manager.pressKey(String.valueOf(priceWithoutChange));
        manager.pressKey(keyEnter, 3);
        manager.pressKey(String.valueOf(sumWithChange));
        manager.pressKey(keyEnter);
        manager.sendCommands();

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }

        //FIXME после того как можно будет скипнуть сообщение
        manager.sleepPlease(9000);
        //manager.pressKey(keyboard.getKeyValue(keyEnter), 0, 1);
        manager.sendCommands();
        cancelCheque(String.valueOf(initRecNum));

        counters = manager.getCounters(NEXT_REC_NUM, REC_CNTS, REC_SUMS);
        double lastCanceled = Double.parseDouble(counters.getRecSums().getCanceled());
        int lastRecCounts = Integer.parseInt(counters.getRecCounts().get("CANCELED"));

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(lastCanceled, initCanceled + priceWithoutChange);
        softly.assertEquals(lastRecCounts, initRecCounts + 1);
        softly.assertAll();

        manager.pressKey(keyCancel);
        manager.sendCommands();
    }

    private void openShiftRb() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key1);
        manager.pressKey(keyEnter);
        manager.sendCommands();
        manager.sleepPlease(1000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);

        manager.sendCommands();
        manager.sleepPlease(1000);
    }

    private void closeShiftRb() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key2);
        manager.pressKey(keyEnter);
        manager.pressKey(keyCancel);
        manager.sendCommands();
        manager.sleepPlease(3000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
    }

    private void saleOnePosition() {
        manager.pressKey(keyCancel);
        manager.pressKey(key5, 2);
        manager.pressKey(keyEnter, 4);
        manager.sendCommands();
        manager.sleepPlease(2000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    private void cancelCheque(String num) {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key6);
        manager.pressKey(num);
        manager.pressKey(keyEnter, 3);
        manager.sendCommands();

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }

        steps.expectation().waitExpectedLcd("Документ", "аннулирован");

        manager.pressKey(keyEnter);
        manager.pressKey(keyMenu);
        manager.sendCommands();
    }

    private void operateDifferentTypes(int sumOnOneType) {
        manager.pressKey(String.valueOf(sumOnOneType * COUNT_PAY_TYPES));
        manager.pressKey(keyEnter, 2);
        manager.pressKey(keyUp);
        manager.pressKey(keyEnter);

        chooseType(sumOnOneType, 1);
        chooseType(sumOnOneType, 2);
        chooseType(sumOnOneType, 3);
        chooseType(sumOnOneType, 4);
        chooseType(sumOnOneType, 5);
        chooseType(sumOnOneType, 6);
        chooseType(sumOnOneType, 7);
        chooseType(sumOnOneType, 8);
        chooseType(sumOnOneType, 9);
        chooseType(sumOnOneType, 10);
        chooseType(sumOnOneType, 11);
        chooseType(sumOnOneType, 12);
        chooseType(sumOnOneType, 13);
        chooseType(sumOnOneType, 14);
        chooseType(sumOnOneType, 15);
        chooseType(0, 16);

        manager.pressKey(keyEnter);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(2000);
        }

        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    private void chooseType(int sum, int numberType) {
        if (numberType == 1) {
            manager.pressKey(keyEnter);
            manager.pressKey(String.valueOf(sum));
            manager.pressKey(keyEnter);
            manager.sendCommands();
            return;
        }

        manager.pressKey(keyDown, numberType - 1);
        manager.pressKey(keyEnter);
        if (sum == 0) {
            manager.pressKey(keyEnter);
            manager.sendCommands();
            return;
        }
        manager.pressKey(String.valueOf(sum));
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    @Step
    private void payDifferentTypesOfPayment(int sumOnOneType) {
        operateDifferentTypes(sumOnOneType);
    }

    private void payScalableGood(String article, String goodName, String weight) {
        manager.pressKey(keyGoods);
        manager.pressKey(keyEnter);
        manager.pressKey(article);
        manager.pressKey(keyEnter);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd(goodName);

        manager.pressKey(weight);
        manager.pressKey(keyEnter, 4);
        manager.sendCommands();

        steps.expectation().waitLoader();
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    private void payGoodsFromDb(String article, String goodName) {
        manager.pressKey(keyGoods);
        manager.pressKey(keyEnter);
        manager.pressKey(article);
        manager.pressKey(keyEnter);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd(goodName);

        manager.pressKey(keyEnter, 3);
        manager.sendCommands();

        steps.expectation().waitLoader();
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

}
