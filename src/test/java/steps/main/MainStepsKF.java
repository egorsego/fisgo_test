package steps.main;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.lcdData.*;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import application_manager.cashbox.Keyboard;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import steps.wait.WaitSteps;

import java.util.ArrayList;
import java.util.List;

import static application_manager.api_manager.CashBoxType.KASSA_BUS;
import static application_manager.api_manager.CashBoxType.PULSE_FA;

public class MainStepsKF extends AbstractMainSteps {


    public MainStepsKF(Manager manager, CashBox cashBox, WaitSteps wait) {
        super(manager, cashBox, wait);
    }

    @Override
    @Step("Ввод пароля")
    public void inputPassword() {

        //TODO как-нибудь убрать и исправить, сейчас не понятно как лучше
        if(cashBox.getBoxType().equals(PULSE_FA) || cashBox.getBoxType().equals(KASSA_BUS)){
            return;
        }

        wait.waitExpectedEvent(Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .inputScreen(InputScreen.builder()
                                        .inputValue("")
                                        .type(InputScreenType.INPUT_PASSWORD)
                                        .build())
                                .build())
                        .build())
                .build());

        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key4);
        manager.sendCommands();

        ArrayList<String> textArr = new ArrayList<>();
        textArr.add("Администратор ");

        wait.waitExpectedEvent(Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .notificationScreen(NotificationScreen.builder()
                                        .text(textArr)
                                        .type(NotificationType.NOTIFICATION_USER)
                                        .build())
                                .build())
                        .build())
                .build());

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        manager.getEventsContainer().clearLcdEvents();
        manager.sleepPlease(2000);
    }

    @Override
    @Step("Тех. обнуление")
    public void technicalZero() {
        manager.getEventsContainer().clearLcdEvents();
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key7);
        manager.sendCommands();

        ArrayList<ScrollScreenItem> items = new ArrayList<>();
        items.add(ScrollScreenItem.builder()
                .alignCenter(false)
                .footer(false)
                .header(true)
                .text("Что будет удалено: ")
                .build());
        items.add(ScrollScreenItem.builder()
                .alignCenter(false)
                .footer(false)
                .header(false)
                .text("1.Настройки ККТ")
                .build());
        items.add(ScrollScreenItem.builder()
                .alignCenter(false)
                .footer(false)
                .header(false)
                .text("2.Пользователи")
                .build());
        items.add(ScrollScreenItem.builder()
                .alignCenter(false)
                .footer(false)
                .header(false)
                .text("3.БД чеков")
                .build());
        items.add(ScrollScreenItem.builder()
                .alignCenter(false)
                .footer(false)
                .header(false)
                .text("4.Товары")
                .build());

        wait.waitExpectedEvent(Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .scrollScreen(ScrollScreen.builder()
                                        .items(items)
                                        .itemsCount(5)
                                        .needDrawScroll(true)
                                        .scroll(0)
                                        .build())
                                .build())
                        .build())
                .build());

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }

        wait.waitNotificationScreen(NotificationType.NOTIFICATION_INFO, "Замените ФН", "на новый", "Выкл. ККТ");

//        manager.executeSshCommand("sync");
//        manager.executeSshCommand("sync");
//        manager.executeSshCommand("sync");

        manager.stop();
        manager.sleepPlease(4_000);
        manager.rebootFiscat();
        inputPassword();
        soundOffForKF();
    }

    @Override
    @Step("Быстрое тех. обнуление, без использования пользовательского интерфейса")
    public void fastTechnicalZero() {
        manager.techZeroing();
    }

    @Override
    @Step("Выключить звук на КФ")
    public void soundOffForKF() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key9);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(1000);
    }

}
