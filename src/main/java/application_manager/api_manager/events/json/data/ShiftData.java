package application_manager.api_manager.events.json.data;

import application_manager.api_manager.events.enums.ShiftStatus;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ShiftData {

    private ShiftStatus status;

}
