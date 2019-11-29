package autotests.dreamkasf;

import application_manager.api_manager.CashBoxType;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.PrinterData;
import application_manager.api_manager.json.response.data.Tag;
import autotests.BaseTestClass;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.*;

import static org.testng.Assert.*;

public class CorrectionChequeTest extends BaseTestClass {

    private final Random random = new Random();
    private String date;
    private int initFdNum;

    @BeforeClass
    public void beforeCorrectionChequeTest() {
        System.out.println("Начало тестов чека коррекции");

        steps.shift().openShift();

        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));

        initFdNum = manager.getLastFdNum();

        String day = String.format("%02d", random.nextInt(28) + 1);
        String month = String.format("%02d", random.nextInt(12) + 1);
        String year = String.format("%02d", random.nextInt(99));
        date = day + "." + month + "." + year;
    }

    @AfterClass
    public void afterCorrectionChequeTest() {
        manager.iAmHub(false);
        System.out.println("Конец тестов чека коррекции");
    }

    @Test
    public void testCorrection() {
        server.clearRequests();
        goToDocuments();

        //Открытие чека коррекции
        openCorrection();
        //Тип чека коррекции
        inputType();
        //Система налогооблажения
        chooseSNO();
        //Наличными
        String cash = insertMoney();
        //Безналичными
        String cashless = insertMoney();
        //Предоплата
        String prepay = insertMoney();
        //Постоплата
        String postpay = insertMoney();
        //Встречным представлением
        String counteroffer = insertMoney();
        //Тип коррекции
        inputType();
        //Основание для коррекции
        String correctionBasis = insertString();
        //Дата документа основания
        insertDate();
        //Номер документа основания
        String baseNum = insertString();
        //Сумма по ставке 20%
        String twenty = insertMoney();
        //Сумма по ставке 10%
        String ten = insertMoney();
        //Сумма по ставке 0%
        String zero = insertMoney();
        //Сумма по ставке без налога
        String without = insertMoney();
        //Сумма по ставке 20/120
        String six = insertMoney();
        //Сумма по ставке 10/110
        String one = insertMoney();

        manager.sleepPlease(5000);

        checkPrintBuffer(cash, cashless, prepay, postpay, counteroffer, correctionBasis, baseNum,
                twenty, ten, zero, without, six, one);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        manager.sleepPlease(5000);

        checkDocumentFromFs(cash, cashless, prepay, postpay, counteroffer, correctionBasis, baseNum,
                twenty, ten, zero, without, six, one);
    }

    //__________________________________________________________________________________________________________________
    //                                                   STEPS
    //__________________________________________________________________________________________________________________

    @Step("переход в Документы")
    private void goToDocuments() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key2);
        manager.sendCommands();
    }

    @Step("Открытие чека коррекции")
    private void openCorrection() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.key4);
        } else {
            manager.pressKey(KeyEnum.key5);
        }

        manager.sendCommands();
    }

    @Step("Ввод типа коррекции")
    private void inputType() {
        manager.pressKey(KeyEnum.key1);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Ввод системы налогооблажения")
    private void chooseSNO() {
        manager.pressKey(KeyEnum.key1);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Указание денежного значения")
    private String insertMoney() {
        String money = setMoney();

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        return money;
    }

    private String setMoney() {
        String money = String.valueOf(random.nextInt(9) + 1);
        manager.pressKey(KeyEnum.valueOf("key" + money));

        manager.pressKey(KeyEnum.keyComma);
        money = money + ".";

        String second = String.valueOf(random.nextInt(10));
        manager.pressKey(KeyEnum.valueOf("key" + second));
        money = money + second;
        String last = String.valueOf(random.nextInt(9) + 1);
        manager.pressKey(KeyEnum.valueOf("key" + last));
        money = money + last;

        return money;
    }

    @Step("Ввод строки")
    private String insertString() {
        manager.pressKey(KeyEnum.keyReversal);
        manager.pressKey(KeyEnum.key0);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        return "@";
    }

    @Step("Ввод даты")
    private void insertDate() {
        manager.pressKey(date);

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step
    private void checkPrintBuffer(
            String cash, String cashless, String prepay, String postpay, String counteroffer, String correctionBasis,
            String baseNum, String twenty, String ten, String zero, String without, String six, String one
    ) {
        ArrayList<String> expPrinter = new ArrayList<>();
        expPrinter.add("\nШаг 1/17\n" +
                "Тип чека коррекции \n" +
                "Коррекция прихода \n\n" +
                "Шаг 2/17\n" +
                "Система налогооблажения: \n" +
                "Общая\n\n" +
                "Шаг 3/17\n" +
                "Наличными \n" +
                "         = " + cash + "\n\n" +
                "Шаг 4/17\n" +
                "Безналичными \n" +
                "         = " + cashless + "\n\n" +
                "Шаг 5/17\n" +
                "Аванс \n" +
                "         = " + prepay + "\n\n" +
                "Шаг 6/17\n" +
                "Кредит \n" +
                "         = " + postpay + "\n\n" +
                "Шаг 7/17\n" +
                "Встречным представлением \n" +
                "         = " + counteroffer + "\n\n" +
                "Шаг 8/17\n" +
                "Тип коррекции: \n" +
                "Самостоятельная\n\n" +
                "Шаг 9/17\n" +
                "Основание для коррекции: \n" +
                correctionBasis + "\n\n" +
                "Шаг 10/17\n" +
                "Дата основания для коррекции: \n" +
                date + "\n\n" +
                "Шаг 11/17\n" +
                "Номер документа основания для коррекции: \n" +
                baseNum + "\n\n" +
                "Шаг 12/17\n" +
                "СУММА НДС 20%       =       " + twenty + "\n" +
                "Шаг 13/17\n" +
                "СУММА НДС 10%       =       " + ten + "\n" +
                "Шаг 14/17\n" +
                "СУММА C НДС 0%      =       " + zero + "\n" +
                "Шаг 15/17\n" +
                "СУММА БЕЗ НДС       =       " + without + "\n" +
                "Шаг 16/17\n" +
                "СУММА НДС 20/120    =       " + six + "\n" +
                "Шаг 17/17\n" +
                "СУММА НДС 10/110    =       " + one + "\n\n\n\n");

        Event cheque = Event.builder()
                .type(EventType.PRINTER)
                .data(EventData.builder()
                        .printerData(PrinterData.builder()
                                .printBuffer(expPrinter)
                                .build())
                        .build())
                .build();
        Map<EventType, List<Event>> eventsMap = manager.getEventsContainer().getEventsMap();
        List<Event> eventsPrinter = eventsMap.get(EventType.PRINTER);
        assertTrue(eventsPrinter.contains(cheque));
    }

    @Step("Проверка запроса в кабинет")
    private void checkDocumentFromFs(
            String cash, String cashless, String prepay, String postpay, String counteroffer, String correctionBasis,
            String baseNum, String twenty, String ten, String zero, String without, String six, String one
    ) {
        int fdNum = manager.getLastFdNum();
        assertEquals(initFdNum + 1, fdNum);
        List<Tag> doc = manager.getDocFromFs(fdNum);
        Map<String, Object> tags = new HashMap<>();

        for (Tag tag : doc) {
            if (tag.getOfdQuittance() != null) {
                continue;
            }
            tags.put(tag.getTag().toString(), tag.getValue());
        }

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(tags.get("1000"), "КАССОВЫЙ ЧЕК КОРРЕКЦИИ", "Неверное значение тэга - 1000");
        softly.assertEquals(tags.get("1040"), Double.valueOf(fdNum), "Неверное значение тэга - 1040");
        softly.assertEquals(tags.get("1054"), 1.0, "Неверное значение тэга - 1054");
        Float sum = Float.valueOf(cash) + Float.valueOf(cashless) + Float.valueOf(prepay) +
                Float.valueOf(postpay) + Float.valueOf(counteroffer);
        softly.assertEquals(tags.get("1020"), Double.valueOf(Math.round(sum * 100)), "Неверное значение тэга - 1020");
        softly.assertEquals(tags.get("1173"), 0.0, "Неверное значение тэга - 1173");
        softly.assertNull(tags.get("1174"));
        softly.assertEquals(tags.get("1177"), correctionBasis, "Неверное значение тэга - 1177");
        softly.assertEquals(tags.get("1179"), baseNum, "Неверное значение тэга - 1179");
        softly.assertEquals(tags.get("1081"), Double.valueOf(Math.round(Float.valueOf(cashless) * 100)), "Неверное значение тэга - 1081");
        softly.assertEquals(tags.get("1031"), Double.valueOf(Math.round(Float.valueOf(cash) * 100)), "Неверное значение тэга - 1031");
        softly.assertEquals(tags.get("1215"), Double.valueOf(Math.round(Float.valueOf(prepay) * 100)), "Неверное значение тэга - 1215");
        softly.assertEquals(tags.get("1216"), Double.valueOf(Math.round(Float.valueOf(postpay) * 100)), "Неверное значение тэга - 1216");
        softly.assertEquals(tags.get("1217"), Double.valueOf(Math.round(Float.valueOf(counteroffer) * 100)), "Неверное значение тэга - 1217");
        softly.assertEquals(tags.get("1102"), Double.valueOf(Math.round(Float.valueOf(twenty) * 100)), "Неверное значение тэга - 1102");
        softly.assertEquals(tags.get("1103"), Double.valueOf(Math.round(Float.valueOf(ten) * 100)), "Неверное значение тэга - 1103");
        softly.assertEquals(tags.get("1104"), Double.valueOf(Math.round(Float.valueOf(zero) * 100)), "Неверное значение тэга - 1104");
        softly.assertEquals(tags.get("1105"), Double.valueOf(Math.round(Float.valueOf(without) * 100)), "Неверное значение тэга - 1105");
        softly.assertEquals(tags.get("1106"), Double.valueOf(Math.round(Float.valueOf(six) * 100)), "Неверное значение тэга - 1106");
        softly.assertEquals(tags.get("1107"), Double.valueOf(Math.round(Float.valueOf(one) * 100)), "Неверное значение тэга - 1107");
        softly.assertAll();
    }
}
