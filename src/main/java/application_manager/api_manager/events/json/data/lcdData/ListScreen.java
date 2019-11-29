package application_manager.api_manager.events.json.data.lcdData;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class ListScreen {

    private ArrayList<ListItem> items;
    private Integer curPos;

}
