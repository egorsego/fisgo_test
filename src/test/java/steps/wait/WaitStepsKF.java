package steps.wait;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.enums.ShiftStatus;
import application_manager.api_manager.events.enums.SyncTimeSourceType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.ShiftData;
import application_manager.api_manager.events.json.data.TimeData;
import application_manager.api_manager.events.json.data.lcdData.*;
import hub_emulator.Server;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static application_manager.api_manager.events.enums.EventType.LCD;
import static org.testng.Assert.assertTrue;

@Log4j
public class WaitStepsKF extends AbstractWaitSteps {

    public WaitStepsKF(Manager manager, Server server) {
        super(manager, server);
    }

    @Override
    @Step("Дождаться обновления экрана")
    public void waitUpdateLcd() {
        int countLcdEvents = manager.getEventsContainer().getEventsMap().get(LCD).size();
        while (countLcdEvents >= manager.getEventsContainer().getEventsMap().get(LCD).size()) {
            manager.sleepPlease(100);
        }
    }

    /**
     * Метод позволяет дождаться необходимого экрана на кассе.
     *
     * @param expectedTexts - массив строк ожидаемых на экране
     */
    @Override
    public void waitExpectedLcd(String... expectedTexts) {
        waitExpectedLcd(DisplayType.DISPLAY_CASHIER, expectedTexts);
    }

    @Override
    public void waitExpectedLcd(DisplayType displayType, String... expectedTexts) {
        int count = 0;
        while (count != 60) {
            if (manager.getEventsContainer().isContainsLcdEvents(displayType, expectedTexts)) {
                break;
            }
            manager.sleepPlease(500);
            count++;
        }
        assertTrue(count != 60,
                "Не удалось перейти на ожидаемый экран" + Arrays.toString(expectedTexts));
    }

    @Override
    public void waitExpectedEvent(Event event) {
        int count = 0;
        EventType type = event.getType();
        log.debug("wait event...");
        while (count != 60) {
            List<Event> events = manager.getEventsContainer().getEventsMap().get(type);

            if (events.contains(event)) {
                break;
            }
            manager.sleepPlease(1000);
            count++;
        }
        assertTrue(count != 60,
                "Ожидаемый event не был получен -> " + event);
    }

    @Override
    @Step("Дождаться завершения лоудера")
    public void waitLoader() {
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
    }

    @Override
    @Step("Дождаться синхронизации времени")
    public void waitSyncTime(SyncTimeSourceType source) {
        Event event = Event.builder()
                .type(EventType.TIME)
                .data(EventData.builder()
                        .timeData(TimeData.builder().source(source).timezone(3).build())
                        .build())
                .build();
        int count = 0;
        while (count != 60) {
            if (manager.getEventsContainer().isContainsTimeEvent(event)) {
                break;
            }
            manager.sleepPlease(1000);
            count++;
        }
        assertTrue(count != 60, "Не подтянулось время из " + source.toString());
    }

    @Override
    @Step("Дождаться shift event")
    public void waitExpectedShiftEvent(ShiftStatus status) {
        waitExpectedEvent(Event.builder()
                .type(EventType.SHIFT)
                .data(EventData.builder()
                        .shiftData(ShiftData.builder()
                                .status(status)
                                .build())
                        .build())
                .build());
    }

    @Override
    public void waitNotificationScreen(NotificationType type, String... text) {
        ArrayList<String> textArr;
        if (text == null) {
            textArr = null;
        } else {
            textArr = new ArrayList<>(Arrays.asList(text));
        }

        waitExpectedEvent(Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .notificationScreen(NotificationScreen.builder()
                                        .text(textArr)
                                        .type(type)
                                        .build())
                                .build())
                        .build())
                .build());
    }

    @Override
    public void waitSalesListScreen(int selectedItem, String totalSum, List<SalesListItem> items) {
        Event build = Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .salesListScreen(SalesList.builder()
                                        .selectedItem(selectedItem)
                                        .totalSum(totalSum)
                                        .items(items)
                                        .build())
                                .build())
                        .build())
                .build();
        waitExpectedEvent(build);
    }
}
