package autotests.kassaf;

import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.json.purchase.Purchase;
import hub_emulator.response.enums.TypeResponseExPurchase;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import steps.payment.PaymentsType;

import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class KFReturnConsumptionTest extends BaseTestClass {

    private int previousFdNum;

    @BeforeClass
    public void beforeClass() {
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.step().connectBankTerminal();
        steps.shift().openShift();
        goToReturn();
    }

    @AfterClass
    public void afterClass() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }

    @Test
    public void returnConsumptionCash() {
        clearData();
        previousFdNum = manager.getLastFdNum();
        goToReturnConsumption();
        performReturn(PaymentsType.CASH);
        checkPurchaseDocumentReport(TypeResponseExPurchase.CASH);
        assertEquals(previousFdNum + 1, manager.getLastFdNum());
    }

    @Test
    public void returnConsumptionCashless() {
        clearData();
        previousFdNum = manager.getLastFdNum();
        goToReturnConsumption();
        performReturn(PaymentsType.CASHLESS);
        checkPurchaseDocumentReport(TypeResponseExPurchase.CASHLESS);
        assertEquals(previousFdNum + 1, manager.getLastFdNum());
    }

    @Test
    public void returnConsumptionPrepaid() {
        clearData();
        previousFdNum = manager.getLastFdNum();
        goToReturnConsumption();
        performReturn(PaymentsType.PREPAID);
        checkPurchaseDocumentReport(TypeResponseExPurchase.CASHLESS);
        assertEquals(previousFdNum + 1, manager.getLastFdNum());
    }

    @Test
    public void returnConsumptionConsideration() {
        clearData();
        previousFdNum = manager.getLastFdNum();
        goToReturnConsumption();
        performReturn(PaymentsType.CONSIDERATION);
        checkPurchaseDocumentReport(TypeResponseExPurchase.CASHLESS);
        assertEquals(previousFdNum + 1, manager.getLastFdNum());
    }

    @Test
    public void returnConsumptionCredit() {
        clearData();
        previousFdNum = manager.getLastFdNum();
        goToReturnConsumption();
        performReturn(PaymentsType.CREDIT);
        checkPurchaseDocumentReport(TypeResponseExPurchase.CASHLESS);
        assertEquals(previousFdNum + 1, manager.getLastFdNum());
    }

    //__________________________________________________________________________________________________________________
    //_________________________________________________ STEP ___________________________________________________________

    private void performReturn(PaymentsType cashless) {
        steps.payment().addPositions("100", 1);
        steps.payment().completePurchase(cashless);
    }

    private void goToReturn() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();
    }

    private void goToReturnConsumption() {
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();
        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_REVERT_ENTER_SUM, null);
    }

    private void clearData() {
        server.clearRequests();
        manager.getEventsContainer().clearLcdEvents();
    }

    private void checkPurchaseDocumentReport(TypeResponseExPurchase cash) {
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, cash);
    }

    private void checkPurchase(HubRequest hubRequest, TypeResponseExPurchase type) {
        assertNotNull(hubRequest);
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getUuid());

        Purchase[] purchases = hubRequest.getData().getPurchases();
        assertNotNull(purchases);
        assertEquals(purchases.length, 1);
        assertNotNull(purchases[0].getCashier());
        assertEquals(purchases[0].getCashier().getName(), "Администратор");
        assertEquals(purchases[0].getDiscountSum(), "0");
        assertNotNull(purchases[0].getPayments());
        assertEquals(purchases[0].getPayments()[0].getType(), type);
        assertNotNull(purchases[0].getPositions());
        assertEquals(purchases[0].getPositions()[0].getQuantity(), 1000);
        assertEquals(purchases[0].getPositions()[0].getPrice(), 10000);
        assertEquals(purchases[0].getPositions()[0].getTax(), "NDS_NO_TAX");
        assertEquals(purchases[0].getPositions()[0].getTaxSum(), 0);
        assertNotNull(purchases[0].getPositions()[0].getProduct());
        assertNotNull(purchases[0].getPositions()[0].getProduct().getMeta());
        assertEquals(purchases[0].getPositions()[0].getProduct().getMeta().getName(), "Товар");
        assertEquals(purchases[0].getPositions()[0].getProduct().getMeta().getPrice(), Integer.valueOf(10000));
        assertEquals(purchases[0].getPositions()[0].getProduct().getMeta().getTax(), "NDS_NO_TAX");
        assertEquals(purchases[0].getPositions()[0].getProduct().getMeta().getType(), "COUNTABLE");
        assertEquals(purchases[0].getPositions()[0].getProduct().getMeta().getScale(), Boolean.FALSE);
        assertNotNull(purchases[0].getSumWithouDiscounts(), "10000");
        assertNotNull(purchases[0].getTotalSum(), "10000");
        assertNotNull(purchases[0].getType(), "REFUND");
    }

}
