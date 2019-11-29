package application_manager.api_manager.events.json;

import application_manager.api_manager.events.enums.EventType;

import application_manager.api_manager.events.json.data.EventData;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@Builder
@ToString
public class Event {

    private EventData data;
    private EventType type;

}
