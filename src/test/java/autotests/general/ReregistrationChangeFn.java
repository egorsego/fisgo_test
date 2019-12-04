package autotests.general;

import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import com.google.gson.Gson;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.FS_NUMBER;
import static hub_emulator.response.enums.MethodsEnum.CASH_INFO_REPORT;
import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.MethodsEnum.REGISTRATION_REPORT;
import static hub_emulator.response.enums.RegistrationTypeEnum.CHANGE_PARAMETERS_AND_FN;
import static hub_emulator.response.enums.RegistrationTypeEnum.CLOSE_FN;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ReregistrationChangeFn extends BaseTestClass {

    private String fnNum;

    @BeforeClass
    public void before() {
        manager.getEventsContainer().clearLcdEvents();
        regNum = manager.getRegNumKKT();

        steps.step().technicalZero();
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        registrationWithoutDefaultTax();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void testReregistrationChangeFn() {
        checkCloseFn();
    }

    @Test
    public void testFailReregistrationChangeFn() {
        fnNum = manager.getConfigFields(FS_NUMBER).get(FS_NUMBER);
        server.clearRequests();

        String incorrectFnNum = "123123";
        steps.hub().answerPoll(RepositoryPollResponse.getCloseFn(CLOSE_FN, incorrectFnNum));
    }

    @Step
    private void checkCloseFn() {
        fnNum = manager.getConfigFields(FS_NUMBER).get(FS_NUMBER);
        server.clearRequests();

        steps.hub().answerPoll(RepositoryPollResponse.getCloseFn(CLOSE_FN, fnNum));

        checkRegistrationReport();

        manager.fsReset();

        manager.rebootFiscat();

        manager.iAmHub(true);
        manager.setPollTime(5);
        //Дождаться CASH_INFO_REPORT
        checkCashInfoReport();

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getCloseFn(CHANGE_PARAMETERS_AND_FN, fnNum));
        //дождаться получения KKT_REGISTER_INFO
        checkKktRegisterInfo();

        steps.step().inputPassword();
    }

    @Step
    private void checkRegistrationReport() {
        assertTrue(server.checkReceivedRequest(REGISTRATION_REPORT, 1), "JSON REGISTRATION_REPORT не был отправлен на HUB");
        String registrationReport = server.getLastRequest();
        System.out.println("\n" + "\n" + "\n" + registrationReport);
    }

    @Step
    private void checkKktRegisterInfo() {
        assertTrue(server.checkReceivedRequest(KKT_REGISTER_INFO, 1));
        String kktRegisterInfo = server.getLastRequest();
        assertTrue(steps.step().isJSONValid(kktRegisterInfo));
        assertNotNull(new Gson().fromJson(kktRegisterInfo, HubRequest.class).getUuid());
    }

    @Step
    private void checkCashInfoReport() {
        assertTrue(server.checkReceivedRequest(CASH_INFO_REPORT, 1), "JSON CASH_INFO_REPORT не был отправлен на HUB");
        String cashInfoReport = server.getLastRequest();
        System.out.println("\n" + "\n" + "\n" + cashInfoReport);
    }

    @Step("Регистрация кассы без ОСН")
    private void registrationWithoutDefaultTax() {
        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegWithoutDefaultTax(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
    }


}
