package autotests.general;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.MethodsEnum;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class KabinetDreamkasTest extends BaseTestClass {

    @BeforeClass(alwaysRun = true)
    public void beforeKabinetDreamkas() {
        manager.iAmHub(true);
    }

    @Test
    @DisplayName("Проверка JSON подключения/отключения к Кабинету")
    public void testConnectDisconnectToCabinet() {
        steps.cab().connectToCabinet();
        disconnectToKabinet();
        steps.cab().connectToCabinet();
    }

    @Test
    public void testConnectCabinetWithProductCount() {
        hubQueue.addResponse(MethodsEnum.REGISTER, HubRequest.builder()
                .result("OK")
                .data(HubData.builder()
                        .owner("x@x.com")
                        .productCount("100")
                        .build())
                .build());

        steps.cab().connectToCabinet();
    }

    @AfterClass(alwaysRun = true)
    public void afterKabinetDreamkas() {
        manager.iAmHub(false);
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }

    //-----------------------------------------------STEPS--------------------------------------------------------------

    @Step("Перейти в меню - Кабинет")
    private void goToKabinetMenu() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key8);
        manager.sendCommands();
    }

    @Step("Отключение к Кабинету")
    private void disconnectToKabinet() {
        goToKabinetMenu();
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.sleepPlease(1000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("Касса", "успешно", "отключена"));
        } else {
            steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_OK, "Касса", "успешно", "отключена");
        }
    }

}
