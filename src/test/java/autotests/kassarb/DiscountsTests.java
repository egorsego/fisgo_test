package autotests.kassarb;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.json.request.data.enums.LeafEnum;
import application_manager.api_manager.json.response.data.Discount;
import application_manager.api_manager.json.response.data.PositionResponse;
import application_manager.api_manager.json.response.data.Purchase;
import autotests.BaseTestClass;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class DiscountsTests extends BaseTestClass {

    private static final String DELETE_DISCOUNTS = "echo \"attach '/FisGo/goodsDb.db' as DISCOUNTS;" +
            " delete from discounts.DISCOUNTS;\" | sqlite3 /FisGo/goodsDb.db";
    private static final String DELETE_GOODS = "echo \"attach '/FisGo/goodsDb.db' as GOODS; delete from goods.GOODS;\" " +
            "| sqlite3 /FisGo/goodsDb.db";
    private static final String DELETE_GOODS_CODE = "echo \"attach '/FisGo/goodsDb.db' as GOODS_CODE;" +
            " delete from goods_code.GOODS_CODE;\" " +
            "| sqlite3 /FisGo/goodsDb.db";
    private static final String INSERT_GOOD = "echo \"attach '/FisGo/goodsDb.db' as goods;" +
            " insert into goods.GOODS " +
            "(ID, PRECISION, MEASURE, NAME, ARTICUL, GOODS_TYPE, PRICE, NDS, ATTRIBUTES, HASH_REM_ID, NDS_MANUAL)" +
            " values (5 , 0, 'qwe', 'qwe', 111333, 16, 100000, 5, 0, 3938726230, 0);\" " +
            "| sqlite3 /FisGo/goodsDb.db";
    private static final String INSERT_GOOD_CODE = "echo \"attach '/FisGo/goodsDb.db' as goods;" +
            " insert into goods.GOODS_CODE (GOODS_ID, HASH_VAL, TYPE) values (5, 424452379, 2);\" " +
            "| sqlite3 /FisGo/goodsDb.db";

    @BeforeClass(alwaysRun = true)
    public void beforeDiscounts() {
        System.out.println("Настройка предварительных условий для тестов скидок");
        manager.executeSshCommand(DELETE_DISCOUNTS);
        manager.executeSshCommand(DELETE_GOODS);
        manager.executeSshCommand(DELETE_GOODS_CODE);
        manager.executeSshCommand(INSERT_GOOD);
        manager.executeSshCommand(INSERT_GOOD_CODE);
        System.out.println();
    }

    @AfterClass(alwaysRun = true)
    public void afterDiscounts() {
        System.out.println("Возврат к первоначальным условиям после тестов скидок");
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }

    /**
     * Тест на наличие нужных айтемов в дереве меню.
     */
    @Test
    public void testMenu() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key4);
        manager.sendCommands();

        ArrayList<String> childs = manager.getLeaf(LeafEnum.ALL).getChilds();
        assertTrue(childs.contains("9.Скидки"));

        manager.pressKey(KeyEnum.key9);
        manager.sendCommands();
        childs = manager.getLeaf(LeafEnum.ALL).getChilds();
        assertTrue(childs.contains("1.Добавить"));
    }

    /**
     * Тест на добавление скидки
     */
    @Test(dependsOnMethods = {"testMenu"})
    public void testAddDiscount() {
        goToMenuDiscount();

        addDiscount(10);
        addDiscount(11);

        //запрос с кассы всех скидок и проверки
        Discount discount1 = Discount.builder().mode("MANUAL").name("10%").type("PER").value(10.0).build();
        Discount discount2 = Discount.builder().mode("MANUAL").name("11%").type("PER").value(11.0).build();
        List<Discount> discounts = manager.getDiscounts();
        assertTrue(discounts.contains(discount1), "Скидка 10% не добавилась");
        assertTrue(discounts.contains(discount2), "Скидка 11% не добавилась");

        deleteLastDiscount();
        discounts = manager.getDiscounts();
        assertFalse(discounts.contains(discount2));

        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();
    }

    /**
     * Тест на добавление скидок в чек
     */
    @Test(dependsOnMethods = {"testAddDiscount"})
    public void testAddDiscountOnCheque() {
        addPosition(100);
        addAbsoluteDiscountOnPosition(11);
        addWeightGoods("111333", 2);
        addPercentDiscountOnPosition();
        addDiscountOnPurchase();
        punchCheque();

        Purchase receipt = manager.getLastPurchase();
        assertNotNull(receipt, "Чек не был пробит. getLastPurchase вернул null");
        assertEquals("10%", receipt.getDiscountName());
        assertEquals(new Integer(170010), receipt.getTotalSumDisplay());
        assertEquals(new Integer(170010), receipt.getTotalSumReceipt());

        PositionResponse[] positions = receipt.getPositions();
        assertNotNull(positions, "В чеке отсутствуют позиции");
        assertEquals(2, positions.length, "Неверное кол-во позиций в чеке");
        assertEquals(new Integer(1100), positions[0].getDiscountSum());
        assertEquals("Ф", positions[0].getDiscountName());
        assertEquals(new Integer(10000), positions[0].getPosCost());
        assertEquals(new Integer(10000), positions[0].getPosSumDisplay());
        assertEquals(new Integer(8900), positions[0].getTotalPosSum());

        assertEquals("10%", positions[1].getDiscountName());
        assertEquals(new Integer(100000), positions[1].getPosCost());
        assertEquals(new Integer(100000), positions[1].getPosSumDisplay());
        assertEquals(new Integer(200000), positions[1].getTotalPosSum());
    }



    //------------------------------------------------STEPS-------------------------------------------------------------
    /**
     * Переход в меню скидок
     */
    @Step
    private void goToMenuDiscount() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key9);
        manager.sendCommands();
    }

    /**
     * Удаление последней скидки
     */
    private void deleteLastDiscount() {
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyUp);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавление скидки
     *
     * @param discountValue - значение скидки в процентах
     */
    private void addDiscount(int discountValue) {
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(String.valueOf(discountValue));
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавление скидки на чек
     */
    private void addDiscountOnPurchase() {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.keyDown);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyDown);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавление позиции
     *
     * @param value - сумма товара
     */
    private void addPosition(int value) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.keyCancel);
        manager.pressKey(String.valueOf(value));
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавление процентной скидки на позицию
     */
    private void addPercentDiscountOnPosition() {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyDown);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавление абсолютной скидки на позицию
     *
     * @param value - значение скидки
     */
    private void addAbsoluteDiscountOnPosition(int value) {
        manager.holdKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(String.valueOf(value));
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    /**
     * Добавить весовой товар
     *
     * @param article - артикул товара
     * @param weight  - вес товара
     */
    private void addWeightGoods(String article, int weight) {
        manager.pressKey(KeyEnum.keyGoods);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.pressKey(article);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(String.valueOf(weight));
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step
    private void punchCheque() {
        manager.pressKey(KeyEnum.keyEnter, 3);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(2000);
        }
        manager.pressKey(KeyEnum.keyEnter, 1);
        manager.sendCommands();
    }

}
