package autotests.general;

import application_manager.api_manager.events.enums.ShiftStatus;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class LoadTest extends BaseTestClass {

    private int taskId = 0;
    private HubRequest exPurchase;
    private String errorOverflowFn = "Исчерпана память хранения ОФД";
    private HubRequest lastPollResult;
    private HubRequest lastStats;

    @BeforeClass
    public void beforeExternalPurchaseTests() {
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithIncorrectOfd(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();
    }

    @AfterClass
    public void afterExternalPurchaseTests() {
        manager.iAmHub(false);
    }

    @Test
    public void testOverflowFn() {
        do {
            steps.hub().answerPoll(getExPurch(30));
        } while (!getPollStatus().equals(errorOverflowFn));

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        do {
            lastStats = server.getLastRequest(MethodsEnum.STATS);
            assertNotNull(lastStats);
            assertNotNull(lastStats.getData().getNeedSendToOfd());
        } while (lastStats.getData().getNeedSendToOfd() != 0);

        steps.expectation().waitExpectedShiftEvent(ShiftStatus.SHIFT_OPENED);
    }

    //__________________________________________________________________________________________________________________
    //                                                  STEPS
    //__________________________________________________________________________________________________________________

    private String getPollStatus() {
        lastPollResult = server.getLastRequest(MethodsEnum.POLL);
        assertNotNull(lastPollResult);
        assertNotNull(lastPollResult.getData());
        assertNotNull(lastPollResult.getData().getTaskResults());
        assertEquals(lastPollResult.getData().getTaskResults().length, 15);
        for (int i = 0; i < lastPollResult.getData().getTaskResults().length; i++) {
            if (lastPollResult.getData().getTaskResults()[i].getResult().equals("ERROR")) {
                if (lastPollResult.getData().getTaskResults()[i].getResult().equals(errorOverflowFn)) {
                    return errorOverflowFn;
                }
            }
        }
        return "ОК";
    }

    private HubRequest getExPurch(int countPos) {
        if (exPurchase != null) {
            return exPurchase;
        }
        ArrayList<Positions> positions = new ArrayList<>();
        for (int i = 0; i < countPos; i++) {
            positions.add(Positions.builder()
                    .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_10").taxSum(0)
                    .build());
        }
        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();
        TaskResults[] tasks = new TaskResults[15];

        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = TaskResults.builder().taskId(taskId++)
                    .data(getPollTaskData(positions, total, TypeResponseExPurchase.CASHLESS,
                            "54651022bffebc03098b456" + taskId))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        exPurchase = HubRequest.builder().data(hubData).result("OK").build();
        return exPurchase;
    }

    private PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total,
                                         TypeResponseExPurchase paymentsType, String remId) {
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

}
