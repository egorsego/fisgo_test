package autotests.dreamkasf;

import application_manager.api_manager.json.response.data.Position;
import application_manager.api_manager.json.response.data.Tag;
import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static org.testng.Assert.*;

public class TobaccoSaleTest extends BaseTestClass {

    private final static String DATA_MATRIX_CODE = "04606203086627c4aO<OTAC68VZ6b";
    private final static String BARCODE = "4606203086627";

    @BeforeClass
    public void beforeClass() {
        regNum = manager.getRegNumKKT();
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        addTobaccoGoodsFromCab();
        steps.shift().openShift();
    }

    @AfterClass
    public void afterClass() {
        manager.iAmHub(false);
    }

    @Test
    public void testNotificationIfNot18Years() {
        manager.clickScanner(DATA_MATRIX_CODE);

        steps.expectation().waitExpectedLcd("Проверьте", "паспорт", "покупателя:есть", "ли ему 18 лет");

        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();

        Position position = manager.getPositions(1);
        assertNull(position.getPositionResponse());
        assertNull(position.getGoodsResponse());
    }

    @Test
    public void testNotificationIf18Years() {
        server.clearRequests();
        int lastFdNum = manager.getLastFdNum();

        manager.clickScanner(DATA_MATRIX_CODE);

        steps.expectation().waitExpectedLcd("Проверьте", "паспорт", "покупателя:есть", "ли ему 18 лет");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        checkPosition();
        steps.payment().completePurchase();

        checkPurchaseDocumentReport();
        checkPrintBuffer();
        assertTrue(checkFdFromFn(lastFdNum));
    }

    @Test
    public void testWarningIfAlreadyAdded() {
        manager.clickScanner(DATA_MATRIX_CODE);

        steps.expectation().waitExpectedLcd("Проверьте", "паспорт", "покупателя:есть", "ли ему 18 лет");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        checkPosition();

        manager.clickScanner(DATA_MATRIX_CODE);
        steps.expectation().waitExpectedLcd("Данный код", "маркировки", "уже", "отсканирован!");
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        //проверка что позиция не добавилась
        assertEquals(manager.getCountPositions(), 1);

        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
    }

    @Test
    public void testScanBarcodeIfCancel() {
        manager.clickScanner(BARCODE);
        steps.expectation().waitExpectedLcd("Считан линейный", "штрих-код,", "а не Data Matrix", "[Отмена]/[Ввод]");

        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();

        //проверка что позиция не добавилась
        assertEquals(manager.getCountPositions(), 0);
    }

    @Test
    public void testScanBarcodeIfEnter() {
        int lastFdNum = manager.getLastFdNum();
        manager.clickScanner(BARCODE);
        steps.expectation().waitExpectedLcd("Считан линейный", "штрих-код,", "а не Data Matrix", "[Отмена]/[Ввод]");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        //проверка что позиция не добавилась
        assertEquals(manager.getCountPositions(), 1);

        checkPosition();

        steps.payment().completePurchase();

        assertFalse(checkFdFromFn(lastFdNum));
    }


    //__________________________________________________________________________________________________________________

    @Step("Проверка данных о позиции")
    private void checkPosition() {
        Position position = manager.getPositions(1);
        assertNotNull(position);

        //________________________________GOODS_RESPONSE________________________________________________________________
        assertNotNull(position.getGoodsResponse());
        assertEquals(position.getGoodsResponse().getAttributes(), 0);
        assertNotNull(position.getGoodsResponse().getBarcode());
        assertEquals(position.getGoodsResponse().getBarcode(), "4606203086627");
        assertNotNull(position.getGoodsResponse().getGoodsName());
        assertEquals(position.getGoodsResponse().getGoodsName(), "KENT");
        assertEquals(position.getGoodsResponse().getGoodsType(), 32);
        assertEquals(position.getGoodsResponse().getPrecision(), 0);
        assertNotNull(position.getGoodsResponse().getRemId());
        assertEquals(position.getGoodsResponse().getRemId(), "a6d275db-a7f5-4d0c-93fa-ee657df63ad2");
        assertEquals(position.getGoodsResponse().getTaxNumber(), 5);
        assertNotNull(position.getGoodsResponse().getUnit_name());
        assertEquals(position.getGoodsResponse().getUnit_name(), "1");
        assertEquals(position.getGoodsResponse().getUnitPrice(), 10000);

        //_______________________________POSITIONS_RESPONSE_____________________________________________________________
        assertNotNull(position.getPositionResponse());
        assertNotNull(position.getPositionResponse().getClc_credit());
        assertEquals(position.getPositionResponse().getClc_credit(), "false");
        assertNotNull(position.getPositionResponse().getClc_full_credit());
        assertEquals(position.getPositionResponse().getClc_full_credit(), "false");
        assertNotNull(position.getPositionResponse().getClc_full_prepayment());
        assertEquals(position.getPositionResponse().getClc_full_prepayment(), "false");
        assertNotNull(position.getPositionResponse().getClc_pl_agent());
        assertEquals(position.getPositionResponse().getClc_pl_agent(), "false");
        assertNotNull(position.getPositionResponse().getCredit());
        assertEquals(position.getPositionResponse().getCredit(), "0");
        assertNotNull(position.getPositionResponse().getMode_value());
        assertEquals(position.getPositionResponse().getMode_value(), "4606203086627");
        assertNotNull(position.getPositionResponse().getPosCost());
        assertEquals(position.getPositionResponse().getPosCost(), Integer.valueOf(10000));
        assertNotNull(position.getPositionResponse().getPosSumDisplay());
        assertEquals(position.getPositionResponse().getPosSumDisplay(), Integer.valueOf(10000));
        assertNotNull(position.getPositionResponse().getQuantity());
        assertEquals(position.getPositionResponse().getQuantity(), "1");
        assertNotNull(position.getPositionResponse().getTax_sum());
        assertEquals(position.getPositionResponse().getTax_sum(), Integer.valueOf(10000));
        assertNotNull(position.getPositionResponse().getTotalPosSum());
        assertEquals(position.getPositionResponse().getTotalPosSum(), Integer.valueOf(10000));
        assertNotNull(position.getPositionResponse().getPrepayment());
        assertEquals(position.getPositionResponse().getPrepayment(), "0");
    }

