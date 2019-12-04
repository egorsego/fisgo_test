package autotests.general;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.json.response.data.counters_data.XZData;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.handshake.In;
import hub_emulator.json.handshake.Methods;
import hub_emulator.json.handshake.Out;
import hub_emulator.json.purchase.Payments;
import hub_emulator.json.purchase.Positions;
import hub_emulator.json.purchase.Purchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static application_manager.api_manager.json.request.data.enums.CountersFieldsEnum.SALE_SUMS;
import static application_manager.api_manager.json.request.data.enums.CountersFieldsEnum.X_Z_DATA;
import static hub_emulator.response.enums.MethodsEnum.*;
import static org.testng.Assert.*;

public class JsonHubTests extends BaseTestClass {

    @BeforeClass
    public void beforeJsonCabinetTests() {
        manager.iAmHub(true);
        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
    }

    @AfterClass
    public void afterJsonCabinetTests() {
        manager.iAmHub(false);
    }

    @Test
    public void testHandshake() {
        //ребут кассы
        manager.rebootFiscat();
        manager.iAmHub(true);
        steps.step().inputPassword();

        //проверка что handshake пришел
        assertTrue(server.checkReceivedRequest(HANDSHAKE, 1));
        HubRequest handshake = server.getLastRequest(HANDSHAKE);
        checkHandshakeOnStructure(handshake);

        //Инициализация "мягкого ассерта" и проверка всех полей на необходимые значения
        SoftAssert softly = new SoftAssert();
        checkOutMethodsHandshake(softly, handshake);
        checkInMethodsHandshake(softly, handshake);
    }

    @Test
    public void testPurchaseDocumentReport() {
        //покупка на одну позицию налом
        steps.payment().payOnePositionCash(100);
        //проверка что PURCHASE_DOCUMENT_REPORT пришел
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastPurchase = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchaseOnStructure(lastPurchase);
    }

