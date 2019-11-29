package autotests.kassaf;

import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.api_manager.events.json.data.lcdData.SalesListItem;
import application_manager.api_manager.goods.Goods;
import application_manager.api_manager.goods.GoodsCode;
import application_manager.api_manager.goods.GoodsQueryBuilder;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.json.purchase.Positions;
import hub_emulator.response.enums.MethodsEnum;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class ScannerTest extends BaseTestClass {

    private String nameGood = "Parliament";
    private String barcode = "4606203086627";
    private String dataMatrixCode = "04606203086627c4aO<OTAC68VZ6b";

    private List<SalesListItem> listItems;

    @BeforeClass
    public void beforeClass() {
        listItems = getSalesListItems();
        prepareGoodsDb();

        manager.iAmHub(true);
        manager.setPollTime(20);

        steps.cab().connectToCabinet();
        steps.shift().openShift();
        steps.step().goToFreeMode();
    }

    @AfterClass
    public void afterClass() {
        manager.pressKey(KeyEnum.keyCancel, 2);
        manager.sendCommands();
        manager.iAmHub(false);
    }

    @Test
    public void addCigaretteTestIf18() {
        clear();

        manager.clickScanner(dataMatrixCode);

        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Проверьте", "паспорт",
                "покупателя:есть", "ли ему 18 лет");
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitSalesListScreen(1, "2 220,00", listItems);
        steps.payment().completePurchase();
        checkPurchaseDocumentReport();

        clear();

        manager.pressKey(barcode);
        manager.pressKey(KeyEnum.keyGoods);
        manager.sendCommands();

        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Проверьте", "паспорт",
                "покупателя:есть", "ли ему 18 лет");
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitSalesListScreen(1, "2 220,00", listItems);
        steps.payment().completePurchase();
        checkPurchaseDocumentReport();
    }

    @Test
    public void scanBarcodeInsteadDataMatrix() {
        clear();
        manager.clickScanner(barcode);
        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Отсканируйте код",
                "Data Matrix на", "пачке", " ");
        clear();
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_ENTER_SUM);
    }

    @Test
    public void addCigaretteTwice() {
        clear();
        manager.clickScanner(dataMatrixCode);

        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Проверьте", "паспорт",
                "покупателя:есть", "ли ему 18 лет");
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitSalesListScreen(1, "2 220,00", listItems);

        manager.clickScanner(dataMatrixCode);
        steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Данный код",
                "маркировки", "уже", "отсканирован!");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.payment().completePurchase();
        checkPurchaseDocumentReport();
    }

    //__________________________________________________________________________________________________________________
    //                                               STEPS
    //__________________________________________________________________________________________________________________

    private void clear() {
        manager.getEventsContainer().clearLcdEvents();
        server.clearRequests();
    }

    private List<SalesListItem> getSalesListItems() {
        List<SalesListItem> listItems = new ArrayList<>();
        listItems.add(SalesListItem.builder().count(1)
                .description("")
                .name("1." + nameGood)
                .price("2 220,00")
                .build());
        return listItems;
    }

    private void prepareGoodsDb() {
        manager.executeSshCommand(GoodsQueryBuilder.getDeleteGoodsTable());
        manager.executeSshCommand(GoodsQueryBuilder.getDeleteGoodsCodeTable());

        manager.executeSshCommand(GoodsQueryBuilder.getQueryInsertGood(Goods.builder().ID("9978").PRECISION("1")
                .MEASURE("Сигареты").NAME(nameGood).BARCODE(barcode).GOODS_TYPE("32").PRICE("222000").NDS("6")
                .ATTRIBUTES("0").build()));
        manager.executeSshCommand(GoodsQueryBuilder.getQueryInsertGoodCode(GoodsCode.builder()
                .GOODS_ID("9978").HASH_VAL("2086856535").TYPE("1").build()));
    }


    private void checkPurchaseDocumentReport() {
        assertTrue(server.checkReceivedRequest(MethodsEnum.PURCHASE_DOCUMENT_REPORT, 1));
        HubRequest lastRequest = server.getLastRequest(MethodsEnum.PURCHASE_DOCUMENT_REPORT);
        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData());
        assertNotNull(lastRequest.getData().getPurchases());
        assertEquals(lastRequest.getData().getPurchases().length, 1);
        assertNotNull(lastRequest.getData().getPurchases()[0]);
        Positions[] positions = lastRequest.getData().getPurchases()[0].getPositions();
        assertNotNull(positions);
        assertEquals(positions.length, 1);
        assertNotNull(positions[0]);
        assertNotNull(positions[0].getExciseBarcode());
        assertEquals(positions[0].getExciseBarcode(), dataMatrixCode);
        assertNotNull(positions[0].getProduct());
        assertNotNull(positions[0].getProduct().getMeta());
        assertNotNull(positions[0].getProduct().getMeta().getType());
        assertEquals(positions[0].getProduct().getMeta().getType(), "TOBACCO");

    }

}
