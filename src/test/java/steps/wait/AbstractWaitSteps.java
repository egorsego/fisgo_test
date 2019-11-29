package steps.wait;

import application_manager.api_manager.Manager;
import application_manager.api_manager.events.enums.DisplayType;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.lcdData.ImageId;
import application_manager.api_manager.events.json.data.lcdData.LcdData;
import application_manager.api_manager.events.json.data.lcdData.ListItem;
import application_manager.api_manager.events.json.data.lcdData.ListScreen;
import hub_emulator.Server;
import hub_emulator.json.HubRequest;

import java.util.ArrayList;

import static hub_emulator.response.enums.MethodsEnum.STATS;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public abstract class AbstractWaitSteps implements WaitSteps {

    Manager manager;
    Server server;

    public AbstractWaitSteps(Manager manager, Server server) {
        this.manager = manager;
        this.server = server;
    }

    /**
     * Метод ожидает отправку всех документов в ОФД. Каждый раз проверяется значение поля NeedSendToOfd в статистике.
     */
    @Override
    public void waitSendingAllDocsToOFD() {
        int a;
        int count = 0;
        boolean isNotSending = false;
        while (count != 10) {
            server.clearRequests(STATS);
            assertTrue(server.checkReceivedRequest(STATS, 1));
            HubRequest lastRequest = server.getLastRequest(STATS);
            assertNotNull(lastRequest);
            assertNotNull(lastRequest.getData());
            assertNotNull(lastRequest.getData().getNeedSendToOfd());
            a = lastRequest.getData().getNeedSendToOfd();
            if(a == 0){
                isNotSending = true;
                break;
            }
            count++;
        }
        assertTrue(isNotSending, "Документы не отправляются в ОФД");
    }

    @Override
    public void waitListScreen(int curPos, String text1, String text2, String text3, String text4) {
        ArrayList<ListItem> items = new ArrayList<>();
        items.add(ListItem.builder()
                .imageId(ImageId.UNDEFINED)
                .rightText("")
                .subscript("")
                .text(text1)
                .build());

        items.add(ListItem.builder()
                .imageId(ImageId.UNDEFINED)
                .rightText("")
                .subscript("")
                .text(text2)
                .build());

        items.add(ListItem.builder()
                .imageId(ImageId.UNDEFINED)
                .rightText("")
                .subscript("")
                .text(text3)
                .build());

        items.add(ListItem.builder()
                .imageId(ImageId.UNDEFINED)
                .rightText("")
                .subscript("")
                .text(text4)
                .build());

        waitExpectedEvent(Event.builder()
                .type(EventType.LCD)
                .data(EventData.builder()
                        .lcdData(LcdData.builder()
                                .display(DisplayType.DISPLAY_CASHIER)
                                .listScreen(ListScreen.builder()
                                        .curPos(1)
                                        .items(items)
                                        .build())
                                .build())
                        .build())
                .build());
    }

}
