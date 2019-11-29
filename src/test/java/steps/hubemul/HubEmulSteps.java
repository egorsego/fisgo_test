package steps.hubemul;

import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.RegistrationTypeEnum;

public interface HubEmulSteps {

    void answerPoll(HubRequest response);

    void checkPollSuccessResults();

    void registrationCashBox(RegistrationTypeEnum type, HubRequest responseKktRegInfo);

}
