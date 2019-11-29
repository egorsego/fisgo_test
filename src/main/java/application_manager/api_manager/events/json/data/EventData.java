package application_manager.api_manager.events.json.data;


import application_manager.api_manager.events.json.data.lcdData.LcdData;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class EventData {

    private KeyData keyData;
    private LcdData lcdData;
    private TimeData timeData;
    private ShiftData shiftData;
    private PrinterData printerData;

}