    @Test
    public void testCountersReport() {
        steps.shift().openShift();
        steps.step().goToFreeMode();
        server.clearRequests();

        //покупка на одну позицию налом
        steps.payment().payOnePositionCash(100);

        steps.step().connectBankTerminal();
        steps.step().goToFreeMode();

        //покупка на одну позицию
        steps.payment().payOnePositionCard(200);

        //проверка что COUNTERS_REPORT пришел
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 2));

        HubRequest counterReport = server.getLastRequest(COUNTERS_REPORT);
        checkCountersReportOnStructure(counterReport);

        //TODO ПОТОМУ ЧТО СЧЕТЧИКИ НЕ РАБОТАЮТ НА ДКФ
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            Map<String, String> saleSums = manager.getCounters(SALE_SUMS).getSaleSums();

            String saleSumsCash = saleSums.get("CASH");
            Integer cash = ((Double) (Double.parseDouble(saleSumsCash) * 100)).intValue();
            assertEquals(counterReport.getData().getCash(), cash);

            String saleSumsCard = saleSums.get("CARD");
            Integer cashless = ((Double) (Double.parseDouble(saleSumsCard) * 100)).intValue();
            assertEquals(counterReport.getData().getCashless(), cashless);

            steps.shift().closeShift();

            XZData xzData = manager.getCounters(X_Z_DATA).getXzData();

            steps.shift().openShift();

            String cashInDrawer = xzData.getCashInDrawer();
            Integer balance = ((Double) (Double.parseDouble(cashInDrawer) * 100)).intValue();
            assertEquals(counterReport.getData().getBalance(), balance);

            String saleSum = xzData.getSaleSum();
            Integer receipts = ((Double) (Double.parseDouble(saleSum) * 100)).intValue();
            assertEquals(counterReport.getData().getReceipts(), receipts);
        }
    }

    @Test
    public void testStats(){
        if(cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)){
            return;
        }
        server.clearRequests();
        assertTrue(server.checkReceivedRequest(STATS, 1));
        HubRequest lastRequest = server.getLastRequest(STATS);

        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData());
        assertNotNull(lastRequest.getData().getFnFlags());
        assertNotNull(lastRequest.getData().getNeedSendToOfd());

        assertFalse(lastRequest.getData().getFnFlags().isMemOverflow());
        assertFalse(lastRequest.getData().getFnFlags().isCriticaFnErr());
        assertFalse(lastRequest.getData().getFnFlags().isResourcesExhastion());
        assertFalse(lastRequest.getData().getFnFlags().isImmediateReplacement());
    }

    //_____________________________________________STEPS________________________________________________________________

    /**
     * Проверка на структуру json COUNTERS_REPORT
     */
    @Step
    private void checkCountersReportOnStructure(HubRequest hubRequest) {
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData());

        assertNotNull(hubRequest.getData().getCash());
        assertNotNull(hubRequest.getData().getCashless());
        assertNotNull(hubRequest.getData().getReceipts());
        assertNotNull(hubRequest.getData().getBalance());
    }

    /**
     * Проверка на структуру json PURCHASE
     */
    @Step
    private void checkPurchaseOnStructure(HubRequest hubRequest) {
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getData().getPurchases());

        for (Purchase purchase : hubRequest.getData().getPurchases()) {
            assertNotNull(purchase.getShift());

            if (!purchase.getNumberFn().isEmpty()) {
                assertNotNull(purchase.getNumberFn());
                assertNotNull(purchase.getNumberFd());
                assertNotNull(purchase.getRegistryNumber());
                assertNotNull(purchase.getFiscalSign());
            }

            assertNotNull(purchase.getNumber());
            assertNotNull(purchase.getDate());
            assertNotNull(purchase.getType());
            assertNotNull(purchase.getTotalSum());
            assertNotNull(purchase.getSumWithouDiscounts());
            assertNotNull(purchase.getDiscountSum());
            assertNotNull(purchase.getCashier());
            assertNotNull(purchase.getCashier().getName());

            assertNotNull(purchase.getPositions());
            for (Positions position : purchase.getPositions()) {
                assertNotNull(position.getNumber());
                assertNotNull(position.getQuantity());
                assertNotNull(position.getPrice());
                assertNotNull(position.getDiscount());
                assertNotNull(position.getTax());
                assertNotNull(position.getProduct());
                assertNotNull(position.getProduct().getMeta());
                assertNotNull(position.getProduct().getMeta().getTax());
                assertNotNull(position.getProduct().getMeta().getType());
            }
            Payments[] payments = purchase.getPayments();
            for (Payments payment : payments) {
                assertNotNull(payment.getType());
                assertNotNull(payment.getSum());
            }
        }
    }

    /**
     * Проверка на структуру json handshake
     */
    @Step
    private void checkHandshakeOnStructure(HubRequest hubRequest) {
        HubData data = hubRequest.getData();
        assertNotNull(data);

        Methods methods = hubRequest.getData().getMethods();
        assertNotNull(methods);

        In in = hubRequest.getData().getMethods().getIn();
        assertNotNull(in);

        Out out = hubRequest.getData().getMethods().getOut();
        assertNotNull(out);
    }

    /**
     * Проверка версии in методов в handshake
     */
    @Step
    private void checkInMethodsHandshake(SoftAssert softly, HubRequest hubRequest) {
        int upsertProduct = hubRequest.getData().getMethods().getIn().getUpsertProduct();
        softly.assertEquals(upsertProduct, 5, "Ошибка версии upsertProduct");

        int bindProduct = hubRequest.getData().getMethods().getIn().getBindProduct();
        softly.assertEquals(bindProduct, 1, "Ошибка версии bindProduct");

        int deleteProduct = hubRequest.getData().getMethods().getIn().getDeleteProduct();
        softly.assertEquals(deleteProduct, 1, "Ошибка версии deleteProduct");

        int upsertDiscount = hubRequest.getData().getMethods().getIn().getUpsertDiscount();
        softly.assertEquals(upsertDiscount, 1, "Ошибка версии upsertDiscount");

        int deleteDiscount = hubRequest.getData().getMethods().getIn().getDeleteDiscount();
        softly.assertEquals(deleteDiscount, 1, "Ошибка версии deleteDiscount");

        int registration = hubRequest.getData().getMethods().getIn().getRegistration();
        softly.assertEquals(registration, 1, "Ошибка версии registration");

        int externalPurchase = hubRequest.getData().getMethods().getIn().getExternalPurchase();
        softly.assertEquals(externalPurchase, 1, "Ошибка версии externalPurchase");
    }

    /**
     * Проверка версии out методов в handshake
     */
    @Step
    private void checkOutMethodsHandshake(SoftAssert softly, HubRequest hubRequest) {
        int handshake = hubRequest.getData().getMethods().getOut().getHandshake();
        softly.assertEquals(handshake, 2, "Ошибка версии handshake");

        int purchaseDocumentReport = hubRequest.getData().getMethods().getOut().getPurchaseDocumentReport();
        softly.assertEquals(purchaseDocumentReport, 3, "Ошибка версии purchase_document_report");

        int moneyDocumentReport = hubRequest.getData().getMethods().getOut().getMoneyDocumentReport();
        softly.assertEquals(moneyDocumentReport, 1, "Ошибка версии moneyDocumentReport");

        int unregister = hubRequest.getData().getMethods().getOut().getUnregister();
        softly.assertEquals(unregister, 1, "Ошибка версии unregister");

        int shiftDocumentReport = hubRequest.getData().getMethods().getOut().getShiftDocumentReport();
        softly.assertEquals(shiftDocumentReport, 1, "Ошибка версии shiftDocumentReport");

        int cashInfoReport = hubRequest.getData().getMethods().getOut().getCashInfoReport();
        softly.assertEquals(cashInfoReport, 1, "Ошибка версии cashInfoReport");

        int poll = hubRequest.getData().getMethods().getOut().getPoll();
        softly.assertEquals(poll, 1, "Ошибка версии poll");

        int kktRegisterInfo = hubRequest.getData().getMethods().getOut().getKktRegisterInfo();
        softly.assertEquals(kktRegisterInfo, 1, "Ошибка версии kktRegisterInfo");

        int register = hubRequest.getData().getMethods().getOut().getRegister();
        softly.assertEquals(register, 1, "Ошибка версии register");

        int registrationReport = hubRequest.getData().getMethods().getOut().getRegistrationReport();
        softly.assertEquals(registrationReport, 1, "Ошибка версии registrationReport");

        int whoAmI = hubRequest.getData().getMethods().getOut().getWhoAmI();
        softly.assertEquals(whoAmI, 1, "Ошибка версии whoAmI");

        int countersReport = hubRequest.getData().getMethods().getOut().getCountersReport();
        softly.assertEquals(countersReport, 1, "Ошибка версии countersReport");

        int searchProduct = hubRequest.getData().getMethods().getOut().getSearchProduct();
        softly.assertEquals(searchProduct, 1, "Ошибка версии searchProduct");
    }
}
