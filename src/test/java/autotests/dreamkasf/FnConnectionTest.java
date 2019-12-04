package autotests.dreamkasf;

import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class FnConnectionTest extends BaseTestClass {

    @BeforeClass
    public void before() {
        steps.shift().openShift();
    }

    @Test
    public void fnConnectionTest() {
        int expected = Integer.parseInt(manager.executeSshCommand("lsof -c fiscat | wc -l").get(0));
        manager.pressKey(KeyEnum.keyMenu);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 10; j++) {
                manager.pressKey(KeyEnum.key3);
                manager.pressKey(KeyEnum.keyCancel);
            }
            manager.sendCommands();
        }
        int actual = Integer.parseInt(manager.executeSshCommand("lsof -c fiscat | wc -l").get(0));
        assertTrue(expected + 5 > actual, "Соединений с ФН больше чем ожидалось");
    }

}
