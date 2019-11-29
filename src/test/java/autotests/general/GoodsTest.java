package autotests.general;

import autotests.BaseTestClass;
import hub_emulator.Server;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;

import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.AssertJUnit.assertEquals;

public class GoodsTest extends BaseTestClass {

    @BeforeClass
    public void beforeJsonCabinetTests() {
        //старт сервера
        server = new Server();
        clearGoodsDb();
        manager.iAmHub(true);
        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
    }

    @AfterClass
    public void afterJsonCabinetTests() {
        manager.iAmHub(false);
        server.stop();
    }

    @Test
    public void testLoadGoods() {
        steps.hub().answerPoll(getLoadGoodsTasks());
        assertEquals("15",  manager.getCountGoodsFromCashbox());
    }


    //__________________________________________________________________________________________________________________
    //________________________________________________STEPS ____________________________________________________________

    @Step
    private void clearGoodsDb() {
        String DELETE_GOODS = "echo \"attach '/FisGo/goodsDb.db' as GOODS; delete from goods.GOODS;\" " +
                "| sqlite3 /FisGo/goodsDb.db";
        String DELETE_GOODS_CODE = "echo \"attach '/FisGo/goodsDb.db' as GOODS_CODE;" +
                " delete from goods_code.GOODS_CODE;\" " +
                "| sqlite3 /FisGo/goodsDb.db";

        manager.executeSshCommand(DELETE_GOODS);
        manager.executeSshCommand(DELETE_GOODS_CODE);
    }

    private HubRequest getLoadGoodsTasks() {
        TaskResults[] tasks = new TaskResults[15];
        for (int i = 0; i < tasks.length; i++) {
            List<String> articlesList = new ArrayList<>();
            articlesList.add("11111" + i);
            PollTaskData pollTaskData = PollTaskData.builder()
                    .remId("c4b61c56-f199-4420-9e12-aa1c33e869c" + i)
                    .articles(articlesList)
                    .name("Товар " + i)
                    .price(1000)
                    .tax("NDS_NO_TAX")
                    .type("COUNTABLE")
                    .measure("шт")
                    .build();
            tasks[i] = TaskResults.builder().taskId(i + 1).data(pollTaskData).taskType("upsert_product").build();
        }
        HubData data = HubData.builder().task(tasks).build();
        return HubRequest.builder().result("ОК").data(data).build();
    }

}
