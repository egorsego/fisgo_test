package steps.shift;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import application_manager.cashbox.Keyboard;
import io.qameta.allure.Step;
import steps.main.MainSteps;

import static application_manager.api_manager.events.enums.EventType.LCD;

public abstract class AbstractShiftSteps implements ShiftSteps {

    Manager manager;
    CashBox cashBox;
    Keyboard keyboard;
    MainSteps mainSteps;

    public AbstractShiftSteps(Manager manager, CashBox cashBox, MainSteps mainSteps) {
        this.manager = manager;
        this.cashBox = cashBox;
        this.keyboard = cashBox.getKeyboard();
        this.mainSteps = mainSteps;
    }

    @Override
    @Step("Открытие смены")
    public void openShift() {
        if (manager.getConfigFields(ConfigFieldsEnum.SHIFT_TIMER).get(ConfigFieldsEnum.SHIFT_TIMER).equals("0")) {
            manager.pressKey(KeyEnum.keyMenu, 2);
            manager.pressKey(KeyEnum.key1, 2);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();

            int countLcdEvents = manager.getEventsContainer().getEventsMap().get(LCD).size();
            if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
                while (countLcdEvents >= manager.getEventsContainer().getEventsMap().get(LCD).size()) {
                    manager.sleepPlease(100);
                }
            }

            manager.sleepPlease(3000);
            while (manager.getLoaderStatus()) {
                manager.sleepPlease(1000);
            }
        } else {
            mainSteps.goToFreeMode();
        }
    }

    @Override
    @Step("Закрытие смены")
    public void closeShift() {
        if (manager.getConfigFields(ConfigFieldsEnum.SHIFT_TIMER).get(ConfigFieldsEnum.SHIFT_TIMER).equals("1")) {
            manager.pressKey(KeyEnum.keyMenu, 2);
            manager.pressKey(KeyEnum.key1);
            manager.pressKey(KeyEnum.key2);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
            while (manager.getLoaderStatus()) {
                manager.sleepPlease(2000);
            }
            manager.pressKey(KeyEnum.keyMenu);
            manager.sendCommands();
        }
    }

}
