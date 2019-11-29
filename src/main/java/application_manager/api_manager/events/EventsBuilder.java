package application_manager.api_manager.events;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.*;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.lcdData.LcdData;
import application_manager.api_manager.events.json.data.PrinterData;
import application_manager.api_manager.events.json.data.StringDraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventsBuilder {

    private EventsBuilder() {
        throw new IllegalStateException("EventsBuilder - utility class");
    }

    public static List<Event> buildLcdExpectedEvents(DisplayType displayType, String[] textEventList) {
        ArrayList<Event> events = new ArrayList<>();
        for (String s : textEventList) {
            events.add(buildLcdEvent(displayType, s));
        }
        return events;
    }

    public static Event buildPrinterEvent(String[] text) {
        return Event.builder()
                .type(EventType.PRINTER)
                .data(EventData.builder()
                        .printerData(PrinterData.builder().printBuffer(new ArrayList<>(Arrays.asList(text))).build())
                        .build())
                .build();
    }

    private static Event buildLcdEvent(DisplayType displayType, String text) {
        return Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(displayType)
                                .stringDraw(StringDraw.builder()
                                        .bright("BRIGHT_OFF")
                                        .text(text)
                                        .build())
                                .build())
                        .build())
                .build();
    }

}
