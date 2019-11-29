package application_manager.api_manager.events;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import java.util.*;

import static application_manager.api_manager.events.enums.EventType.*;

@Log4j
public class EventsContainer {

    @Getter
    private Map<EventType, List<Event>> eventsMap;

    public EventsContainer() {
        eventsMap = new EnumMap<>(EventType.class);
        EventType[] methodsEnums = EventType.values();
        for (EventType type : methodsEnums) {
            eventsMap.put(type, new ArrayList<>());
        }
    }

    public void parseEvents(String[] events) {
        Event event;
        for (String e : events) {
            System.out.println(e);
            event = new Gson().fromJson(e, Event.class);
            if (event.getType().equals(LCD) || event.getType().equals(TIME)) {
                // log.debug(event);
            }
            eventsMap.get(event.getType()).add(event);
        }
    }

    /**
     * Метод который проверяет был ли на кассе необходимый экран. На вход принимает строки, которые должны быть
     * на экране
     */
    public boolean isContainsLcdEvents(String... expectedStr) {
        return isContainsLcdEvents(DisplayType.DISPLAY_CASHIER, expectedStr);
    }

    public boolean isContainsLcdEvents(DisplayType displayType, String... expectedStr) {
        return eventsMap.get(LCD).containsAll(EventsBuilder.buildLcdExpectedEvents(displayType, expectedStr));
    }

    public boolean isContainsPrintEvent(String... expectedStr) {
        return eventsMap.get(PRINTER).contains(EventsBuilder.buildPrinterEvent(expectedStr));
    }

    public boolean isContainsTimeEvent(Event event) {
        return eventsMap.get(TIME).contains(event);
    }

    public boolean isContainsShiftEvent(Event event) {
        return eventsMap.get(SHIFT).contains(event);
    }

    public void clearEvents(EventType type) {
        eventsMap.get(type).clear();
    }

    public void clearLcdEvents() {
        eventsMap.get(LCD).clear();
    }

    public void clearTimeEvents() {
        eventsMap.get(TIME).clear();
    }

}
