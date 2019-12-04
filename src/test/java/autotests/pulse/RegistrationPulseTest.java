package autotests.pulse;

import autotests.BaseTestClass;
import com.google.gson.Gson;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.FS_NUMBER;
import static hub_emulator.response.enums.MethodsEnum.*;
import static hub_emulator.response.enums.RegistrationTypeEnum.CHANGE_PARAMETERS_AND_FN;
import static hub_emulator.response.enums.RegistrationTypeEnum.CLOSE_FN;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class RegistrationPulseTest extends BaseTestClass {

    private HubRequest withoutRegNumAndAutoNum;
    private HubRequest incorrectKktRegInfo;
    private HubRequest correctKktRegInfo;
    private HubRequest resultPoll;
    private HubRequest correctChangeRegInfo;

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        regNum = manager.getRegNumKKT();

        manager.techZeroing();
        manager.rebootFiscat();

        withoutRegNumAndAutoNum = RepositoryRegistrationResponse.getKktRegForPulseIncorrect(null);
        incorrectKktRegInfo = RepositoryRegistrationResponse.getKktRegForPulseIncorrect(regNum);
        correctKktRegInfo = RepositoryRegistrationResponse.getKktRegForPulse(regNum);
        correctChangeRegInfo = RepositoryRegistrationResponse.getKktRegForPulseChange(regNum);

        manager.iAmHub(true);
        manager.setPollTime(10);

        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
        steps.hub().checkPollSuccessResults();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void testIncorrectWithoutRegNumAndAutoNum() {
        //incorrect(без рег. номера и номера автомата)
        server.clearRequests(REGISTRATION_REPORT);
        hubQueue.addResponse(KKT_REGISTER_INFO, withoutRegNumAndAutoNum);
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        server.checkReceivedRequest(REGISTRATION_REPORT, 1);
        resultPoll = server.getLastRequest(POLL);
        checkIncorrectResultPoll(resultPoll, "Не заполнен регистрационный номер ККТ");
    }

    @Test
    public void testIncorrectAutoNum() {
        //incorrect(без номера автомата)
        server.clearRequests(REGISTRATION_REPORT);
        hubQueue.addResponse(KKT_REGISTER_INFO, incorrectKktRegInfo);
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        server.checkReceivedRequest(REGISTRATION_REPORT, 1);
        resultPoll = server.getLastRequest(POLL);
        checkIncorrectResultPoll(resultPoll, "Отсутствует номер автомата");
    }

    @Test
    public void testCorrect() {
        //correct
        server.clearRequests(REGISTRATION_REPORT);
        hubQueue.addResponse(KKT_REGISTER_INFO, correctKktRegInfo);
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        server.checkReceivedRequest(REGISTRATION_REPORT, 1);
        resultPoll = server.getLastRequest(POLL);
        checkCorrectResultPoll(resultPoll);
    }

    @Test
    public void testIncorrectIfKktAlreadyFiscal() {
        //ККТ уже зарегистрирована
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
        resultPoll = server.getLastRequest(POLL);
        checkIncorrectResultPoll(resultPoll, "ККТ уже зарегистрирована");
    }

    @Test
    public void testReRegistration() {
        steps.hub().answerPoll(RepositoryPollResponse.getExPurch(TypeResponseExPurchase.CASHLESS));
        steps.hub().checkPollSuccessResults();

        server.clearRequests(REGISTRATION_REPORT);
        hubQueue.addResponse(KKT_REGISTER_INFO, correctChangeRegInfo);
        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.CHANGE_PARAMETERS));
        server.checkReceivedRequest(REGISTRATION_REPORT, 1);
        resultPoll = server.getLastRequest(POLL);
        checkCorrectResultPoll(resultPoll);

        steps.hub().answerPoll(RepositoryPollResponse.getExPurch(TypeResponseExPurchase.CASHLESS));
        steps.hub().checkPollSuccessResults();
    }

    @Test
    public void testReRegistrationWithChangeFS(){
        String fnNum = manager.getConfigFields(FS_NUMBER).get(FS_NUMBER);
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

    //__________________________________________________________________________________________________________________
    //                                                 STEPS
    //__________________________________________________________________________________________________________________

    @Step("Проверка результатов POLL валидной регистрации")
    private void checkCorrectResultPoll(HubRequest resultPoll) {
        assertNotNull(resultPoll);
        assertNotNull(resultPoll.getData());
        assertNotNull(resultPoll.getData().getTaskResults());
        assertEquals(resultPoll.getData().getTaskResults().length, 1);
        assertNotNull(resultPoll.getData().getTaskResults()[0]);
        assertNotNull(resultPoll.getData().getTaskResults()[0].getResult());
        assertEquals(resultPoll.getData().getTaskResults()[0].getResult(), "SUCCESS");
    }

    @Step("Проверка результатов POLL невалидной регистрации")
    private void checkIncorrectResultPoll(HubRequest resultPoll, String messageError) {
        assertNotNull(resultPoll);
        assertNotNull(resultPoll.getData());
        assertNotNull(resultPoll.getData().getTaskResults());
        assertEquals(resultPoll.getData().getTaskResults().length, 1);
        assertNotNull(resultPoll.getData().getTaskResults()[0]);
        assertNotNull(resultPoll.getData().getTaskResults()[0].getResult());
        assertNotNull(resultPoll.getData().getTaskResults()[0].getMessage());
        assertNotNull(resultPoll.getData().getTaskResults()[0].getErrorCode());
        assertEquals(resultPoll.getData().getTaskResults()[0].getResult(), "ERROR");
        assertEquals(resultPoll.getData().getTaskResults()[0].getMessage(), messageError);
        assertEquals(resultPoll.getData().getTaskResults()[0].getErrorCode(), messageError);
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
        Assert.assertNotNull(new Gson().fromJson(kktRegisterInfo, HubRequest.class).getUuid());
    }

    @Step
    private void checkCashInfoReport() {
        assertTrue(server.checkReceivedRequest(CASH_INFO_REPORT, 1), "JSON CASH_INFO_REPORT не был отправлен на HUB");
        String cashInfoReport = server.getLastRequest();
        System.out.println("\n" + "\n" + "\n" + cashInfoReport);
    }
}
