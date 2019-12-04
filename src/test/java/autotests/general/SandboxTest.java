package autotests.general;

import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import hub_emulator.json.HubRequest;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.mockserver.model.HttpRequest;
import org.testng.Assert;
import org.testng.annotations.Test;

@Log4j
public class SandboxTest extends BaseTestClass {

    @Test
    public void additionTest() {
        int result = add(1, 2);
        Assert.assertEquals(3, result);
    }

    @Test
    public void substractionTest() {
        int result = add(3, 1);
        Assert.assertEquals(2, result);
    }

    @Test
    public void problematicTest() {
        int result = add(add(0, 1), substract(10,1));
        Assert.assertEquals(9, result);
    }

    @Step("Сложение")
    private int add(int a, int b) {
        return a + b;
    }

    @Step("Вычитание")
    private int substract(int a, int b) {
        return a - b;
    }
        //@Step("Эксперимент")
        //private int doStuffWithHub(int a, int b) {
        //return a + b;
        //manager.executeSshCommand("killall fiscat");
        //manager.iAmHub(true);
        //manager.setPollTime(10);
        //steps.cab().connectToCabinet();
        //HttpRequest[] requests = server.getRequests();
        //HubRequest hr = server.getLastRequest(MethodsEnum.WHO_AM_I);
        //steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        //}
}
