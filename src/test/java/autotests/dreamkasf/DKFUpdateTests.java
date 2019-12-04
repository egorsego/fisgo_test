package autotests.dreamkasf;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import hub_emulator.Server;
import hub_emulator.json.HubRequest;
import hub_emulator.json.update.Update;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Log4j
public class DKFUpdateTests extends BaseTestClass {

    private String newFisGoVersion;

    @BeforeClass
    public void before() {
        log.debug("Начало тестов обновления");
        manager.getEventsContainer().clearLcdEvents();

        manager.iAmHub(true);
        manager.setPollTime(10);

        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));

        newFisGoVersion = getNewVersion();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
        manager.executeSshCommand("rm /FisGo/test_update");
    }

    @Test
    public void testUpdateIfOpenShift() {
        server.clearRequests();
        manager.getEventsContainer().clearLcdEvents();
        deleteDownloadDir();

        steps.shift().openShift();

        hubQueue.addResponse(MethodsEnum.UPDATE_FISGO_VERSION, getUpdateResponse());
        checkUpdates();
        assertTrue(server.checkReceivedRequest(MethodsEnum.UPDATE_FISGO_VERSION, 1));

        steps.expectation().waitExpectedLcd("Доступна версия:", newFisGoVersion);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd("Закройте", "смену", "для обновления", "ПО");

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Test
    public void testUpdateIfShiftNotOpen() {
        server.clearRequests();
        manager.getEventsContainer().clearLcdEvents();
        steps.shift().closeShift();

        hubQueue.addResponse(MethodsEnum.UPDATE_FISGO_VERSION, getUpdateResponse());

        checkUpdates();
        assertTrue(server.checkReceivedRequest(MethodsEnum.UPDATE_FISGO_VERSION, 1));

        steps.expectation().waitExpectedLcd("Доступна версия:", newFisGoVersion);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        steps.expectation().waitExpectedLcd("Перезагрузка", "ККТ", "через", "1");
        manager.stop();

        manager.sleepPlease(25_000);

        manager.rebootFiscat();
        steps.step().inputPassword();

        List<String> list = manager.executeSshCommand("cat /FisGo/test_update");
        assertNotNull(list);
        assertNotNull(list.get(0));
        assertEquals(list.get(0), "123");
    }

    //__________________________________________________________________________________________________________________

    @Step("Переход в меню версия")
    private void checkUpdates() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key5);
        manager.sendCommands();
    }

    private HubRequest getUpdateResponse() {
        ArrayList<Update> updates = new ArrayList<>();
        updates.add(Update.builder()
                .version(newFisGoVersion)
                .barrier(true)
                .md5(getMd5Update())
                .size(123123)
                .url(Server.getUrl().toString() + MethodsEnum.DOWNLOAD_NEW_VERSION.getPath())
                .build());
        return HubRequest.builder()
                .updates(updates)
                .build();
    }

    private String getNewVersion() {
        String fisgoVersion = manager.getConfigFields(ConfigFieldsEnum.FISGO_VERSION).get(ConfigFieldsEnum.FISGO_VERSION);
        String[] split = fisgoVersion.split("\\.");
        Integer integer = Integer.valueOf(split[split.length - 1]);
        integer++;
        split[split.length - 1] = integer.toString();
        StringBuilder resultVersion = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            resultVersion.append(split[i]);
            if (i != split.length - 1) {
                resultVersion.append(".");
            }
        }
        return resultVersion.toString();
    }

    private void deleteDownloadDir() {
        manager.executeSshCommand("rm -r /download");
    }

    private String getMd5Update() {
        String filename = "./src/main/resources/update.tar.gz";

        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(filename)));
            byte[] digest = md.digest();
            result = DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
