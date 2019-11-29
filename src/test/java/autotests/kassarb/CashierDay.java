package autotests.kassarb;

import application_manager.cashbox.CashBox;
import application_manager.cashbox.Keyboard;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.Manager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static application_manager.cashbox.KeyEnum.*;
import static application_manager.cashbox.KeyEnum.key5;
import static application_manager.cashbox.KeyEnum.keyEnter;

public class CashierDay {

    private static CashBox cashBox;
    private static Manager manager;
    private static Keyboard keyboard;

    @BeforeClass
    public static void setUp() {
        cashBox = new CashBox(CashBoxType.KASSA_F, "192.168.243.120");
        manager = new Manager(cashBox);
        manager.start();
        keyboard = cashBox.getKeyboard();
    }

    @AfterClass
    public void after() {
        manager.stop();
    }


    @Test
    public void cashierDay() {
        for (int i = 0; i < 50; i++) {
            //openShift();
            saleOnePosition();
            saleWithManyPositions(30);
            for (int j = 0; j < 10; j++) {
                saleWithManyPositions(j);
            }
            insertion();
            withdrawal();
            closeShift();
        }
    }

    private void insertion() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key5);

        manager.pressKey(key4);
        manager.pressKey(key4);
        manager.pressKey(key4);
        manager.pressKey(keyEnter, 2);
        manager.sendCommands();
        manager.sleepPlease(8000);
    }

    private void withdrawal() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key4);

        manager.pressKey(key4);
        manager.pressKey(key4);
        manager.pressKey(key4);
        manager.pressKey(keyEnter, 2);
        manager.sendCommands();
        manager.sleepPlease(8000);
    }

    private void openShift() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key1);
        manager.pressKey(keyEnter);
        manager.sendCommands();
        manager.sleepPlease(1000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);

        manager.sendCommands();
        manager.sleepPlease(1000);
    }

    private void closeShift() {
        manager.pressKey(keyMenu);
        manager.pressKey(key1);
        manager.pressKey(key2);
        manager.pressKey(keyEnter);
        manager.pressKey(keyCancel);
        manager.sendCommands();
        manager.sleepPlease(3000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
    }

    private void saleOnePosition() {
        manager.pressKey(key5, 2);
        manager.pressKey(keyEnter, 4);
        manager.sendCommands();
        manager.sleepPlease(2000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2000);
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

    private void saleWithManyPositions(int countPosition) {
        for (int i = 0; i < countPosition; i++) {
            manager.pressKey(key5);
            manager.pressKey(keyEnter);
        }
        manager.pressKey(keyEnter, 3);
        manager.sendCommands();
        manager.sleepPlease(2000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(2500);
        manager.pressKey(keyEnter);
        manager.sendCommands();
    }

}
