package autotests.general;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.enums.SyncTimeSourceType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.TimeData;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;
import org.testng.annotations.*;

import static hub_emulator.response.enums.RegistrationTypeEnum.*;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

@Log4j
public class TimeTests extends BaseTestClass {

    private static final long DELTA = 60;

    @BeforeClass
    public void beforeClass() {
        manager.iAmHub(true);
        manager.setPollTime(10);

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            hubQueue.addResponse(MethodsEnum.WHO_AM_I, HubRequest.builder().result("OK").build());
        }

        steps.step().technicalZero();

        manager.iAmHub(true);
        manager.setPollTime(10);

        steps.cab().connectToCabinet();

        steps.hub().registrationCashBox(REGISTRATION, RepositoryRegistrationResponse.getKktRegCorrect(manager.getRegNumKKT()));
        steps.hub().checkPollSuccessResults();

        steps.shift().closeShift();

        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
    }

    @AfterClass
    public void afterClass() {
        manager.iAmHub(false);
    }

    @Test
    public void testNtpSync() {
        //устанавливаем в систему 2005 год
        manager.executeSshCommand("date +%s -s @1104537600");

        manager.rebootFiscat();
        steps.step().inputPassword();

        steps.expectation().waitSyncTime(SyncTimeSourceType.NTPD);

        long unixTimeFromLocMachine = getUnixTimeFromLocMachine();
        long l = manager.getCurrentDate().getMillis() / 1000;
        log.debug("Время на машине - " + unixTimeFromLocMachine);
        log.debug("Время на кассе  - " + l);

        assertTrue(Math.abs(unixTimeFromLocMachine - l) <= DELTA);
    }

    @Test
    public void testWithoutNtpSync() {
        manager.rebootFiscat();
        steps.step().inputPassword();
        steps.expectation().waitSyncTime(SyncTimeSourceType.NTPD);

        //устанавливаем в систему 2005 год
        manager.executeSshCommand("date +%s -s @1104537600");

        manager.rebootFiscat("-n");
        steps.step().inputPassword();

        steps.expectation().waitSyncTime(SyncTimeSourceType.FS);

        long unixTimeFromLocMachine = getUnixTimeFromLocMachine();
        long l = manager.getCurrentDate().getMillis() / 1000;
        log.debug("Время на машине - " + unixTimeFromLocMachine);
        log.debug("Время на кассе  - " + l);

        assertTrue(Math.abs(unixTimeFromLocMachine - l) <= DELTA);
    }

    @Test
    public void testWithoutNtpAndDeltaFNSync() {
        //устанавливаем в систему 2005 год
        manager.executeSshCommand("date +%s -s @1104537600");

        manager.executeSshCommand("echo \"attach '/FisGo/configDb.db' as CONFIG; delete from CONFIG.time_fs;\" " +
                "| sqlite3 /FisGo/configDb.db");
        manager.rebootFiscat("-n");

        //проверка что появился экран с вводом времени
        manager.sleepPlease(10_000);
        steps.expectation().waitExpectedLcd("Введите дату");

        //устанавливаем время 11.11.11 11:11
        manager.pressKey(KeyEnum.key0, 10);
        manager.pressKey(KeyEnum.key1, 10);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        steps.step().inputPassword();

        steps.expectation().waitSyncTime(SyncTimeSourceType.USER);

        long l = manager.getCurrentDate().getMillis() / 1000;
        log.debug("Время на кассе  - " + l);

        assertTrue("Время на кассе не соответсвует ожидаемому (на кассе - " + l + " , ожидается - 1320999080)", Math.abs(1320999080 - l) <= DELTA);
    }

    @Test
    public void testWithOpenShift() {
        steps.shift().openShift();

        //устанавливаем в систему 2005 год
        manager.executeSshCommand("date +%s -s @1104537600");

        manager.rebootFiscat();
        steps.step().inputPassword();
        steps.expectation().waitSyncTime(SyncTimeSourceType.FS);

        long unixTimeFromLocMachine = getUnixTimeFromLocMachine();
        long l = manager.getCurrentDate().getMillis() / 1000;
        log.debug("Время на машине - " + unixTimeFromLocMachine);
        log.debug("Время на кассе  - " + l);

        steps.shift().closeShift();

        assertTrue(Math.abs(unixTimeFromLocMachine - l) <= DELTA);
    }

    @Test
    public void testChangeTimeZoneInOpenShift() {
        steps.shift().openShift();

        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd("Необходимо", "закрыть", "смену");

        steps.shift().closeShift();
    }

    //__________________________________________________________________________________________________________________
    //                                                   STEPS
    //__________________________________________________________________________________________________________________

    private void checkWriteTimeToFsFromFs() {
        manager.getEventsContainer().clearEvents(EventType.TIME);

        manager.rebootFiscat("-n");
        steps.step().inputPassword();

        steps.expectation().waitSyncTime(SyncTimeSourceType.FS);
        checkMissingEventWriteToFs();
    }

    private void checkFailWriteTimeToFsFromNtpd() {
        manager.getEventsContainer().clearEvents(EventType.TIME);

        manager.rebootFiscat();
        steps.step().inputPassword();

        steps.expectation().waitSyncTime(SyncTimeSourceType.NTPD);
        checkMissingEventWriteToFs();
    }

    @Step("Получить текущее время на машине")
    private long getUnixTimeFromLocMachine() {
        return new DateTime(DateTimeZone.UTC).getMillis() / 1000;
    }

    @Step("Проверить отсутствие ивента записи времени в ФН")
    private void checkMissingEventWriteToFs() {
        log.debug("WAIT MISSING EVENT WRITE TO FS");

        Event event = Event.builder()
                .type(EventType.TIME)
                .data(EventData.builder()
                        .timeData(TimeData.builder().source(SyncTimeSourceType.TO_FS).timezone(3).build())
                        .build())
                .build();
        int count = 0;
        while (count != 10) {
            if (manager.getEventsContainer().isContainsTimeEvent(event)) {
                break;
            }
            manager.sleepPlease(1000);
            count++;
        }
        Assert.assertFalse(count != 10, "Время записалось в ФН");
    }

    @Step("Установить время последней операции больше текущего")
    private void setTimeLastFiscalOperationMoreCurTime() {
        manager.getEventsContainer().clearEvents(EventType.LCD);

        manager.rebootFiscat("-n");
        steps.step().inputPassword();

        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();

        steps.expectation().waitSyncTime(SyncTimeSourceType.FS);

        manager.executeSshCommand("date +%s -s @" + steps.step().getTimePlus24Hours());

        if (!cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.expectation().waitExpectedLcd("Смена", "более", "24 ч");
        }
        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();

        steps.shift().closeShift();
    }

    @Step("Проверить POLL с результатом некорректной регистрации из Кабинета при отключенном ФН")
    private void checkErrorRegWithNoNtpd() {
        HubRequest lastRequest = server.getLastRequest(MethodsEnum.POLL);
        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData());
        assertNotNull(lastRequest.getData().getTaskResults());
        assertEquals(1, lastRequest.getData().getTaskResults().length);
        assertNotNull(lastRequest.getData().getTaskResults()[0]);
        assertNotNull(lastRequest.getData().getTaskResults()[0].getResult());
        assertEquals("ERROR", lastRequest.getData().getTaskResults()[0].getResult());
        assertNotNull(lastRequest.getData().getTaskResults()[0].getErrorCode());
        assertEquals("Ошибка синхронизации времени перед регистрацией", lastRequest.getData().getTaskResults()[0].getErrorCode());
        assertNotNull(lastRequest.getData().getTaskResults()[0].getMessage());
        assertEquals("Ошибка синхронизации времени перед регистрацией", lastRequest.getData().getTaskResults()[0].getMessage());
    }

    @Step("Установить кассу в начальное состояние")
    private void setStartStateForCashbox() {
        manager.getEventsContainer().clearEvents(EventType.TIME);
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            hubQueue.addResponse(MethodsEnum.WHO_AM_I, HubRequest.builder().result("OK").build());
        }
        steps.step().technicalZero();
        steps.expectation().waitSyncTime(SyncTimeSourceType.NTPD);
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        }
    }

}
