package steps.cabinet;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import io.qameta.allure.Step;
import steps.wait.WaitSteps;

import static application_manager.api_manager.CashBoxType.PULSE_FA;
import static application_manager.api_manager.events.json.data.lcdData.NotificationType.NOTIFICATION_LOADING;
import static application_manager.api_manager.events.json.data.lcdData.NotificationType.NOTIFICATION_OK;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class CabinetStepsKF extends AbstractCabinetSteps{

    public CabinetStepsKF(Manager manager, CashBox cashBox, WaitSteps wait) {
        super(manager, cashBox, wait);
    }

    @Step
    @Override
    public void connectToCabinet(String code) {
        manager.getEventsContainer().clearLcdEvents();
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key8);
        manager.pressKey(KeyEnum.key1);
        manager.sendCommands();
        manager.pressKey(code);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1300);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        manager.sleepPlease(5000);

        assertFalse(manager.getEventsContainer().isContainsLcdEvents("Отсутствует", "связь"));

        assertTrue(manager.getEventsContainer().isContainsLcdEvents("Касса", "успешно", "подключена"),
                "Не было сообщения \" Касса успешно подключена \"");
    }

    @Step("Подключиться к кабинету")
    @Override
    public void connectToCabinet() {
        if (cashBox.getBoxType().equals(PULSE_FA)) {
            return;
        }
        manager.getEventsContainer().clearLcdEvents();
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key8);
        manager.pressKey(KeyEnum.key1, 6);
        manager.sendCommands();

        wait.waitNotificationScreen(NOTIFICATION_LOADING, "Подключение", "к Кабинету", "Дримкас");
        wait.waitNotificationScreen(NOTIFICATION_OK, "Касса", "успешно", "подключена");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }
}