    @Step("Проверка JSON - PURCHASE_DOCUMENT_REPORT")
    private void checkPurchaseDocumentReport() {
        server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1);
        HubRequest purchaseDocRep = server.getLastRequest(PURCHASE_DOCUMENT_REPORT);

        assertNotNull(purchaseDocRep);
        assertNotNull(purchaseDocRep.getUuid());
        assertNotNull(purchaseDocRep.getUrl());
        assertNotNull(purchaseDocRep.getData());
        assertNotNull(purchaseDocRep.getData().getPurchases());
        assertEquals(purchaseDocRep.getData().getPurchases().length, 1);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0]);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getCashier());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getDate());

        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getDiscountSum());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getDiscountSum(), "0");

        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPayments());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPayments().length, 1);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPayments()[0].getType());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPayments()[0].getType(), TypeResponseExPurchase.CASH);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPayments()[0].getSum());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPayments()[0].getSum(), Integer.valueOf(10000));

        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPositions().length, 1);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getTax());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getBarcode());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getBarcode(), "4606203086627");
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getExciseBarcode());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getExciseBarcode(), DATA_MATRIX_CODE);
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getType());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getType(), "TOBACCO");
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getPositions()[0].getProduct().getMeta().getTax(), "NDS_0");

        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getSumWithouDiscounts());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getSumWithouDiscounts(), "10000");
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getTotalSum());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getTotalSum(), "10000");
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getType());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getType(), "SALE");
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getNumberFn());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getNumberFd());
        assertNotNull(purchaseDocRep.getData().getPurchases()[0].getRegistryNumber());
        assertEquals(purchaseDocRep.getData().getPurchases()[0].getRegistryNumber(), regNum);
    }

    @Step("Добавить товар с признаком ТАБАК из кабинета")
    private void addTobaccoGoodsFromCab() {
        TaskResults[] taskResults = new TaskResults[1];
        taskResults[0] = TaskResults.builder()
                .taskId(1)
                .data(PollTaskData.builder()
                        .name("KENT")
                        .type("TOBACCO")
                        .price(10000)
                        .alcohol(false)
                        .scale(false)
                        .precision(1d)
                        .measure("шт")
                        .tax("NDS_0")
                        .barcodes(new ArrayList<String>() {{
                            add("4606203086627");
                        }})
                        .remId("a6d275db-a7f5-4d0c-93fa-ee657df63ad2")
                        .build())
                .taskType("upsert_product")
                .build();
        HubRequest tobaccoGood = HubRequest.builder()
                .data(HubData.builder()
                        .task(taskResults)
                        .build())
                .result("OK")
                .build();

        steps.hub().answerPoll(tobaccoGood);
        steps.hub().checkPollSuccessResults();
    }

    @Step("Проверка печатного буфера")
    private void checkPrintBuffer() {
        assertTrue(manager.getEventsContainer().isContainsPrintEvent("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"  Улица Пушкина дом колотушкrина\n"));
        assertTrue(manager.getEventsContainer().isContainsPrintEvent("КАССИР Админ\n" +
                "1.KENT\n" +
                "   100.00 * 1       =     100.00\n"));
        assertTrue(manager.getEventsContainer().isContainsPrintEvent(" ПОЛНЫЙ РАСЧЁТ     \n" +
                " C НДС 0%           =     100.00\n"));
        assertTrue(manager.getEventsContainer().isContainsPrintEvent("ИТОГ                =     100.00\n"));
        assertTrue(manager.getEventsContainer().isContainsPrintEvent("КТ:00050430771947236334614F3C4F54\n"));
        assertTrue(manager.getEventsContainer().isContainsPrintEvent("НАЛИЧНЫМИ           =     100.00\n" +
                "Получено            =     100.00\n" +
                "СУММА C НДС 0%      =     100.00\n" +
                "--------------------------------\n" +
                "      Спасибо за покупку!\n"));
    }

    @Step("Проверка документа из ФН")
    private boolean checkFdFromFn(int lastFdNum) {
        int lastFdNumNext = manager.getLastFdNum();
        assertTrue(lastFdNumNext - lastFdNum == 1);
        List<Tag> docFromFs = manager.getDocFromFs(lastFdNumNext);
        ArrayList<Double> kt = new ArrayList<>(
                Arrays.asList(0.0, 5.0, 4.0, 48.0, 119.0, 25.0, 71.0, 35.0, 99.0, 52.0, 97.0, 79.0, 60.0, 79.0, 84.0));
        return docFromFs.contains(Tag.builder().tag(1162).name("КТ").value(kt).build());
    }
}
