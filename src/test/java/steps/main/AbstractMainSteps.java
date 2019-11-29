package steps.main;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import steps.wait.WaitSteps;

import java.util.List;

import static application_manager.api_manager.CashBoxType.PULSE_FA;

public abstract class AbstractMainSteps implements MainSteps {

    Manager manager;
    CashBox cashBox;
    WaitSteps wait;

    public AbstractMainSteps(Manager manager, CashBox cashBox, WaitSteps wait) {
        this.manager = manager;
        this.cashBox = cashBox;
        this.wait = wait;
    }

    @Override
    @Step("Первоначальный ввод даты и времени")
    public void inputDataTime() {
        if (!cashBox.getBoxType().equals(PULSE_FA)) {
            wait.waitExpectedLcd("Часовой пояс:");
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
            wait.waitExpectedLcd("Введите дату", "и время");
            manager.pressKey(KeyEnum.keyEnter, 2);
            manager.sendCommands();
        }
    }

    @Override
    @Step("Печать X-отчета")
    public void printXreport() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.sleepPlease(7000);
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }

    @Override
    @Step("Подключить банковский терминал")
    public void connectBankTerminal() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Override
    @Step("Получить время на кассе +24 часа")
    public String getTimePlus24Hours() {
        List<Event> events = manager.getEventsContainer().getEventsMap().get(EventType.TIME);
        Event lstEventTime = events.get(events.size() - 1);
        Integer intTimeFromCashbox = Integer.valueOf(lstEventTime.getData().getTimeData().getDateUnix());
        intTimeFromCashbox += 24 * 60 * 60 + 60 * 15; //24 часа 15 минут
        return String.valueOf(intTimeFromCashbox);
    }

    @Override
    @Step("Проверка на валидность JSON")
    public boolean isJSONValid(String jsonInString) {
        Gson gson = new Gson();
        try {
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    @Override
    @Step("Перейти в экран свободной продажи")
    public void goToFreeMode() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();
        manager.sleepPlease(2000);
    }

}
