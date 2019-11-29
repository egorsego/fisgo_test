package application_manager.api_manager.events.json.data.lcdData;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Icons {

    private IconsStatus wifi;
    private IconsStatus ethernet;
    private IconsStatus cabinet;
    private IconsStatus net2g;

}
