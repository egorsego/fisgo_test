package autotests.general;

import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;

public class ExternalPurchaseDifferentTaxModes extends BaseTestClass {

    private List<String> taxModes;

    @BeforeClass
    public void beforeExternalPurchaseTests() {
        manager.iAmHub(true);
        manager.setPollTime(10);

        steps.step().technicalZero();

        manager.iAmHub(true);
        manager.setPollTime(10);

        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        regNum = manager.getRegNumKKT();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
        steps.hub().checkPollSuccessResults();
    }

    /**
     * taxModes.add("DEFAULT");
     * taxModes.add("SIMPLE");
     * taxModes.add("SIMPLE_WO");
     * taxModes.add("ENVD");
     * taxModes.add("AGRICULT");
     * taxModes.add("PATENT");
     */

    @Test
    public void testExPurchDEFAULT() {
        taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("DEFAULT"));
        steps.hub().checkPollSuccessResults();
    }


    @Test
    public void testExPurchSIMPLE() {
        taxModes = new ArrayList<>();
        taxModes.add("SIMPLE");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("SIMPLE"));
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testExPurchSIMPLE_WO() {
        taxModes = new ArrayList<>();
        taxModes.add("SIMPLE_WO");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("SIMPLE_WO"));
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testExPurchENVD() {
        taxModes = new ArrayList<>();
        taxModes.add("ENVD");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("ENVD"));
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testExPurchAGRICULT() {
        taxModes = new ArrayList<>();
        taxModes.add("AGRICULT");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("AGRICULT"));
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testExPurchPATENT() {
        taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");

        steps.shift().closeShift();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithNeedTaxMode(taxModes, regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        steps.hub().checkPollSuccessResults();

        steps.hub().answerPoll(getExPurch("PATENT"));
        steps.hub().checkPollSuccessResults();
    }


    //__________________________________________________________________________________________________________________
    //__________________________________________________________________________________________________________________

    public static HubRequest getExPurch(String taxMode) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_10").taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        tasks[0] = TaskResults.builder().taskId(1)
                .data(getPollTaskData(positions, total, CASHLESS, "54651022bffebc03098b4561", taxMode))
                .taskType("external_purchase").build();

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private static PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total,
                                                TypeResponseExPurchase paymentsType, String remId, String taxMode) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(paymentsType).build());
        return PollTaskData.builder()
                .remId(remId)
                .taxMode(taxMode)
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .attributes(Attributes.builder().email("g.glushkov@dreamkas.ru").build())
                .total(total)
                .build();
    }
}
