package steps.hubemul;

import application_manager.api_manager.Manager;
import hub_emulator.Server;
import hub_emulator.json.HubRequest;
import hub_emulator.response.ResponseHub;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;

import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;

@Log4j
public class HubEmulStepsKF extends AbstractHubEmulSteps {

    public HubEmulStepsKF(Manager manager, Server server, ResponseHub hubQueue) {
        super(manager, server, hubQueue);
    }

    @Override
    @Step("Зарегистрировать кассу через HUB")
    public void registrationCashBox(RegistrationTypeEnum type, HubRequest responseKktRegInfo) {
        hubQueue.addResponse(KKT_REGISTER_INFO, responseKktRegInfo);
        answerPoll(RepositoryPollResponse.getRegistration(type));
    }
}
