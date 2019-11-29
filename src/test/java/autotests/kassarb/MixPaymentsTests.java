package autotests.kassarb;

import application_manager.cashbox.KeyEnum;

import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MixPaymentsTests extends BaseTestClass {

    @BeforeClass
    public void setUp() {
        steps.shift().openShift();
    }

    @AfterClass
    public void after() {

    }

    @Test
    public void testMixPayments() {
        steps.payment().addPositions("1000", 1);

        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyUp);
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyDown);
        manager.sendCommands();
        manager.sleepPlease(2000);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        System.out.println();

        assertTrue(manager.getEventsContainer().isContainsLcdEvents("Сумма к оплате:", "         1000.00",
                "Получено:", "         1000,00"));

    }

}
