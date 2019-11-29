package autotests.general;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
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
import hub_emulator.response.repository.RepositoryAgentTags;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.AGENT_MASK;
import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.MethodsEnum.POLL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class ExternalPurchaseAgents extends BaseTestClass {

    @BeforeClass
    public void beforeExternalPurchaseTests() {
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));

        steps.cab().connectToCabinet();
        registrationWithAllAgents();
    }

    @AfterClass
    public void afterExternalPurchaseTests() {
        manager.iAmHub(false);
    }

    //__________________________________________________________________________________________________________________
    //                                              АГЕНТЫ НА ПОЗИЦИЮ
    //__________________________________________________________________________________________________________________

    @Test
    public void testCorrectAllAgentsTag() {
        HubRequest exPurch = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent1(),
                RepositoryAgentTags.getAgent2(),
                RepositoryAgentTags.getAgent4(),
                RepositoryAgentTags.getAgent8(),
                RepositoryAgentTags.getAgent16(),
                RepositoryAgentTags.getAgent32(),
                RepositoryAgentTags.getAgent64());

        steps.hub().answerPoll(exPurch);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent1() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent1());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent2() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent2());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent4() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent4());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent8() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent8());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent16() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent16());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent32() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent32());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent64() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getAgent64());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    //__________________________________________________________________________________________________________________
    //                                              АГЕНТЫ НА ЧЕК
    //__________________________________________________________________________________________________________________

    @Test
    public void testCorrectAgent1OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent1());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent2OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent2());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent4OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent4());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent8OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent8());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent16OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent16());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent32OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent32());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testCorrectAgent64OnPurch() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPurch(
                RepositoryAgentTags.getAgent64());

        steps.hub().answerPoll(exPurchAllTags);
        steps.hub().checkPollSuccessResults();
    }

    //__________________________________________________________________________________________________________________
    //                                              НЕВАЛИДНЫЕ
    //__________________________________________________________________________________________________________________

    @Test
    public void testIncorrectAgent64OnPurcAndPosition() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositionsAndPurch(
                RepositoryAgentTags.getAgent64());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAllAgentsTag() {
        HubRequest exPurch = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent1(),
                RepositoryAgentTags.getIncorrectAgent2(),
                RepositoryAgentTags.getIncorrectAgent4(),
                RepositoryAgentTags.getIncorrectAgent8(),
                RepositoryAgentTags.getIncorrectAgent16(),
                RepositoryAgentTags.getIncorrectAgent32(),
                RepositoryAgentTags.getIncorrectAgent64());

        steps.hub().answerPoll(exPurch);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent1() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent1());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent2() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent2());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent4() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent4());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent8() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent8());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent16() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent16());

        steps.hub().answerPoll(exPurchAllTags);

        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent32() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent32());

        steps.hub().answerPoll(exPurchAllTags);
        checkPollErrorResults();
    }

    @Test
    public void testIncorrectAgent64() {
        HubRequest exPurchAllTags = getExPurchWithTagsOnPositions(
                RepositoryAgentTags.getIncorrectAgent64());

        steps.hub().answerPoll(exPurchAllTags);
        checkPollErrorResults();
    }

    //__________________________________________________________________________________________________________________
    //                                                  STEPS
    //__________________________________________________________________________________________________________________

    private void checkPollErrorResults() {
        HubRequest lastPoll = server.getLastRequest(POLL);
        assertNotNull(lastPoll);
        assertNotNull(lastPoll.getData());
        assertNotNull(lastPoll.getData().getTaskResults());
        for (TaskResults taskResults : lastPoll.getData().getTaskResults()) {
            assertEquals("ERROR", taskResults.getResult());
        }
    }

    private HubRequest getExPurchWithTagsOnPositionsAndPurch(ArrayList<Tags>... tags) {

        ArrayList<Positions> positions = new ArrayList<>();

        for (ArrayList<Tags> tagArr : tags) {
            positions.add(Positions.builder()
                    .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_NO_TAX").taxSum(0)
                    .tags(tagArr)
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(9513).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        for (int i = 0; i < tags.length; i++) {
            tasks[i] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskDataWithTags(tags[i], positions, total, TypeResponseExPurchase.CASHLESS,
                            "54651022bffebc03098b456" + i))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();

    }

    private HubRequest getExPurchWithTagsOnPositions(ArrayList<Tags>... tags) {

        ArrayList<Positions> positions = new ArrayList<>();

        for (ArrayList<Tags> tagArr : tags) {
            positions.add(Positions.builder()
                    .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_NO_TAX").taxSum(0)
                    .tags(tagArr)
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(9513).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        tasks[0] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                .data(getPollTaskData(positions, total, TypeResponseExPurchase.CASHLESS, "54651022bffebc03098b4561"))
                .taskType("external_purchase").build();

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();

    }

    private HubRequest getExPurchWithTagsOnPurch(ArrayList<Tags>... tags) {
        ArrayList<Positions> positions = new ArrayList<>();

        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_NO_TAX").taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(9513).build())
                .build();

        TaskResults[] tasks = new TaskResults[tags.length];

        for (int i = 0; i < tags.length; i++) {
            tasks[i] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskDataWithTags(tags[i], positions, total, TypeResponseExPurchase.CASHLESS,
                            "54651022bffebc03098b456" + i))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private static PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total,
                                                TypeResponseExPurchase paymentsType, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(2000).type(paymentsType).build());
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

    private static PollTaskData getPollTaskDataWithTags(ArrayList<Tags> tags, ArrayList<Positions> positions, Total total,
                                                        TypeResponseExPurchase paymentsType, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(2000).type(paymentsType).build());
        return PollTaskData.builder()
                .remId(remId)
                .taxMode("DEFAULT")
                .type("SALE")
                .positions(positions)
                .payments(payments)
                .tags(tags)
                .attributes(Attributes.builder().email("g.glushkov@dreamkas.ru").build())
                .total(total)
                .build();
    }

    private HubRequest getKeyForPulse() {
        String key = "MSwxOzM7NTs0OzYsG5e8HGbYPJUXVHi0H8IUyYxTGqTgAxLKssEq8ONwBSOkGxaDPIqQt3QB8P1j3aoA2f" +
                "Nf5svYrHKzT0a5846R+iUi34D1sMAnnW02RLW41vnjeauhYhbZHsAd5tPzv/r4oWj1GWzhU8nf3z9bMeGXMY8jMJv9m21s/N4lilBi" +
                "Cm5+4WX8RZtS+rm432FX6Cd2ayJcxfAxnCV988pcrQ5XLoeQiAxgCRYp5lULYIyE9krgulwifitf0FlY8QpWe19wJWZxZTEVNfl986" +
                "pS4UBH556dwm9s0OLapAp0ugaSugnJPp/I+R+pwcKn9lA7H310gFzyNry6fqodR4uwjGHOAw==";

        PollTaskData pollTaskData = PollTaskData.builder().key(key).remId("1").build();
        TaskResults[] tasks = new TaskResults[]{TaskResults.builder()
                .taskId(1)
                .data(pollTaskData)
                .taskType("key_activation")
                .build()};

        HubData hubData = HubData.builder().task(tasks).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private void registrationWithAllAgents() {
        Map<ConfigFieldsEnum, String> configFields = manager.getConfigFields(ConfigFieldsEnum.STAGE, AGENT_MASK);
        RegistrationTypeEnum regType;

        if (configFields.get(ConfigFieldsEnum.STAGE).equals("2")) {
            if (configFields.get(AGENT_MASK).equals("127")) {
                return;
            }
            regType = RegistrationTypeEnum.CHANGE_PARAMETERS;
        } else {
            regType = RegistrationTypeEnum.REGISTRATION;
        }

        steps.shift().closeShift();
        regNum = manager.getRegNumKKT();
        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(regType));
        steps.shift().openShift();
    }

}
