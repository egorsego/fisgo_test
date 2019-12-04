package autotests.general;

import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.json.license.Services;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static hub_emulator.response.enums.MethodsEnum.POLL;
import static hub_emulator.response.enums.MethodsEnum.STATS;
import static org.testng.Assert.*;

public class ActivateLicenseKeyTest extends BaseTestClass {

    @BeforeClass
    public void before() {
        manager.getEventsContainer().clearLcdEvents();
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            manager.clearDreamkasKey();
        }
        steps.step().technicalZero();
        manager.setPollTime(15);
        manager.iAmHub(true);
        steps.cab().connectToCabinet();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    /**
     * Тест активирования ключа платной лицензии
     * <p>
     * 1. Послать на кассу невалидный ключ
     * 2. Проверить что на экране не было сообщения об активации ключа
     * 3. Послать на кассу валидный ключ
     * 4. Проверить что на кассе было сообщение об активации ключа
     * 5. Проверить ответ на POLL с результатами об активации ключа
     * 6. Проверить json статистики с платными фичами.
     */
    @Test
    public void testActivateKey() {
        steps.hub().answerPoll(RepositoryPollResponse.getInvalidKey());

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertFalse(manager.getEventsContainer().isContainsLcdEvents("Подписка", "активирована"),
                    "Было сообщение \"Подписка активирована\"");
        } else {
            steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Некорректный",
                    "формат/пар-р", "команды", " ");
        }

        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("Подписка", "активирована"),
                    "На экране не было сообщения \"Подписка активирована\"");
        } else {
            steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_WARNING, "Подписка", "активирована");
        }

        //проверка ответа на структуру
        checkActivateSuccessOnStructure(server.getLastRequest(POLL));
        checkActivateSuccessOnValue(server.getLastRequest(POLL));

        assertTrue(server.checkReceivedRequest(STATS, 1), "JSON STATS не был отправлен на HUB");
        checkActivateStatsOnStructure(server.getLastRequest(STATS));
        checkActivateStatsOnValue(server.getLastRequest(STATS));
    }

    //______________________________________________STEPS_______________________________________________________________

    @Step("Проверка json stats об успешном активировании ключа на кассе (на значения)")
    private void checkActivateStatsOnValue(HubRequest requestStats) {

        //FIXME необходимо сформировать для всех касс ключ в котором 4 фичи и убрать этот костыль
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            assertEquals(4, requestStats.getData().getLicense().getServices().length);
        } else {
            assertEquals(3, requestStats.getData().getLicense().getServices().length);
        }

        Services[] services = requestStats.getData().getLicense().getServices();

        //FIXME необходимо сформировать для всех касс ключ в котором 4 фичи и убрать этот костыль
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            assertEquals(services[0].getCode(), Integer.valueOf(1));
            assertEquals(services[0].getDescription(), "НДС 20%");

            assertEquals(services[1].getCode(), Integer.valueOf(3));
            assertEquals(services[1].getDescription(), "ФФД 1.05 с 1 июля 2019");

            assertEquals(services[2].getCode(), Integer.valueOf(4));
            assertEquals(services[2].getDescription(), "Продажа табачной продукции");

            assertEquals(services[3].getCode(), Integer.valueOf(5));
            assertEquals(services[3].getDescription(), "Приемка маркированного товара");
        } else {
            assertEquals(services[0].getCode(), Integer.valueOf(1));
            assertEquals(services[0].getDescription(), "НДС 20%");

            assertEquals(services[1].getCode(), Integer.valueOf(3));
            assertEquals(services[1].getDescription(), "ФФД 1.05 с 1 июля 2019");

            assertEquals(services[2].getCode(), Integer.valueOf(4));
            assertEquals(services[2].getDescription(), "Продажа табачной продукции");
        }
    }

    @Step("Проверка json stats об успешном активировании ключа на кассе (на структуру)")
    private void checkActivateStatsOnStructure(HubRequest requestStats) {
        assertNotNull(requestStats);
        assertNotNull(requestStats.getData());
        assertNotNull(requestStats.getData().getLicense());
        assertNotNull(requestStats.getData().getLicense().getServices());

        for (Services services : requestStats.getData().getLicense().getServices()) {
            assertNotNull(services.getCode());
            assertNotNull(services.getDescription());
        }
    }

    @Step("Проверка json ответа на poll об успешной активации ключа (на значения)")
    private void checkActivateSuccessOnValue(HubRequest requestSuccessActivate) {
        for (TaskResults task : requestSuccessActivate.getData().getTaskResults()) {
            assertEquals(task.getTaskId(), Integer.valueOf(1));
            assertEquals(task.getResult(), "SUCCESS");
        }
    }

    @Step("Проверка json об успешном активировании ключа на кассе (на структуру)")
    private void checkActivateSuccessOnStructure(HubRequest requestSuccessActivate) {
        assertNotNull(requestSuccessActivate);
        assertNotNull(requestSuccessActivate.getUuid());
        assertNotNull(requestSuccessActivate.getData());
        assertNotNull(requestSuccessActivate.getData().getTaskResults());
        for (TaskResults task : requestSuccessActivate.getData().getTaskResults()) {
            assertNotNull(task.getTaskId());
            assertNotNull(task.getResult());
        }
    }
}
