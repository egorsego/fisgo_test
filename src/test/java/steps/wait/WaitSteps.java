package steps.wait;

import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.ShiftStatus;
import application_manager.api_manager.events.enums.SyncTimeSourceType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.api_manager.events.json.data.lcdData.SalesListItem;

import java.util.List;

public interface WaitSteps {

    void waitExpectedLcd(String... expectedTexts);

    void waitExpectedLcd(DisplayType displayType, String... expectedTexts);

    void waitExpectedEvent(Event event);

    void waitNotificationScreen(NotificationType type, String... text);

    void waitListScreen(int curPos, String text1, String text2, String text3, String text4);

    void waitSalesListScreen(int selectedItem, String totalSum, List<SalesListItem> items);

    void waitLoader();

    void waitSyncTime(SyncTimeSourceType source);

    void waitExpectedShiftEvent(ShiftStatus status);

    void waitUpdateLcd();

    void waitSendingAllDocsToOFD();

}
