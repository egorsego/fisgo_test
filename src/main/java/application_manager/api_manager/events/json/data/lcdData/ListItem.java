package application_manager.api_manager.events.json.data.lcdData;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ListItem {

    private String text;
    private String subscript;
    private String rightText;
    private ImageId imageId;

}
