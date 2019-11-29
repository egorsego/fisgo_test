package application_manager.api_manager.events.json.data.lcdData;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ScrollScreenItem {
    private String text;
    private Boolean header;
    private Boolean footer;
    private Boolean alignCenter;
}
