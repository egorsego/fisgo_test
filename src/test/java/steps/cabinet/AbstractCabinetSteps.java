package steps.cabinet;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import application_manager.cashbox.Keyboard;
import io.qameta.allure.Step;
import steps.wait.WaitSteps;

public abstract class AbstractCabinetSteps implements CabinetSteps {

    Manager manager;
    CashBox cashBox;
    Keyboard keyboard;
    WaitSteps wait;

    public AbstractCabinetSteps(Manager manager, CashBox cashBox, WaitSteps wait) {
        this.manager = manager;
        this.cashBox = cashBox;
        this.keyboard = cashBox.getKeyboard();
        this.wait = wait;
    }

    @Step
    @Override
    public void disconnectCabinet() {
        manager.getEventsContainer().clearLcdEvents();
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key8);
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();

        wait.waitExpectedLcd("Отключить", "кассу от");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        wait.waitExpectedLcd("Касса", "успешно", "отключена");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

}
