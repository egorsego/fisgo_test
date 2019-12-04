package autotests.kassaf;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.json.response.data.*;
import autotests.BaseTestClass;
import hub_emulator.Server;
import org.testng.annotations.*;

import static org.testng.Assert.assertEquals;

public class SalesTest extends BaseTestClass {

    @BeforeClass(alwaysRun = true)
    public void beforeSales() {
        server = new Server();
        manager.setPollTime(10);
        manager.iAmHub(true);
        steps.cab().connectToCabinet();
        steps.shift().openShift();
    }

    @Test
    public void saleInCashOnePosition() {
        steps.payment().addPositions("100", 1);
        addWeightGoodsOnPosition();
        Position expectedPosition = getExpectedPosition();
        Position positions = manager.getPositions(1);
        assertEquals(manager.getPositions(1), getExpectedPosition());
        Position weightPositions = manager.getPositions(2);
        System.out.println();
    }

    @AfterClass(alwaysRun = true)
    public void afterSales() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }


    //-----------------------------------------------STEPS--------------------------------------------------------------

    private void addWeightGoodsOnPosition() {
        manager.clickScanner("2855555100006");

    }

    private Position getExpectedPosition() {
        GoodsResponse goodsResponse = GoodsResponse.builder()
                .agentsInfo("").barcode("").unit_name("").directoryCode("").goodsGroupCode("").idGoodsGroup("")
                .goodsCode("").remId("").measure("").depName("").unitPrice(10000).attributes(0).precision(0)
                .taxNumber(6).goodsType(0).article("").goodsName("Товар")
                .build();

        PositionResponse positionResponse = PositionResponse.builder()
                .pos_num("1").clc_full_prepayment("false").clc_full_credit("false").clc_credit("false")
                .clc_pl_agent("false").prepayment("0").credit("0").quantity("1").posCost(10000).totalPosSum(10000)
                .posSumDisplay(10000).mode("0").mode_value("").tax_sum(10000)
                .build();

        return Position.builder().goodsResponse(goodsResponse).positionResponse(positionResponse).build();
    }

}
