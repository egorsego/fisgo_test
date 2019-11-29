package hub_emulator.response.repository;

import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.poll.PollTaskData;
import hub_emulator.json.poll.TaskResults;
import hub_emulator.json.purchase.*;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.enums.TypeResponseExPurchase;
import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static hub_emulator.response.enums.TypeResponseExPurchase.*;
import static hub_emulator.response.enums.TypeResponseExPurchase.CONSIDERATION;

@Log4j
public class RepositoryPollResponse {

    public static int taskId = 1;

    //REGISTRATION
    public static HubRequest getRegistration(RegistrationTypeEnum type) {
        PollTaskData pollTaskData = PollTaskData.builder()
                .type(type.toString())
                .cashier(
                        Cashier.builder().inn("720700677760").name("Кассир").build())
                .build();

        TaskResults tasks = TaskResults.builder()
                .taskId(1).taskType("registration").data(pollTaskData).result("OK")
                .build();

        HubData hubData = HubData.builder().task(new TaskResults[]{tasks}).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CLOSE_FN
    public static HubRequest getCloseFn(RegistrationTypeEnum type, String fnNum) {
        PollTaskData pollTaskData = PollTaskData.builder()
                .type(type.toString())
                .fnNumber(fnNum)
                .cashier(
                        Cashier.builder().inn("720700677760").name("Кассир").build())
                .build();

        TaskResults tasks = TaskResults.builder()
                .taskId(1).taskType("registration").data(pollTaskData).result("OK")
                .build();

        HubData hubData = HubData.builder().task(new TaskResults[]{tasks}).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //ACTIVATE_KEY
    public static HubRequest getActivateKey(String kktPlantNum) {
        String path = "";
        switch (kktPlantNum) {
            //если дкф
            //case "0498010011":
            case "0496040818":
                path = "./src/main/resources/hub-responses/license-keys/licenseKeyDKF";
                break;
            //если кф
            case "0497124323":
                path = "./src/main/resources/hub-responses/license-keys/licenseKeyKF";
                break;
            default:
                log.error("НЕВОЗМОЖНО АКТИВИРОВАТЬ КЛЮЧ (НЕИЗВЕСТНЫЙ ЗН ККТ ->" + kktPlantNum + ")");
        }

        String activateKey = null;
        try {
            activateKey = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            log.error("IOException", e);
        }

        PollTaskData pollTaskData = PollTaskData.builder().key(activateKey).remId("1").build();
        TaskResults[] tasks = new TaskResults[]{TaskResults.builder()
                .taskId(1)
                .data(pollTaskData)
                .taskType("key_activation")
                .build()};

        HubData hubData = HubData.builder().task(tasks).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //INVALID_KEY
    public static HubRequest getInvalidKey() {
        PollTaskData pollTaskData = PollTaskData.builder().key("123123").remId("1").build();
        TaskResults[] tasks = new TaskResults[]{TaskResults.builder()
                .taskId(1)
                .data(pollTaskData)
                .taskType("key_activation")
                .build()};

        HubData hubData = HubData.builder().task(tasks).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //__________________________________________________________________________________________________________________
    //                                           EXTERNAL_PURCHASE
    //__________________________________________________________________________________________________________________

    //WITH TYPE
    public static HubRequest getExPurch(TypeResponseExPurchase typeResponseExPurchase) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_10").taxSum(0)
                .build());

        Total total = Total.builder()
                .totalSum(2000)
                .taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[1];

        tasks[0] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, typeResponseExPurchase, "54651022bffebc03098b4561"))
                .taskType("external_purchase").build();

        HubData hubData = HubData.builder().task(tasks).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //WITH ALL TYPE
    public static HubRequest getWithAllTypes() {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(1000).total(2000).tax("NDS_10").taxSum(182)
                .build());
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(2000).total(4000).tax("NDS_0").taxSum(4000)
                .build());
        positions.add(Positions.builder()
                .name("Товар").type("SCALABLE").quantity(2345).price(3000).total(7035).tax("NDS_18").taxSum(9999999)
                .build());
        positions.add(Positions.builder()
                .name("Товар").type("COUNTABLE").quantity(2).price(2000).total(4000).tax("NDS_NO_TAX").taxSum(9999999)
                .build());


        Total total = Total.builder()
                .totalSum(17035).taxesSum(TaxesSum.builder().nds10(222).build())
                .build();

        TaskResults[] tasks = new TaskResults[5];

        tasks[0] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, CASHLESS, "54651022bffebc03098b4561"))
                .taskType("external_purchase").build();

        tasks[1] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, CASH, "54651022bffebc03098b4562"))
                .taskType("external_purchase").build();

        tasks[2] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, CREDIT, "54651022bffebc03098b4563"))
                .taskType("external_purchase").build();

        tasks[3] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, PREPAID, "54651022bffebc03098b4564"))
                .taskType("external_purchase").build();

        tasks[4] = TaskResults.builder().taskId(taskId++)
                .data(getPollTaskData(positions, total, CONSIDERATION, "54651022bffebc03098b4565"))
                .taskType("external_purchase").build();

        HubData hubData = HubData.builder().task(tasks).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    private static PollTaskData getPollTaskData(ArrayList<Positions> positions, Total total, TypeResponseExPurchase paymentsType, String remId) {
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

    public static HubRequest getPurchaseWithPositionTags(Tags... tags) {
        ArrayList<Positions> positions = new ArrayList<>();
        positions.add(Positions.builder()
                .name("Товар")
                .type("COUNTABLE")
                .quantity(2)
                .price(1000)
                .total(2000)
                .tax("NDS_20")
                .taxSum(0)
                .tags(new ArrayList<>(Arrays.asList(tags)))
                .build());


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
}
