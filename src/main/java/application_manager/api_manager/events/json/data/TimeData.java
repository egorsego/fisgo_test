package application_manager.api_manager.events.json.data;

import application_manager.api_manager.events.enums.SyncTimeSourceType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TimeData {

    @EqualsAndHashCode.Exclude
    String dateUnix;
    private SyncTimeSourceType source;
    private Integer timezone;
    private String status;

}
