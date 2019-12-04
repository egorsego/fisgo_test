package autotests.general;

import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.SHIFT_TIMER;

public class ShiftsTest extends BaseTestClass {

    private String shiftTimer;
    private SoftAssert softly;

    @BeforeClass(alwaysRun = true)
    public void beforeShifts() {
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();
    }

    @Test
    public void testOpenCloseShift() {
        softly = new SoftAssert();

        steps.shift().openShift();

        //Проверка открытия смены в конфиге в поле SHIFT_TIMER
        shiftTimer = manager.getConfigFields(SHIFT_TIMER).get(SHIFT_TIMER);
        softly.assertEquals("1", shiftTimer, "Смена не открылась");

        steps.shift().closeShift();

        //Проверка открытия смены в конфиге в поле SHIFT_TIMER
        shiftTimer = manager.getConfigFields(SHIFT_TIMER).get(SHIFT_TIMER);
        softly.assertEquals("0", shiftTimer, "Смена не закрылась");
        softly.assertAll();
    }

    @AfterClass
    public void afterShifts() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }
}
