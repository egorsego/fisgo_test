package steps.main;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import steps.wait.WaitSteps;

import static application_manager.api_manager.CashBoxType.KASSA_F;
import static application_manager.api_manager.CashBoxType.PULSE_FA;

@Log4j
public class MainStepsDKF extends AbstractMainSteps {

    public MainStepsDKF(Manager manager, CashBox cashBox, WaitSteps wait) {
        super(manager, cashBox, wait);
    }

    @Override
    @Step("Быстрое тех. обнуление, без использования пользовательского интерфейса")
    public void fastTechnicalZero() {
        manager.techZeroing();
    }

    @Override
    @Step("Ввод пароля")
    public void inputPassword() {
        if (cashBox.getBoxType().equals(PULSE_FA)) {
            return;
        }
        while (!manager.getEventsContainer().isContainsLcdEvents("Введите пароль")
                && !manager.getEventsContainer().isContainsLcdEvents("На кассе не",
                                                                                "установлен Ключ",
                                                                                "Позвоните",
                                                                                "8 800 551-46-65")) {
            manager.sleepPlease(1000);
        }

        if(manager.getEventsContainer().isContainsLcdEvents("На кассе не",
                                                                        "установлен Ключ",
                                                                        "Позвоните",
                                                                        "8 800 551-46-65")) {
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
        }

        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key4);
        manager.sendCommands();

        manager.getEventsContainer().clearLcdEvents();
        manager.sleepPlease(2000);
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
    @Step("Тех. обнуление")
    public void technicalZero() {
        log.info("START TECHNICAL ZEROING");
        manager.getEventsContainer().clearLcdEvents();
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key7);
        manager.sendCommands();

        wait.waitExpectedLcd("Будет очищено: ", "1.Настройки ККТ",
                "2.Пользователи", "3.БД чеков");

        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }

        if(manager.getEventsContainer().isContainsLcdEvents("Невозможно", "отключиться", "от кабинета.", "Продолжить?")){
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
        }

        wait.waitExpectedLcd("Замените ФН", "на новый.");

        manager.executeSshCommand("sync");
        manager.executeSshCommand("sync");
        manager.executeSshCommand("sync");

        manager.stop();
        manager.sleepPlease(4_000);
        manager.rebootFiscat();

        inputPassword();
        log.info("FINISH TECHNICAL ZEROING");
    }

    @Override
    @Step("Выключить звук на КФ")
    public void soundOffForKF() {
        if (cashBox.getBoxType().equals(KASSA_F)) {
            manager.pressKey(KeyEnum.keyMenu);
            manager.pressKey(KeyEnum.key7);
            manager.pressKey(KeyEnum.key2);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();

            while (manager.getLoaderStatus()) {
                manager.sleepPlease(1000);
            }
            manager.sleepPlease(1000);
        }
    }


}
