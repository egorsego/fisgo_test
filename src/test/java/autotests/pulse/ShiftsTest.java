package autotests.pulse;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.enums.ShiftStatus;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static application_manager.api_manager.events.enums.SyncTimeSourceType.*;
import static hub_emulator.response.enums.MethodsEnum.POLL;
import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Log4j
public class ShiftsTest extends BaseTestClass {

    private final int COUNT_TASKS_EX_PURCHASE = 15;
    private final int COUNT_REALY_FISCAL_PURCHASE = 4;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        steps.step().fastTechnicalZero();

        manager.iAmHub(true);
        manager.setPollTime(10);

        HubRequest responseKktRegInfo = RepositoryRegistrationResponse.getKktForInternetShop(manager.getRegNumKKT());
        steps.hub().registrationCashBox(RegistrationTypeEnum.REGISTRATION, responseKktRegInfo);
    }

    @AfterClass
    public void after() {
        steps.step().fastTechnicalZero();

        manager.rebootFiscat();

        manager.iAmHub(true);
        manager.setPollTime(10);

        HubRequest responseKktRegInfo = RepositoryRegistrationResponse.getKktForInternetShop(manager.getRegNumKKT());
        steps.hub().registrationCashBox(RegistrationTypeEnum.REGISTRATION, responseKktRegInfo);

        manager.iAmHub(false);
    }

    @BeforeMethod
    public void beforeMethod() {
        log.info("beforeMethod - ShiftsTest");
        manager.getEventsContainer().clearTimeEvents();
        manager.rebootFiscat("-n");
        manager.iAmHub(true);
        manager.setPollTime(10);

        steps.expectation().waitSyncTime(FS);
    }

    /**
     * Тест проверяет открытие и закрытие смены по истечении 24 часов
     */
    @Test
    public void testOpenCloseShift24Hours() {
        manager.getEventsContainer().clearEvents(EventType.SHIFT);

        steps.hub().answerPoll(getPurchases());

        checkResultPoll();

        manager.executeSshCommand("date +%s -s @" + steps.step().getTimePlus24Hours());

        steps.expectation().waitExpectedShiftEvent(ShiftStatus.SHIFT_24);
        steps.expectation().waitExpectedShiftEvent(ShiftStatus.SHIFT_CLOSED);

        steps.hub().answerPoll(getPurchases());
        steps.expectation().waitExpectedShiftEvent(ShiftStatus.SHIFT_OPENED);
    }

    /**
     * Тест проверяет переоткрытие смены при пробитии внешних чеков. Fiscat запущен с флагом -n (отсутствие ntpd).
     * <p>
     * 1. Дождаться синхронизации времени из ФН
     * 2. Отправить 15 чеков на кассу
     * 3. Дождаться пока касса зафискализирует 5 чеков
     * 4. Изменить время на 24 часа вперед, для того чтобы смена истекла
     * 5. Дождаться POLL с результатами фискализации этих чеков
     * 6. Проверить, что в этом POLL 5 результатов SUCCESS, а 10 - ERROR
     * 7. Отправить на кассу следущую пачку чеков
     * 8. Проверить, что они все успешно зафискализировались и касса отправила отчет
     */
    @Test
    public void testOpenCloseShift24HoursIfCashboxInProgress() {
        //отправить 15 чеков
        server.clearRequests(POLL);
        hubQueue.addResponse(POLL, getPurchases());
        assertTrue(server.checkReceivedRequest(POLL, 1), "JSON POLL не был отправлен на HUB");

        server.clearRequests(POLL);
        //дождаться PURCHASE_DOCUMENT_REPORT на 5 чека и изменить время на 24 часа вперед
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, COUNT_REALY_FISCAL_PURCHASE),
                "На сервер не пришло " + COUNT_REALY_FISCAL_PURCHASE + "PURCHASE_DOCUMENT_REPORT");

        manager.executeSshCommand("date +%s -s @" + steps.step().getTimePlus24Hours());

        //дождаться poll с результатом выполнения и проверить json
        checkPollWithResultFailedPurchase();

        //отправить следующие 15 чеков
        server.clearRequests();
        hubQueue.addResponse(POLL, getPurchases());
        assertTrue(server.checkReceivedRequest(POLL, 1), "JSON POLL не был отправлен на HUB");

        //проверить pol c результатом выполнения
        checkPollWithResultSuccessPurchase();
    }


    //__________________________________________________________________________________________________________________
    //________________________________________________STEPS ____________________________________________________________


    private void checkResultPoll() {
        HubRequest lastRequest = server.getLastRequest(POLL);
        assertNotNull(lastRequest);
        assertNotNull(lastRequest.getData());
        assertNotNull(lastRequest.getData().getTaskResults());
        assertEquals(COUNT_TASKS_EX_PURCHASE, lastRequest.getData().getTaskResults().length);
        for (int i = 0; i < lastRequest.getData().getTaskResults().length; i++) {
            assertEquals(lastRequest.getData().getTaskResults()[i].getResult(), "SUCCESS");
        }
    }

    @Step("Перерегистрация с режимом интернет-магазин")
    private void manualReRegistrationWithInternetWorkMode() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key7);
        manager.pressKey(KeyEnum.keyEnter, 3);
        manager.pressKey(KeyEnum.key7);
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        steps.expectation().waitExpectedLcd("Проверьте данные");
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();
    }

    @Step("Включить автооткрытие смены")
    private void enableAutoReOpenShift() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key6);
        manager.pressKey(KeyEnum.key1);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd("Включено", "Автомат.", "открытие", "смены");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Проверка JSON POLL с результатом успешного выполнения тасок")
    private void checkPollWithResultSuccessPurchase() {
        HubRequest lastRequest = server.getLastRequest(POLL);
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, COUNT_TASKS_EX_PURCHASE));
        assertEquals(COUNT_TASKS_EX_PURCHASE, lastRequest.getData().getTaskResults().length);
        assertEquals(getCountErrorResult(lastRequest), 0);
    }

    @Step("Проверка JSON POLL с результатом неуспешного выполнения тасок")
    private void checkPollWithResultFailedPurchase() {
        assertTrue(server.checkReceivedRequest(POLL, 1),
                "На сервер не пришел POLL с результатом выполнения тасок");
        HubRequest lastRequest = server.getLastRequest(POLL);
        assertEquals(COUNT_TASKS_EX_PURCHASE, lastRequest.getData().getTaskResults().length,
                "Количество результатов в POLL не соответствует ожидаемому");
        assertEquals(getCountErrorResult(lastRequest), 1,
                "Количество ERROR в результатах POLL не соответствует ожидаемому");
    }

    /**
     * Метод возвращает внешний чек
     */
    private HubRequest getPurchases() {
        ArrayList<Positions> positions = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            positions.add(Positions.builder()
                    .name("Товар" + i)
                    .type("COUNTABLE")
                    .quantity(1)
                    .price(1000)
                    .total(1000)
                    .tax("NDS_20")
                    .taxSum(0)
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[COUNT_TASKS_EX_PURCHASE];

        for (int i = 1; i < tasks.length + 1; i++) {
            tasks[i - 1] = TaskResults.builder().taskId(i)
                    .data(getPollTaskData(positions, total, CASHLESS, "54651022bffebc03098b456" + i))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total, TypeResponseExPurchase paymentsType, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(paymentsType).build());
        return PollTaskData.builder()
                .remId(remId)
                .taxMode("DEFAULT")
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email("g.glushkov@dreamkas.ru").build())
                .total(total)
                .build();
    }

    /**
     * Получить количество результатов poll со статусом ERROR
     *
     * @param request - запрос poll
     */
    private int getCountErrorResult(HubRequest request) {
        int countError = 0;
        for (int i = 0; i < request.getData().getTaskResults().length - 1; i++) {
            if (request.getData().getTaskResults()[i].getResult().equals("ERROR")) {
                countError++;
            }
        }
        return countError;
    }
}
