package autotests.general;

import application_manager.api_manager.json.response.data.Tag;
import autotests.BaseTestClass;
import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.*;

import static hub_emulator.response.enums.MethodsEnum.POLL;
import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static hub_emulator.response.enums.TypeResponseExPurchase.CASHLESS;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ExternalPurchase1212Tag extends BaseTestClass {

    @BeforeClass
    public void beforeExternalPurchaseTests() {
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        steps.cab().connectToCabinet();
    }

    @AfterClass
    public void afterExternalPurchaseTests() {
        manager.iAmHub(false);
    }

    @Test(dataProvider = "value1212Tag", enabled = false)
    public void test1212Tag(Integer value) {
        server.clearRequests();
        steps.hub().answerPoll(RepositoryPollResponse.getPurchaseWithPositionTags(new Tags(1212, value)));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        server.getLastRequest(PURCHASE_DOCUMENT_REPORT);

        List<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());

        Map<String, Object> tags = new HashMap<>();
        for (Tag tag : docFromFs) {
            tags.put(tag.getTag().toString(), tag.getValue());
        }
        assertEquals(tags.get("1212"), String.valueOf(value));
    }

    @Test
    public void test1212TagIfValue15() {
        server.clearRequests();
        steps.hub().answerPoll(getExPurchWithTagsOnPositions(getTagsForValue15()));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
    }

    @Test
    public void test1212TagIfValue16() {
        server.clearRequests();
        steps.hub().answerPoll(getExPurchWithTagsOnPositions(getTagsForValue16()));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
    }

    @Test
    public void testNegativeUnknownValueTag1212() {
        server.clearRequests();
        steps.hub().answerPoll(RepositoryPollResponse.getPurchaseWithPositionTags(new Tags(1212, 300)));

        HubRequest lastRequest = server.getLastRequest(POLL);
        assertEquals(lastRequest.getData().getTaskResults().length, 1);
        for (int i = 0; i < lastRequest.getData().getTaskResults().length; i++) {
            assertEquals(lastRequest.getData().getTaskResults()[0].getResult(), "ERROR");
        }

    }

    @Test(enabled = false)
    public void testNegativeUnknowTag() {
        server.clearRequests();
        steps.hub().answerPoll(RepositoryPollResponse.getPurchaseWithPositionTags(new Tags(1212, 1),
                new Tags(1059, "1")));

        HubRequest lastRequest = server.getLastRequest(POLL);
        assertEquals(lastRequest.getData().getTaskResults().length, 1);
        for (int i = 0; i < lastRequest.getData().getTaskResults().length; i++) {
            assertEquals(lastRequest.getData().getTaskResults()[0].getResult(), "ERROR");
        }

        steps.hub().answerPoll(RepositoryPollResponse.getPurchaseWithPositionTags(new Tags(1212, 1)));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));

    }

    @Test
    public void testAllTags() {
        ArrayList<Tags> listTags = new ArrayList<>();

        for (int i = 1; i < 20; i++) {
            if (i == 15 || i == 16 || i == 19) {
                continue;
            }
            listTags.add(new Tags(1212, i));
        }

        Tags[] tags = listTags.toArray(new Tags[listTags.size()]);
        steps.hub().answerPoll(getExPurchWithTagsOnPositions(tags));
        steps.hub().checkPollSuccessResults();

        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, 1));
        server.getLastRequest(PURCHASE_DOCUMENT_REPORT);

        //TODO
//        ListScreen<Tag> docFromFs = manager.getDocFromFs(manager.getLastFdNum());
//        Map<String, String> tags = new HashMap<>();
//        for (Tag tag : docFromFs) {
//            tags.put(tag.getTag().toString(), tag.getValue());
//        }
//        assertEquals(tags.get("1212"), String.valueOf(value));
    }

    //__________________________________________________________________________________________________________________
    //__________________________________________________________________________________________________________________

    @DataProvider
    public Object[] value1212Tag() {
        return new Object[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 17, 18, 19};
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

    private HubRequest getExPurchWithTagsOnPositions(Tags... tags) {
        ArrayList<Positions> positions = new ArrayList<>();

        for (Tags tag : tags) {
            positions.add(Positions.builder()
                    .name("Товар")
                    .type("COUNTABLE")
                    .quantity(2)
                    .price(1000)
                    .total(2000)
                    .tax("NDS_20")
                    .taxSum(0)
                    .tags(new ArrayList<>(Collections.singletonList(tag)))
                    .build());
        }

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        for (int i = 1; i < tasks.length + 1; i++) {
            tasks[i - 1] = TaskResults.builder().taskId(RepositoryPollResponse.taskId++)
                    .data(getPollTaskData(positions, total, CASHLESS, "54651022bffebc03098b456" + i))
                    .taskType("external_purchase").build();
        }

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();

    }

    private PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total, TypeResponseExPurchase paymentsType, String remId) {
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(17035).type(paymentsType).build());
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

    private ArrayList[] getTagsForValue15() {
        ArrayList[] arrTags = new ArrayList[25];
        for (int i = 1; i < arrTags.length + 1; i++) {
            ArrayList<Tags> tags = new ArrayList<>();
            tags.add(new Tags(1212, 15));
            tags.add(new Tags(1030, String.valueOf(i)));
            arrTags[i - 1] = tags;
        }
        return arrTags;
    }

    private ArrayList[] getTagsForValue16() {
        ArrayList[] arrTags = new ArrayList[6];
        for (int value = 26, i = 0; value < 32; value++, i++) {
            ArrayList<Tags> tags = new ArrayList<>();
            tags.add(new Tags(1212, 16));
            tags.add(new Tags(1030, String.valueOf(value)));
            arrTags[i] = tags;
        }
        return arrTags;
    }

}
