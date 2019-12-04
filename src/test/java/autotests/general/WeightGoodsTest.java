package autotests.general;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.lcdData.*;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.goods.Goods;
import application_manager.api_manager.goods.GoodsCode;
import application_manager.api_manager.goods.GoodsQueryBuilder;
import application_manager.api_manager.json.response.data.GoodsResponse;
import application_manager.api_manager.json.response.data.Position;
import application_manager.api_manager.json.response.data.PositionResponse;
import autotests.BaseTestClass;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WeightGoodsTest extends BaseTestClass {

    private String weightType;

    @BeforeClass
    public void before() {
        initWeightType();
        prepareGoodsDbForTest();
        manager.iAmHub(true);
        steps.cab().connectToCabinet();
        steps.shift().openShift();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void testWeightGood13() {
        steps.step().goToFreeMode();
        manager.clickScanner("2599999010001");
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(5000);
        }

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            Event event = Event.builder()
                    .type(EventType.LCD)
                    .data(EventData.builder()
                            .lcdData(LcdData.builder()
                                    .display(DisplayType.DISPLAY_CASHIER)
                                    .inputScreen(InputScreen.builder()
                                            .bottomLabel(LabelInfo.builder()
                                                    .center1("Установить вес \"Отмена\" \\ \"Ввод\"")
                                                    .build())
                                            .inputValue("")
                                            .middleLabel(LabelInfo.builder()
                                                    .left("Введите вес товара:")
                                                    .build())
                                            .topLabel(LabelInfo.builder()
                                                    .left("weight13        введите вес, кг.")
                                                    .build())
                                            .type(InputScreenType.INPUT_VALUE)
                                            .build())
                                    .build())
                            .build())
                    .build();

            steps.expectation().waitExpectedEvent(event);
        } else {
            steps.expectation().waitExpectedLcd("weight13        ", "Введите вес, кг.");
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("weight13        ", "Введите вес, кг."));
        }

        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents(
                    "1.weight13", "Вес:1кг", "          789.00", "     Итог:789.00"));
        }
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {

            ArrayList<SalesListItem> items = new ArrayList<>();
            items.add(SalesListItem.builder()
                    .count(1000)
                    .description("")
                    .price("789,00")
                    .name("1.weight13")
                    .build());

            steps.expectation().waitSalesListScreen(1, "789,00", items);
        }
        //assertEquals(manager.getPositions(1), getExpectedPositionWeightGood13());
        steps.payment().completePurchase();
    }

    @Test
    public void testWeightGood5() {
        steps.step().goToFreeMode();
        manager.clickScanner("2555555010007");
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(5000);

            ArrayList<SalesListItem> items = new ArrayList<>();
            items.add(SalesListItem.builder()
                    .count(1000)
                    .description("")
                    .price("1 000,00")
                    .name("1.weight5")
                    .build());

            steps.expectation().waitSalesListScreen(1, "1 000,00", items);
        }
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents(
                    "1.weight5", "Вес:1кг", "         1000.00", "    Итог:1000.00"));
        }
        //  assertEquals(manager.getPositions(1), getExpectedPositionWeightGood5());
        steps.payment().completePurchase();
    }

    @Test
    public void testPieceGood13() {
        steps.step().goToFreeMode();
        manager.clickScanner("2599999010100");
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(5000);

            ArrayList<SalesListItem> items = new ArrayList<>();
            items.add(SalesListItem.builder()
                    .count(1)
                    .description("")
                    .price("123,00")
                    .name("1.piece13")
                    .build());

            steps.expectation().waitSalesListScreen(1, "123,00", items);
        }
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("1.piece13", "          123.00", "     Итог:123.00"));
        }
        //assertEquals(manager.getPositions(1), getExpectedPositionPieceGood5());
        steps.payment().completePurchase();
    }

    @Test
    public void testPieceGood5() {
        steps.step().goToFreeMode();
        manager.clickScanner("2522222010004");
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(5000);

            ArrayList<SalesListItem> items = new ArrayList<>();
            items.add(SalesListItem.builder()
                    .count(1)
                    .description("")
                    .price("148,00")
                    .name("1.piece5")
                    .build());

            steps.expectation().waitSalesListScreen(1, "148,00", items);
        }
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("1.piece5", "          148.00", "     Итог:148.00"));
        }
        //assertEquals(manager.getPositions(1), getExpectedPositionPieceGood13());
        steps.payment().completePurchase();
    }

    @Test
    public void testFailWeightGoods() {
        steps.step().goToFreeMode();
        manager.clickScanner("2855555100005");
        //касса висит после пробития штрихкода, поэтому слип
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(5000);
        }

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents(
                    "Товар не найден!", "Добавить из", "облака?", "[Отмена]/[Ввод]"));
        } else {
            steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Товар не найден!",
                    "Добавить из", "облака?", "[Отмена]/[Ввод]");
        }
        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();
    }

    //__________________________________________________________________________________________________________________
    //___________________________________________EXPECTED POSITION______________________________________________________

    private Position getExpectedPositionWeightGood13() {
        GoodsResponse goodsResponse = GoodsResponse.builder()
                .agentsInfo("").barcode("2599999010001").unit_name("0").directoryCode("").goodsGroupCode("")
                .idGoodsGroup("").goodsCode("").remId("NULL").measure("").depName("NULL").unitPrice(78900).attributes(0)
                .precision(0).taxNumber(4).goodsType(Integer.valueOf(weightType)).article("NULL").goodsName("weight13")
                .build();

        PositionResponse positionResponse = PositionResponse.builder()
                .pos_num("1").clc_full_prepayment("false").clc_full_credit("false").clc_credit("false")
                .clc_pl_agent("false").prepayment("0").credit("0").quantity("1000").posCost(78900).totalPosSum(78900)
                .posSumDisplay(78900).mode("1").mode_value("2599999010001").tax_sum(7173)
                .build();

        return Position.builder().goodsResponse(goodsResponse).positionResponse(positionResponse).build();
    }

    private Position getExpectedPositionWeightGood5() {
        GoodsResponse goodsResponse = GoodsResponse.builder()
                .agentsInfo("").barcode("55555").unit_name("0").directoryCode("").goodsGroupCode("").idGoodsGroup("")
                .goodsCode("").remId("NULL").measure("").depName("NULL").unitPrice(100000).attributes(0).precision(0)
                .taxNumber(6).goodsType(Integer.valueOf(weightType)).article("NULL").goodsName("weight5")
                .build();

        PositionResponse positionResponse = PositionResponse.builder()
                .pos_num("1").clc_full_prepayment("false").clc_full_credit("false").clc_credit("false")
                .clc_pl_agent("false").prepayment("0").credit("0").quantity("1000").posCost(100000).totalPosSum(100000)
                .posSumDisplay(100000).mode("1").mode_value("55555").tax_sum(100000)
                .build();

        return Position.builder().goodsResponse(goodsResponse).positionResponse(positionResponse).build();
    }

    private Position getExpectedPositionPieceGood13() {
        GoodsResponse goodsResponse = GoodsResponse.builder()
                .agentsInfo("").barcode("22222").unit_name("1").directoryCode("").goodsGroupCode("").idGoodsGroup("")
                .goodsCode("").remId("NULL").measure("").depName("NULL").unitPrice(14800).attributes(0).precision(0)
                .taxNumber(3).goodsType(1).article("NULL").goodsName("piece5")
                .build();

        PositionResponse positionResponse = PositionResponse.builder()
                .pos_num("1").clc_full_prepayment("false").clc_full_credit("false").clc_credit("false")
                .clc_pl_agent("false").prepayment("0").credit("0").quantity("1").posCost(14800).totalPosSum(14800)
                .posSumDisplay(14800).mode("1").mode_value("22222").tax_sum(2467)
                .build();

        return Position.builder().goodsResponse(goodsResponse).positionResponse(positionResponse).build();
    }

    private Position getExpectedPositionPieceGood5() {
//        String remId = null;
//        if(cashBox.getBoxType().equals(CashBoxType.KASSA_F)){
//            remId = "NULL";
//        }

        GoodsResponse goodsResponse = GoodsResponse.builder()
                .agentsInfo("").barcode("2599999010100").unit_name("0").directoryCode("").goodsGroupCode("")
                .idGoodsGroup("").goodsCode("").remId("NULL").measure("").depName("NULL").unitPrice(12300)
                .attributes(0).precision(0).taxNumber(2).goodsType(1).article("NULL").goodsName("piece13")
                .build();

        PositionResponse positionResponse = PositionResponse.builder()
                .pos_num("1").clc_full_prepayment("false").clc_full_credit("false").clc_credit("false")
                .clc_pl_agent("false").prepayment("0").credit("0").quantity("1").posCost(12300).totalPosSum(12300)
                .posSumDisplay(12300).mode("1").mode_value("2599999010100").tax_sum(1118)
                .build();

        return Position.builder().goodsResponse(goodsResponse).positionResponse(positionResponse).build();
    }

    private void initWeightType() {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            weightType = "16";
        } else {
            weightType = "64";
        }
    }

    //__________________________________________________________________________________________________________________
    //__________________________________________________________________________________________________________________

    @Step("Подготовить базу товаров к тесту")
    private void prepareGoodsDbForTest() {


        String DELETE_GOODS = "echo \"attach '/FisGo/goodsDb.db' as GOODS; delete from goods.GOODS;\" " +
                "| sqlite3 /FisGo/goodsDb.db";
        String DELETE_GOODS_CODE = "echo \"attach '/FisGo/goodsDb.db' as GOODS_CODE;" +
                " delete from goods_code.GOODS_CODE;\" " +
                "| sqlite3 /FisGo/goodsDb.db";

        String insertWeightGood5 = GoodsQueryBuilder.getQueryInsertGood(Goods.builder()
                .ID("10375").PRECISION("0").MEASURE("кг").NAME("weight5").BARCODE("55555").GOODS_TYPE(weightType)
                .PRICE("100000").NDS("6").ATTRIBUTES("0")
                .build());

        String insertWeightGoodCode5 = GoodsQueryBuilder.getQueryInsertGoodCode(GoodsCode.builder()
                .GOODS_ID("10375").HASH_VAL("2704339741").TYPE("1").build());

        String insertWeightGood13 = GoodsQueryBuilder.getQueryInsertGood(Goods.builder()
                .ID("10078").PRECISION("0").MEASURE("кг").NAME("weight13").BARCODE("2599999010001").GOODS_TYPE(weightType)
                .PRICE("78900").NDS("4").ATTRIBUTES("0")
                .build());

        String insertWeightGoodCode13 = GoodsQueryBuilder.getQueryInsertGoodCode(GoodsCode.builder()
                .GOODS_ID("10078").HASH_VAL("4116094048").TYPE("1").build());

        String insertPieceGood13 = GoodsQueryBuilder.getQueryInsertGood(Goods.builder()
                .ID("10076").PRECISION("0").MEASURE("шт").NAME("piece13").BARCODE("2599999010100").GOODS_TYPE("1")
                .PRICE("12300").NDS("2").ATTRIBUTES("0")
                .build());

        String insertPieceGoodCode13 = GoodsQueryBuilder.getQueryInsertGoodCode(GoodsCode.builder()
                .GOODS_ID("10076").HASH_VAL("2933956111").TYPE("1").build());

        String insertPieceGood5 = GoodsQueryBuilder.getQueryInsertGood(Goods.builder()
                .ID("10079").PRECISION("1").MEASURE("шт").NAME("piece5").BARCODE("22222").GOODS_TYPE("1")
                .PRICE("14800").NDS("3").ATTRIBUTES("0")
                .build());

        String insertPieceGoodCode5 = GoodsQueryBuilder.getQueryInsertGoodCode(GoodsCode.builder()
                .GOODS_ID("10079").HASH_VAL("1433442775").TYPE("1").build());

        //удалить все товары из базы
        manager.executeSshCommand(DELETE_GOODS);
        manager.executeSshCommand(DELETE_GOODS_CODE);

        //добавление весового товара (баркод - 5)
        manager.executeSshCommand(insertWeightGood5);
        manager.executeSshCommand(insertWeightGoodCode5);

        //добавление весового товара (баркод - 13)
        manager.executeSshCommand(insertWeightGood13);
        manager.executeSshCommand(insertWeightGoodCode13);

        //добавление штучного товара (баркод - 5)
        manager.executeSshCommand(insertPieceGood5);
        manager.executeSshCommand(insertPieceGoodCode5);

        //добавление штучного товара (баркод - 13)
        manager.executeSshCommand(insertPieceGood13);
        manager.executeSshCommand(insertPieceGoodCode13);
    }
}
