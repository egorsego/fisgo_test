package autotests.general;

import autotests.BaseTestClass;
import com.google.gson.Gson;
import hub_emulator.json.purchase.Attributes;
import hub_emulator.json.purchase.Payments;
import hub_emulator.json.purchase.Tags;
import hub_emulator.json.purchase.Total;
import hub_emulator.response.enums.TypeResponseExPurchase;
import kabinet_api.KabinetAPI;
import kabinet_api.json.Position;
import kabinet_api.json.Receipt;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Log4j
public class ExternalPurchaseWithKabinetAPITest extends BaseTestClass {

    private KabinetAPI kab;
    private Integer deviceId;

    @BeforeClass
    public void before() {
        kab = new KabinetAPI();
        manager.enableBetaHub(true);
        steps.step().inputPassword();
        manager.setPollTime(5);
        steps.cab().connectToCabinet(kab.getCodeFromKabinet());
        steps.shift().openShift();
        deviceId = kab.getDeviceId();
        manager.sleepPlease(5000);
    }

    @AfterClass
    public void after() {
        steps.cab().disconnectCabinet();
        manager.enableBetaHub(false);
        steps.step().inputPassword();
    }

    @Test
    public void testTimeExecutionExternalPurchase() {
        ArrayList<Long> allTimes = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            allTimes.add(getExecutionTime(15));
        }

        long averageTime = getAverageTime(allTimes);
        System.out.println("СРЕДНЕЕ ВРЕМЯ ВЫПОЛНЕНИЯ ОПЕРАЦИИ - " + averageTime + " сек.");
        assertTrue(averageTime <= 100,
                "Среднее время выполнения операции фискализации внешних чеков больше 100 секунд");
    }

    //__________________________________________________________________________________________________________________
    //__________________________________________________________________________________________________________________

    /**
     * Метод отправляет чеки в очередь, проверяет статус их фискализации и возвращает время выполнения операции
     *
     * @param countReceipts - количество чеков, которое необходимо отправить
     * @return - время в секундах
     */
    private long getExecutionTime(int countReceipts) {
        long startTime = System.nanoTime();
        ArrayList<String> listId = new ArrayList<>();
        for (int i = 0; i < countReceipts; i++) {
            sendReceipts(listId);
        }
        assertTrue(checkOperationStatus(listId), "Операции не были успешно выполнены");
        long estimate = (System.nanoTime() - startTime) / 1_000_000_000;
        System.out.println("время: " + estimate + " сек.");
        return estimate;
    }

    /**
     * Метод отправляет чеки в очередь и записывает в лист id операции
     *
     * @param listId - лист с id операции, необходимый для проверки статуса операций
     */
    private void sendReceipts(ArrayList<String> listId) {
        Gson gson = new Gson();
        System.out.println(gson.toJson(getReceipt()));


        String id = kab.sendReceipt(getReceipt());
        assertNotNull("Чек не добавился в очередь", id);
        listId.add(id);
    }

    /**
     * Метод проверки статуса операций.
     *
     * @param listId - лист с id операций
     * @return false - если хотя бы по одному из id вернется статус ERROR, true - если всё ок.
     */
    private boolean checkOperationStatus(ArrayList<String> listId) {
        int count = 0;
        String operationStatus;
        List<String> toRemove;

        for(String s : listId){
            System.out.println(s);
        }

        while (!listId.isEmpty()) {
            if (count == 60) {
                return false;
            }

            toRemove = new ArrayList<>();
            for (String id : listId) {
                operationStatus = kab.getOperationStatus(id);
                if (operationStatus.equals("SUCCESS")) {
                    toRemove.add(id);
                } else if (operationStatus.equals("ERROR")) {
                    return false;
                }
            }
            listId.removeAll(toRemove);

            log.info("Колличество чеков, которые еще не получили статус SUCCESS -> " + listId.size());

            manager.sleepPlease(2000);
            count++;
        }
        return true;
    }

    /**
     * Метод вычисления среднего времени вычисления операций
     *
     * @param allTimes - список со временем выполнения операций
     * @return - среднее время
     */
    private long getAverageTime(ArrayList<Long> allTimes) {
        long a = 0;
        for (Long time : allTimes) {
            a += time;
        }
        a = a / allTimes.size();
        return a;
    }

    /**
     * Метод возвращает тестовый чек на 30 позиций
     */
    private Receipt getReceipt() {
        String typeReceipt = "SALE";
        String taxMode = "SIMPLE_WO";
        ArrayList<Tags> tags = new ArrayList<>();

        //---------------------------------------------------------------------------------
        ArrayList<Position> positions = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            positions.add(Position.builder()
                    .name("Услуга - " + i)
                    .type("SERVICE")
                    .quantity(1)
                    .price(1000)
                    .tax("NDS_NO_TAX")
                    .tags(tags)
                    .build());
        }
        //---------------------------------------------------------------------------------
        ArrayList<Payments> payments = new ArrayList<>();
        payments.add(Payments.builder().sum(1790989).type(TypeResponseExPurchase.CASHLESS).build());
        //---------------------------------------------------------------------------------
        Attributes attributes = Attributes.builder().email("g.glushkov@dreamkas.ru").build();
        //---------------------------------------------------------------------------------
        Total total = Total.builder().priceSum(30_000).build();
        //---------------------------------------------------------------------------------


        return Receipt.builder().deviceId(deviceId).timeout(5).type(typeReceipt).taxMode(taxMode).positions(positions)
                .payments(payments).attributes(attributes).total(total).tags(tags).build();
    }

}
