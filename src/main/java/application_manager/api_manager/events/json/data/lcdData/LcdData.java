package application_manager.api_manager.events.json.data.lcdData;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.json.data.StringDraw;
import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class LcdData {

    @SerializedName("string_draw")
    private StringDraw stringDraw;

    private DisplayType display;

    //__________________________________________________________________________________________________________________
    // GRAPH
    //__________________________________________________________________________________________________________________

    private MenuScreen menuScreen;
    private NotificationScreen notificationScreen;
    private StatusBar statusBar;
    private InputScreen inputScreen;
    private CheckList checkList;
    private ListScreen listScreen;
    private SalesList salesListScreen;
    private Loader loader;
    private DateTimeScreen dateTimeScreen;
    private ScrollScreen scrollScreen;
    private XReportScreen xReportScreen;

}
