package autotests.general;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.api_manager.json.response.data.Tag;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import hub_emulator.response.enums.TypeResponseExPurchase;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.CLC_SERVICE_SIGN;
import static hub_emulator.response.enums.MethodsEnum.*;
import static hub_emulator.response.enums.TypeResponseExPurchase.*;
import static org.testng.Assert.*;

@Log4j
public class ExternalPurchaseTest extends BaseTestClass {

    private String ndsType10 = "NDS_10";
    private String email = "g.glushkov@dreamkas.ru";
    private String goodName = "Товар";
    private String positionType = "COUNTABLE";
    private String taxMode = "DEFAULT";
    private String taskType = "external_purchase";

    private String cashierName;

    @BeforeClass
    public void beforeExternalPurchaseTests() {
        initCashierName();

        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        steps.shift().closeShift();
        regNum = manager.getRegNumKKT();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        includePrintExPurchase();
        steps.shift().openShift();
    }

    @AfterClass
    public void afterExternalPurchaseTests() {
        manager.iAmHub(false);
    }

    @Test
    public void allTypesTest() {
        server.clearRequests();
        steps.payment().sendExternalPurchase(TypeResponseExPurchase.ALL);

        List<HubRequest> purchaseRequests = server.getRequests(PURCHASE_DOCUMENT_REPORT);

        steps.hub().checkPollSuccessResults();

        checkPurchase(purchaseRequests.get(0), CASHLESS);
        checkPurchase(purchaseRequests.get(1), CASH);
        checkPurchase(purchaseRequests.get(2), CREDIT);
        checkPurchase(purchaseRequests.get(3), PREPAID);
        checkPurchase(purchaseRequests.get(4), CONSIDERATION);

        manager.sleepPlease(3000);

        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Test
    public void cashlessTest() {
        checkExternalPurchase(CASHLESS);
    }

    @Test
    public void cashTest() {
        checkExternalPurchase(CASH);
    }

    @Test
    public void creditTest() {
        checkExternalPurchase(CREDIT);
    }

    @Test
    public void prepaidTest() {
        checkExternalPurchase(PREPAID);
    }

    @Test
    public void considerationTest() {
        checkExternalPurchase(CONSIDERATION);
    }

    @Test
    public void testNds20InExternalPurchase() {
        clearLicense();
        server.clearRequests();
        int lastNumDoc = manager.getLastFdNum();

        HubRequest purchaseWithNds20 = getPurchaseWithNds20();

        steps.hub().answerPoll(purchaseWithNds20);
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastPurchaseReport = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);

        checkNdsTagsFromFn(++lastNumDoc, "305");

        checkPurchaseWithNds(lastPurchaseReport, "NDS_18", "NDS_18_CALCULATED");

        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        steps.hub().checkPollSuccessResults();

        server.clearRequests();
        steps.hub().answerPoll(purchaseWithNds20);
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        checkPurchaseWithNds(server.getLastRequest(PURCHASE_DOCUMENT_REPORT), "NDS_20", "NDS_20_CALCULATED");

        checkNdsTagsFromFn(++lastNumDoc, "333");
    }


    //__________________________________________________________________________________________________________________
    //_____________________________Тесты на внешний чек по предоплате___________________________________________________

