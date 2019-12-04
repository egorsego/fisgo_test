package autotests.kassaf;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import autotests.BaseTestClass;
import hub_emulator.Server;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.update.Parameters;
import hub_emulator.response.enums.MethodsEnum;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static hub_emulator.response.enums.MethodsEnum.UPDATE_BACKUP;
import static org.testng.Assert.assertTrue;

public class BackupTest extends BaseTestClass {

    private String uuid;
    private String fnNumber;

    private String commandClearFactoryFields = "echo \"attach '/FisGo/configDb.db' as CONFIG;" +
            "update application_config.CONFIG set ARTICLE = '';" +
            "update application_config.CONFIG set UUID = '';" +
            "update application_config.CONFIG set KKT_PLANT_NUM = '';\" | sqlite3 configDb.db";

    @BeforeClass
    public void before() {
        uuid = manager.getConfigFields(ConfigFieldsEnum.UUID).get(ConfigFieldsEnum.UUID);
        fnNumber = manager.getConfigFields(ConfigFieldsEnum.FS_NUMBER).get(ConfigFieldsEnum.FS_NUMBER);
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.shift().closeShift();
//        regNum = manager.getRegNumKKT();
//        hubQueue.addResponse(KKT_REGISTER_INFO, RepositoryRegistrationResponse.getKktRegCorrect(regNum));
//        hub.answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
//        hub.checkPollSuccessResults();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void testBackup() {
        HubRequest response = HubRequest.builder()
                .data(HubData.builder()
                        .url(Server.getUrl().toString() + MethodsEnum.UPLOAD_BACKUP_FILE.getPath())
                        .parameters(Parameters.builder()
                                .ssuk("wqeqweqweqweqweqwe21")
                                .build())
                        .build())
                .build();
        hubQueue.addResponse(UPDATE_BACKUP, response);
        steps.shift().openShift();

        //TODO проверка запроса
        assertTrue(server.checkReceivedRequest(UPDATE_BACKUP, 1));
        server.clearRequests(UPDATE_BACKUP);
        //TODO проверка файла есть ли он, возможно проверка мд5


        HubRequest responseUploadFile = HubRequest.builder()
                .data(HubData.builder()
                        .uuid(uuid)
                        .fnNumber(fnNumber)
                        .url(Server.getUrl().toString() + MethodsEnum.UPLOAD_BACKUP_FILE.getPath())
                        .parameters(Parameters.builder()
                                .ssuk("wqeqweqweqweqweqwe21")
                                .build())
                        .build())
                .build();
        hubQueue.addResponse(UPDATE_BACKUP, response);

        manager.executeSshCommand(commandClearFactoryFields);
        manager.rebootFiscat();
        manager.iAmHub(true);
        assertTrue(server.checkReceivedRequest(UPDATE_BACKUP, 1));


        while (true) ;
    }

}
