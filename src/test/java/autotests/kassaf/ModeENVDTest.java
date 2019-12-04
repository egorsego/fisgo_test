package autotests.kassaf;

import application_manager.api_manager.CashBoxType;
import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.TypeResponseExPurchase;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.payment.PaymentsType;

import java.util.Random;

import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASH;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ModeENVDTest extends BaseTestClass {

    private final Random random = new Random();
    private String cashierName;

    @BeforeClass
    public void before() {
        initCashierName();
        steps.step().technicalZero();

        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();

        steps.shift().openShift();
        steps.step().connectBankTerminal();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void cash(){
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(PaymentsType.CASH);
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASH);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void cashless(){
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(PaymentsType.CASHLESS);
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void credit(){
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(PaymentsType.CREDIT);
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void prepaid(){
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(PaymentsType.PREPAID);
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void consideration(){
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(PaymentsType.CONSIDERATION);
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void prepayment100() {
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests();
        steps.step().goToFreeMode();
        int money = setPosition();
        setPrepayment();
        checkReceivedRequest(money);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void partPrepayment() {
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        addPrepayment("50");
        steps.payment().completePurchase();
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASH);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void advanceBuyCertificate() {
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key4);
        manager.sendCommands();
        steps.payment().completePurchase();
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASH);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void advanceOverflow() {
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        addAdvance("200");
        steps.payment().completePurchase();
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASH);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
    }

    @Test
    public void advancePaymentCertificate() {
        int lastFdNum = manager.getLastFdNum();
        server.clearRequests(PURCHASE_DOCUMENT_REPORT);
        steps.step().goToFreeMode();
        steps.payment().addPositions("100", 1);
        addAdvance("100");
        steps.payment().completePurchase();
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASH);
        int nextFdNum = manager.getLastFdNum();
        assertEquals(lastFdNum, nextFdNum);
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

    private void checkPurchase(HubRequest hubRequest, TypeResponseExPurchase type) {
        assertNotNull(hubRequest);
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData().getPurchases());
        assertEquals(hubRequest.getData().getPurchases().length, 1);

        assertNotNull(hubRequest.getData().getPurchases()[0].getCashier());
        assertEquals(hubRequest.getData().getPurchases()[0].getCashier().getName(), cashierName);

        assertEquals(hubRequest.getData().getPurchases()[0].getDiscountSum(), "0");

        assertNotNull(hubRequest.getData().getPurchases()[0].getPayments());
        assertEquals(hubRequest.getData().getPurchases()[0].getPayments()[0].getType(), type);

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

    private void initCashierName() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            cashierName = "Администратор";
        } else {
            cashierName = "Админ";
        }
    }

}
