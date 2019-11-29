package steps.hubemul;

import application_manager.api_manager.Manager;
import hub_emulator.Server;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.response.ResponseHub;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;

import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.MethodsEnum.POLL;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Log4j
public abstract class AbstractHubEmulSteps implements HubEmulSteps{

    Manager manager;
    Server server;
    ResponseHub hubQueue;

    public AbstractHubEmulSteps(Manager manager, Server server, ResponseHub hubQueue) {
        this.manager = manager;
        this.server = server;
        this.hubQueue = hubQueue;
    }

    @Step("Ответить на POLL")
    @Override
    public void answerPoll(HubRequest response) {
        manager.sleepPlease(1000);
        server.clearRequests(POLL);
        hubQueue.addResponse(POLL, response);
        log.info("answerPoll -> start");
        assertTrue(server.checkReceivedRequest(POLL, 1), "JSON POLL не был отправлен на HUB");
        log.info("answerPoll -> finish");
        assertTrue(server.checkReceivedRequest(POLL, 2), "JSON POLL не был отправлен результат о выполнении таски");
    }

    @Step
    @Override
    public void checkPollSuccessResults() {
        HubRequest lastPoll = server.getLastRequest(POLL);
        assertNotNull(lastPoll);
        assertNotNull(lastPoll.getData());
        assertNotNull(lastPoll.getData().getTaskResults());
        for (TaskResults taskResults : lastPoll.getData().getTaskResults()) {
            assertEquals("SUCCESS", taskResults.getResult());
        }
    }

    @Override
    @Step("Зарегистрировать кассу через HUB")
    public void registrationCashBox(RegistrationTypeEnum type, HubRequest responseKktRegInfo) {
        hubQueue.addResponse(KKT_REGISTER_INFO, responseKktRegInfo);
        answerPoll(RepositoryPollResponse.getRegistration(type));
    }

}