    @Test
    public void prepayment100() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 1)));
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Test
    public void fullPayment() {
        if(cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)){
            return;
        }
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 4)));
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));

        List<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());
        System.out.println();
    }

    @Test
    public void partPrepayment() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(
                new Tags(1214, 2), new Tags(777, 1000)));
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Test
    public void failPartPrepaymentWithout777() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 2)));
        HubRequest lastRequest = server.getLastRequest(POLL);
        checkFailPurchase(lastRequest, "NEED_SET_SUM_OF_PREPAYMENT");
    }

    @Test
    public void failPartPrepaymentIncorrect1214() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 600)));
        HubRequest lastRequest = server.getLastRequest(POLL);
        checkFailPurchase(lastRequest, "UNKNOWN_TAG_VALUE_1214");
    }

    @Test
    public void failPartPrepayment777moreThanTotal() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 2), new Tags(777, 2000)));
        HubRequest lastRequest = server.getLastRequest(POLL);
        checkFailPurchase(lastRequest, "PREPAYMENT_MORE_OR_EQ_THAN_SUM");
    }

    @Test
    void testManyTasksWithOneInvalidPurchase() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchasesWithOneInvalid());
        HubRequest lastRequest = server.getLastRequest(POLL);

        assertEquals(lastRequest.getData().getTaskResults().length, 4, "POLL не содержит 4 результатов");
        assertEquals(lastRequest.getData().getTaskResults()[0].getResult(), "SUCCESS");
        assertEquals(lastRequest.getData().getTaskResults()[1].getResult(), "ERROR");
        assertEquals(lastRequest.getData().getTaskResults()[2].getResult(), "SUCCESS");
        assertEquals(lastRequest.getData().getTaskResults()[3].getResult(), "SUCCESS");
    }

    //__________________________________________________________________________________________________________________
    //_____________________________Тесты на внешний чек (аванс)_________________________________________________________

    @Test
    public void advance_buyCertificate() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithPositionTags(new Tags(1214, 3)));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
    }

    @Test
    public void advance_paymentCertificate() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithDataTags(new Tags(1215, 1000)));
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Test
    public void advance_overflow() {
        server.clearRequests();
        steps.hub().answerPoll(getPurchaseWithDataTags(new Tags(1215, 10000)));
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(lastRequest, CASHLESS);
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Test
    public void testTimeExecutionExternalPurchase() {
        server.clearRequests();

        HubRequest purchasesWith30Positions = getPurchasesWith30Positions();

        ArrayList<Long> allTimes = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            server.clearRequests(PURCHASE_DOCUMENT_REPORT);
            long startTime = System.nanoTime();
            steps.hub().answerPoll(purchasesWith30Positions);
            steps.hub().checkPollSuccessResults();
            assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 15));
            long estimate = (System.nanoTime() - startTime) / 1_000_000_000;
            allTimes.add(estimate);
        }

        long a = 0;
        for (Long l : allTimes) {
            a += l;
        }

        System.out.println("СРЕДНЕЕ ВРЕМЯ ВЫПОЛНЕНИЯ ОПЕРАЦИИ - " + a / allTimes.size() + " сек.");
        assertTrue(a / allTimes.size() <= 60);
    }

    @Test
    public void serviceTest() {
        server.clearRequests();
        steps.shift().closeShift();

        if (!manager.getConfigFields(CLC_SERVICE_SIGN).get(CLC_SERVICE_SIGN).equals("0")) {
            hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithoutServices(regNum));
            steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
            steps.hub().checkPollSuccessResults();
        }
        steps.shift().openShift();

        server.clearRequests();

        steps.hub().answerPoll(getPurchaseWithServiceGood());
        steps.hub().checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            List<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());

            Map<String, Object> tags = new HashMap<>();
            for (Tag tag : docFromFs) {
                tags.put(tag.getTag().toString(), tag.getValue());
            }

            assertEquals(tags.get("1212"), 4.0);
        }
    }

    @Test
    public void testStatsIfNeedDocOfd() {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            log.debug("Выходим из теста так как ДКФ");
            return;
        }
        steps.shift().closeShift();
        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithIncorrectOfd(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        for (int i = 0; i < 3; i++) {
            steps.hub().answerPoll(RepositoryPollResponse.getExPurch(CASHLESS));
            steps.hub().checkPollSuccessResults();
        }

        server.clearRequests();
        assertTrue(server.checkReceivedRequest(STATS, 1));
        HubRequest lastRequest = server.getLastRequest(STATS);
        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData().getNeedSendToOfd());
        assertTrue(lastRequest.getData().getNeedSendToOfd() >= 3);

        steps.step().technicalZero();
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.shift().closeShift();
        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
    }

    @Test
    public void testIncorrectValueEmail() {
        steps.hub().answerPoll(getExPurchWithIncorrectEmail(CASHLESS));

        HubRequest lastPoll = server.getLastRequest(POLL);
        assertNotNull(lastPoll);
        assertNotNull(lastPoll.getData());
        assertNotNull(lastPoll.getData().getTaskResults());
        for (TaskResults taskResults : lastPoll.getData().getTaskResults()) {
            assertEquals("SUCCESS", taskResults.getResult());
        }
    }

    //---------------------------------------------- STEPS -------------------------------------------------------------

    private HubRequest getExPurchWithIncorrectEmail(TypeResponseExPurchase typeResponseExPurchase) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_10").taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(typeResponseExPurchase).build());
        PollTaskData data = PollTaskData.builder()
                .remId("54651022bffebc03098b4561")
                .taxMode("DEFAULT")
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email("g.glushkovdreamkas.ru").build())
                .total(total)
                .build();

        tasks[0] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                .data(data)
                .taskType("external_purchase").build();

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    @Step
    private void checkFailPurchase(HubRequest lastRequest, String message) {
        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData());
        assertNotNull(lastRequest.getUuid());
        assertNotNull(lastRequest.getUrl());
        assertNotNull(lastRequest.getData().getTaskResults());
        assertNotNull(lastRequest.getData().getTaskResults()[0]);
        assertNotNull(lastRequest.getData().getTaskResults()[0].getResult());
        assertEquals(lastRequest.getData().getTaskResults()[0].getResult(), "ERROR");
        assertNotNull(lastRequest.getData().getTaskResults()[0].getMessage());
        assertEquals(lastRequest.getData().getTaskResults()[0].getMessage(), message);
        assertNotNull(lastRequest.getData().getTaskResults()[0].getErrorCode());
        assertEquals(lastRequest.getData().getTaskResults()[0].getErrorCode(), message);
    }

    @Step
    private void checkExternalPurchase(TypeResponseExPurchase type) {
        server.clearRequests();
        steps.payment().sendExternalPurchase(type);
        HubRequest purchaseRequest = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);
        checkPurchase(purchaseRequest, type);

        //FIXME
        assertTrue(server.checkReceivedRequest(COUNTERS_REPORT, 1));
    }

    @Step("Проверка JSON PURCHASE_DOCUMENT_REPORT - БЕЗНАЛ")
    private void checkPurchase(HubRequest hubRequest, TypeResponseExPurchase type) {
        assertNotNull(hubRequest);
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData().getPurchases());
        assertEquals(1, hubRequest.getData().getPurchases().length);

        assertNotNull(hubRequest.getData().getPurchases()[0].getCashier());
        assertEquals(hubRequest.getData().getPurchases()[0].getCashier().getName(), cashierName);

        assertEquals(hubRequest.getData().getPurchases()[0].getDiscountSum(), "0");

        assertNotNull(hubRequest.getData().getPurchases()[0].getPayments());
        assertEquals(hubRequest.getData().getPurchases()[0].getPayments()[0].getType(), type);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getQuantity(), 2000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getPrice(), 1000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTax(), ndsType10);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTaxSum(), 0);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct());
        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getName(), goodName);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getPrice(), Integer.valueOf(1000));
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax(), ndsType10);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getType(), positionType);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getScale(), Boolean.FALSE);

        assertNotNull(hubRequest.getData().getPurchases()[0].getSumWithouDiscounts(), "2000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getTotalSum(), "2000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getType(), "SALE");
        assertNotNull(hubRequest.getData().getPurchases()[0].getRemId(), "54651022bffebc03098b4561");
    }

    @Step("Перерегистрация с признаком - интернет магазин")
    private void reRegistrationWithInternetShop() {
        if (!manager.getConfigFields(ConfigFieldsEnum.KKT_SIGNS).get(ConfigFieldsEnum.KKT_SIGNS).equals("40")) {
            manager.pressKey(KeyEnum.keyMenu);
            manager.pressKey(KeyEnum.key5);
            manager.pressKey(KeyEnum.key1);
            manager.pressKey(KeyEnum.key7);
            manager.pressKey(KeyEnum.keyEnter, 3);
            manager.pressKey(KeyEnum.key7);
            manager.pressKey(KeyEnum.keyEnter, 4);
            manager.sendCommands();
            manager.sleepPlease(3500);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
            steps.expectation().waitLoader();
        }
    }

    @Step
    private void includePrintExPurchase() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key6);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();
        manager.sleepPlease(1000);
    }

    @Step
    private HubRequest getPurchaseWithPositionTags(Tags... tags) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name(goodName)
                .type(positionType)
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax(ndsType10)
                .taxSum(0)
                .tags(new ArrayList<>(Arrays.asList(tags)))
                .build());


        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        for (int i = 1; i < tasks.length + 1; i++) {
            tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskData(positions, total, CASHLESS, "54651022bffebc03098b456" + i))
                    .taskType(taskType).build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    @Step
    private HubRequest getPurchaseWithDataTags(Tags... tags) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name(goodName)
                .type(positionType)
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax(ndsType10)
                .taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(CASHLESS).build());

        PollTaskData pollTaskData = PollTaskData.builder()
                .remId("54651022bffebc03098b4562")
                .taxMode(taxMode)
                .type("SALE")
                .positions(positions)
                .tags(new ArrayList<>(Arrays.asList(tags)))
                .payments(payments)
                .attributes(Attributes.builder().email(email).build())
                .total(total)
                .build();

        tasks[0] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                .data(pollTaskData)
                .taskType(taskType).build();


        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total, TypeResponseExPurchase paymentsType, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(CASHLESS).build());
        return PollTaskData.builder()
                .remId(remId)
                .taxMode(taxMode)
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email(email).build())
                .total(total)
                .build();
    }

    @Step
    private HubRequest getPurchaseWithNds20() {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name(goodName)
                .type(positionType)
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax("NDS_20")
                .taxSum(0)
                .build());
        positions.add(Positions.builder()
                .name(goodName)
                .type(positionType)
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax("NDS_20_CALCULATED")
                .taxSum(0)
                .build());


        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(CASHLESS).build());
        PollTaskData pollTaskData = PollTaskData.builder()
                .remId("54651022bffebc03098b4562")
                .taxMode(taxMode)
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email(email).build())
                .total(total)
                .build();

        tasks[0] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                .data(pollTaskData)
                .taskType(taskType).build();


        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private void checkPurchaseWithNds(HubRequest hubRequest, String nds1, String nds2) {
        assertNotNull(hubRequest);
        assertNotNull(hubRequest.getData());
        assertNotNull(hubRequest.getUuid());
        assertNotNull(hubRequest.getData().getPurchases());
        assertEquals(1, hubRequest.getData().getPurchases().length);

        assertNotNull(hubRequest.getData().getPurchases()[0].getCashier());
        assertEquals(hubRequest.getData().getPurchases()[0].getCashier().getName(), cashierName);

        assertEquals(hubRequest.getData().getPurchases()[0].getDiscountSum(), "0");

        assertNotNull(hubRequest.getData().getPurchases()[0].getPayments());
        assertEquals(hubRequest.getData().getPurchases()[0].getPayments()[0].getType(), CASHLESS);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getQuantity(), 2000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getPrice(), 1000);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTax(), nds1);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getTaxSum(), 0);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct());
        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getName(), goodName);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getPrice(), Integer.valueOf(1000));
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax(), nds1);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getType(), positionType);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getScale(), Boolean.FALSE);

        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct());
        assertNotNull(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta());
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta().getName(), goodName);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta().getPrice(), Integer.valueOf(1000));
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta().getTax(), nds2);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta().getType(), positionType);
        assertEquals(hubRequest.getData().getPurchases()[0].getPositions()[1].getProduct().getMeta().getScale(), Boolean.FALSE);

        assertNotNull(hubRequest.getData().getPurchases()[0].getSumWithouDiscounts(), "2000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getTotalSum(), "2000");
        assertNotNull(hubRequest.getData().getPurchases()[0].getType(), "SALE");
        assertNotNull(hubRequest.getData().getPurchases()[0].getRemId(), "54651022bffebc03098b4561");
    }

    private HubRequest getPurchaseWithServiceGood() {
        ArrayList<Positions> positions = new ArrayList<>();

        positions.add(Positions.builder()
                .name("Товар")
                .type("SERVICE")
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
            tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskData(positions, total, CASH, "54651022bffebc03098b456" + i))
                    .taskType(taskType).build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private HubRequest getPurchasesWith30Positions() {
        ArrayList<Positions> positions = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            positions.add(Positions.builder()
                    .name("Товар" + i)
                    .type("COUNTABLE")
                    .quantity(1)
                    .price(1000)
                    .total(1000)
                    .tax("NDS_20")
                    .taxSum(0)
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[15];

        for (int i = 1; i < tasks.length + 1; i++) {
            tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskData(positions, total, CASH, "54651022bffebc03098b456" + i))
                    .taskType(taskType).build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();

    }

    private HubRequest getPurchasesWithOneInvalid() {
        ArrayList<Tags> invalidTags = new ArrayList<>();
        invalidTags.add(new Tags(1214, String.valueOf(600)));

        ArrayList<Positions> invalidPositions = new ArrayList<>();
        invalidPositions.add(Positions.builder()
                .name("Товар Невалидный")
                .type("COUNTABLE")
                .quantity(1)
                .price(1000)
                .total(1000)
                .tax("NDS_20")
                .taxSum(0)
                .tags(invalidTags)
                .build());

        ArrayList<Positions> positions = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            positions.add(Positions.builder()
                    .name("Товар" + i)
                    .type("COUNTABLE")
                    .quantity(1)
                    .price(1000)
                    .total(1000)
                    .tax("NDS_20")
                    .taxSum(0)
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[4];

        for (int i = 1; i < tasks.length + 1; i++) {
            if (i == 2) {
                tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                        .data(getPollTaskData(invalidPositions, total, CASH, "54651022bffebc03098b456" + i))
                        .taskType(taskType).build();
            } else {
                tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                        .data(getPollTaskData(positions, total, CASH, "54651022bffebc03098b456" + i))
                        .taskType(taskType).build();
            }
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();

    }

    @Step("Очистить лицензию")
    private void clearLicense() {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            manager.clearDreamkasKey();
            server.clearRequests();
            manager.rebootFiscat();
            steps.step().inputPassword();
            manager.iAmHub(true);
            manager.setPollTime(10);
        }

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.step().technicalZero();
            hubQueue.addResponse(WHO_AM_I,
                    HubRequest.builder().result("OK").data(HubData.builder().owner("x@x.com").build()).build());

            manager.iAmHub(true);
            manager.setPollTime(10);

            hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
            steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));

            includePrintExPurchase();
            steps.shift().openShift();
        }
    }

    @Step("Проверить теги в ФН")
    private void checkNdsTagsFromFn(int lastNumDoc, String value) {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            int lastFdNumNext = manager.getLastFdNum();
            assertEquals(lastNumDoc, lastFdNumNext);

            List<Tag> docFromFs = manager.getDocFromFs(lastFdNumNext);
            System.out.println();
            assertTrue(docFromFs.contains(Tag.builder().tag(1102).value(Double.valueOf(value)).build()),
                    "Тег 1202 отсутсвует, либо содержит неверное значение");
            assertTrue(docFromFs.contains(Tag.builder().tag(1106).value(Double.valueOf(value)).build()),
                    "Тег 1106 отсутсвует, либо содержит неверное значение");
        }
    }

    private void initCashierName() {
        switch (cashBox.getBoxType()) {
            case KASSA_F:
                cashierName = "Администратор";
                break;
            case PULSE_FA:
                cashierName = "Кабинет Дримкас";
                break;
            default:
                cashierName = "Админ";
        }
    }

}
