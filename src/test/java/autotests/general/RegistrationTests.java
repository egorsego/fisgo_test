package autotests.general;

import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.api_manager.events.json.Event;
import application_manager.api_manager.events.json.data.EventData;
import application_manager.api_manager.events.json.data.PrinterData;
import application_manager.api_manager.json.response.data.Tag;
import autotests.BaseTestClass;
import com.google.gson.Gson;
import hub_emulator.json.HubRequest;
import hub_emulator.json.cash_info_report.KktInfo;
import hub_emulator.json.cash_info_report.Version;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.FS_NUMBER;
import static application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum.KKT_PLANT_NUM;
import static hub_emulator.response.enums.MethodsEnum.*;
import static org.testng.Assert.*;

public class RegistrationTests extends BaseTestClass {

    private HubRequest hubRequest;
    private String fnNum;

    private HubRequest responseChangeParLegalEntity;
    private HubRequest responseChangeOFD;
    private HubRequest responseChangeSettingKkt;
    private HubRequest responseChangeAllParameters;
    private HubRequest responseKktRegInfo;

    @BeforeClass
    public void before() {
        manager.getEventsContainer().clearLcdEvents();

        regNum = manager.getRegNumKKT();
        fnNum = manager.getConfigFields(FS_NUMBER).get(FS_NUMBER);

        initResponses();
    }

    @AfterClass
    public void after() {
        manager.iAmHub(false);
    }

    @Test
    public void testRegistrationFromCab() {
        steps.step().technicalZero();
        manager.iAmHub(true);
        manager.setPollTime(15);
        steps.cab().connectToCabinet();

        checkRegistration(responseKktRegInfo, RegistrationTypeEnum.REGISTRATION);
        checkRegistration(responseChangeParLegalEntity, RegistrationTypeEnum.CHANGE_PARAMETERS);
        checkRegistration(responseChangeOFD, RegistrationTypeEnum.CHANGE_PARAMETERS);
        checkRegistration(responseChangeSettingKkt, RegistrationTypeEnum.CHANGE_PARAMETERS);
        checkRegistration(responseChangeAllParameters, RegistrationTypeEnum.CHANGE_PARAMETERS);
    }

    @Test
    public void testRegistrationManual() {
        steps.step().technicalZero();
        goToRegistration();

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(2000);
        }

        //Шаг 1.
        dataTransferToOFD(false);
        //Шаг 2.
        inputINN();
        //Шаг 3.
        nameOrganization();
        //Шаг 4.
        addressCalculation();
        //Шаг 5.
        inputPlaceCalculation();
        //Шаг 6.
        inputRegNumKKT();
        //Шаг 7.
        inputVersionFFD();
        //Шаг 8.
        chooseSNO();
        //Шаг 9
        chooseSigns();
        //Шаг 10
        inputSenderEmail();
        //Отправка данных в ФНС
        sendDataToFNS();

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            checkPrintBufferForerunner();
            checkPrintBufferRegReport();
            checkFdTags();
        } else {
            manager.sleepPlease(10_000); //дождаться провиса после лоудера (
        }
    }

    //__________________________________________________________________________________________________________________
    //                                                   STEPS
    //__________________________________________________________________________________________________________________

    @Step
    private void checkFdTags() {
        List<Tag> docFromFs = manager.getDocFromFs(1);
        assertTrue(docFromFs.contains(Tag.builder().name("НАИМЕНОВАНИЕ ДОКУМЕНТА").tag(1000).value("ОТЧЁТ О РЕГИСТРАЦИИ").build()));
        //assertTrue(docFromFs.contains(Tag.builder().name("ФН").tag(1041).value("99990789395     ").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("РН ККТ").tag(1037).value(regNum + "    ").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ИНН").tag(1018).value("7802870820  ").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ФД").tag(1040).value(1.0).build()));
        // assertTrue(docFromFs.contains(Tag.builder().name("ДАТА, ВРЕМЯ").tag(1012).value("2018-11-22T13:29:00.000+03:00").build()));
        //assertTrue(docFromFs.contains(Tag.builder().name("ФП").tag(1077).value("2008074949").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ШФД").tag(1056).value(0.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("АВТОНОМН. РЕЖИМ").tag(1002).value(1.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("АВТОМАТ. РЕЖИМ").tag(1001).value(0.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ККТ ДЛЯ УСЛУГ").tag(1109).value(1.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("АС БСО").tag(1110).value(0.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ККТ ДЛЯ ИНТЕРНЕТ").tag(1108).value(0.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("СНО").tag(1062).value(63.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("НОМЕР ВЕРСИИ ФФД").tag(1209).value(2.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ФФД ККТ").tag(1189).value(2.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ПРИНТЕР В АВТОМАТЕ").tag(1221).value(0.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ПРОВЕДЕНИЕ ЛОТЕРЕИ").tag(1126).value(1.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ПОДАКЦИЗНЫЕ ТОВАРЫ").tag(1207).value(1.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ПРОВЕДЕНИЕ АЗАРТНОЙ ИГРЫ").tag(1193).value(1.0).build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ВЕР. ККТ").tag(1188).value("001").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ЭЛ.АДР.ОТПРАВИТЕЛЯ").tag(1117).value("kassa@dreamkas.ru").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("НАИМЕНОВАНИЕ ПОЛЬЗОВАТЕЛЯ").tag(1048).value("ООО \"Образец\"").build()));
        assertTrue(docFromFs.contains(Tag.builder().name("ЗН ККТ").tag(1013).value(manager.getConfigFields(KKT_PLANT_NUM).get(KKT_PLANT_NUM)).build()));
    }

    @Step
    private void checkPrintBufferRegReport() {
        List<Event> eventsPrinterList = manager.getEventsContainer().getEventsMap().get(EventType.PRINTER);
        String regRep = eventsPrinterList.get(eventsPrinterList.size() - 1).getData().getPrinterData().getPrintBuffer().get(0);
        LinkedList<String> regRepList = new LinkedList<>(Arrays.asList(regRep.split("\n")));
        regRepList.removeFirst();
        regRepList.removeLast();
        regRepList.removeLast();
        String expectedRegRep = "ИНН 7802870820\n" +
                "РН ККТ " + regNum + "\n" +
                "АВТОНОМН. РЕЖИМ \n" +
                "ПОДАКЦИЗНЫЕ ТОВАРЫ\n" +
                "ККТ ДЛЯ УСЛУГ\n" +
                "ПРОВЕДЕНИЕ АЗАРТНОЙ ИГРЫ\n" +
                "ПРОВЕДЕНИЕ ЛОТЕРЕИ\n" +
                "БАНК. ПЛ. АГЕНТ\n" +
                "БАНК. ПЛ. СУБАГЕНТ\n" +
                "ПЛ. АГЕНТ\n" +
                "ПЛ. СУБАГЕНТ\n" +
                "КОМИССИОНЕР\n" +
                "ПОВЕРЕННЫЙ\n" +
                "АГЕНТ\n" +
                "ЗН ККТ " + manager.getConfigFields(KKT_PLANT_NUM).get(KKT_PLANT_NUM) + "\n" +
                "ВЕР. ККТ 001\n" +
                "ФФД ККТ 1.05\n" +
                "КАССИР Админ\n" +
                "МЕСТО РАСЧЕТОВ\n" +
                "Ф\n" +
                "ЭЛ. АДР. ОТПРАВИТЕЛЯ\n" +
                "kassa@dreamkas.ru\n" +
                "СНО\n" +
                "ОСН\n" +
                "УСН доход\n" +
                "УСН доход-расход\n" +
                "ЕНВД\n" +
                "ЕСХН\n" +
                "Патент\n";

        LinkedList<String> expRegRepList = new LinkedList<>(Arrays.asList(expectedRegRep.split("\n")));
        assertEquals(regRepList, expRegRepList);
    }

    @Step
    private void sendDataToFNS() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_INFO, "Проверьте данные",
                    "Отправить", "регистрационные", "данные в ФНС?");
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.sleepPlease(3000);
    }

    @Step
    private void checkPrintBufferForerunner() {
        assertTrue(manager.getEventsContainer().getEventsMap().get(EventType.PRINTER)
                .contains(
                        Event.builder()
                                .type(EventType.PRINTER)
                                .data(EventData.builder().printerData(PrinterData.builder()
                                        .printBuffer(new ArrayList<String>() {{
                                            add("Шаг 1/10\n" +
                                                    "Передача данных в ОФД: \n" +
                                                    "Выкл\n\n" +
                                                    "Шаг 2/10\n" +
                                                    "ИНН организации: 7802870820\n\n" +
                                                    "Шаг 3/10\n" +
                                                    "Наименование организации: \n" +
                                                    "ООО \"Образец\"\n\n" +
                                                    "Шаг 4/10\n" +
                                                    "Адрес осуществления расчётов: \n" +
                                                    "Санкт-Петербург, ул. Мира 1\n\n" +
                                                    "Шаг 5/10\n" +
                                                    "Место осуществления расчётов: \n" +
                                                    "Ф\n\n" +
                                                    "Шаг 6/10\n" +
                                                    "Регистрационный номер ККТ: \n" +
                                                    regNum + "\n\n" +
                                                    "Шаг 7/10\n" +
                                                    "Версия ФФД: \n" +
                                                    "Версия 1.05\n\n\n" +
                                                    "Шаг 8/10\n" +
                                                    "Система налогообложения: \n" +
                                                    "ОСН\n" +
                                                    "УСН доход\n" +
                                                    "УСН доход-расход\n" +
                                                    "ЕНВД\n" +
                                                    "ЕСХН\n" +
                                                    "Патент\n\n" +
                                                    "Шаг 9/10\n" +
                                                    "Признаки \n\n" +
                                                    "Признак шифрования: \n" +
                                                    "Не используется\n\n" +
                                                    "Признак подакцизного товара: \n" +
                                                    "Используется\n\n" +
                                                    "Признак расчёта за услуги: \n" +
                                                    "Используется\n\n" +
                                                    "Признак проведения азартных игр:\n" +
                                                    "Используется\n\n" +
                                                    "Признак проведения лотереи: \n" +
                                                    "Используется\n\n" +
                                                    "Признак платёжного агента: \n" +
                                                    "Используется\n" +
                                                    "Банк.Пл.Агент\n" +
                                                    "Банк.Пл.С.агент\n" +
                                                    "Пл. агент\n" +
                                                    "Пл. Субагент\n" +
                                                    "Поверенный\n" +
                                                    "Комиссионер\n" +
                                                    "Агент\n\n\n" +
                                                    "Шаг 10/10\n" +
                                                    "Адрес электронной почты\n" +
                                                    "отправителя чека: kassa@dreamkas.ru\n\n\n\n\n\n");
                                        }})
                                        .build()).build())
                                .build()));
    }

    @Step
    private void inputSenderEmail() {
        manager.pressKey(KeyEnum.keyEnter);
        //Ввод email -> "P@P"

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.key1);
            manager.sendCommands();
            manager.pressKey(KeyEnum.key0, 2);
            manager.sendCommands();
            manager.pressKey(KeyEnum.key1);
            manager.pressKey(KeyEnum.key7);
            manager.pressKey(KeyEnum.key4);
            manager.sendCommands();
        }

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step
    private void chooseSigns() {
        manager.pressKey(KeyEnum.keyEnter);
        //Выбор всех признаков
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key6);
        manager.pressKey(KeyEnum.keyEnter);
        //Выбор всех агентов
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key4);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key6);
        manager.pressKey(KeyEnum.key7);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step
    private void chooseSNO() {
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.sendCommands();
    }

    @Step
    private void inputVersionFFD() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.keyEnter);
        } else {
            manager.pressKey(KeyEnum.keyEnter, 2);
        }
        manager.sendCommands();
    }

    @Step
    private void goToRegistration() {
        manager.pressKey(KeyEnum.keyMenu, 1);
        manager.pressKey(KeyEnum.key5, 1);
        manager.pressKey(KeyEnum.key1, 1);
        manager.pressKey(KeyEnum.key1, 1);
        manager.sendCommands();
    }

    @Step("Ввод регистрационного номера ККТ")
    private void inputRegNumKKT() {
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        String regNumKKT = manager.getRegNumKKT();
        manager.pressKey(regNumKKT);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Ввод места расчетов")
    private void inputPlaceCalculation() {
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.key1);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
            return;
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Название организации")
    private void addressCalculation() {
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.sendCommands();
    }

    @Step("Название организации")
    private void nameOrganization() {
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.sendCommands();
    }

    @Step("Ввести ИНН")
    private void inputINN() {
        manager.pressKey(KeyEnum.keyEnter);
        // ИНН-организации (стереть 012345567...)
        manager.pressKey(KeyEnum.keyReversal, 12);
        // Ввод ИНН
        manager.pressKey("7802870820");
        //Название организации -> Адрес расчетов ->
//        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
//            manager.pressKey(keyboard.getKeyValue(KeyEnum.keyEnter), 0, 1);
//        } else {
//            manager.pressKey(keyboard.getKeyValue(KeyEnum.keyEnter), 0, 3);
//        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Отправка данных в ОФД")
    private void dataTransferToOFD(boolean withOFD) {
        // Шаг 1/14 ВВедите передача данных в ОФД
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        if (withOFD) {
            // 1. Вкл
            manager.pressKey(KeyEnum.key2);
            manager.pressKey(KeyEnum.keyEnter);

            //Выбор ОФД
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.keyEnter);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
        } else {
            // 1. Выкл
            manager.pressKey(KeyEnum.key1);
            manager.pressKey(KeyEnum.keyEnter);
            manager.sendCommands();
        }
    }

    @Step
    private void checkRegistration(HubRequest responseKktRegInfo, RegistrationTypeEnum type) {
        server.clearRequests();

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.expectation().waitSendingAllDocsToOFD();
        }

        hubQueue.addResponse(KKT_REGISTER_INFO, responseKktRegInfo);

        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(type));

        //дождаться получения KKT_REGISTER_INFO
        checkKktRegisterInfo();

        //Дождаться REGISTRATION_REPORT
        checkRegistrationReport(responseKktRegInfo, type);

        //Дождаться CASH_INFO_REPORT
        checkCashInfoReport(responseKktRegInfo);
    }

    @Step
    private void checkKktRegisterInfo() {
        assertTrue(server.checkReceivedRequest(KKT_REGISTER_INFO, 1));
        String kktRegisterInfo = server.getLastRequest();
        assertTrue(steps.step().isJSONValid(kktRegisterInfo));
        hubRequest = new Gson().fromJson(kktRegisterInfo, HubRequest.class);
        assertNotNull(hubRequest.getUuid());
        //TODO добавить валидность на UUID
    }

    @Step
    private void checkCashInfoReport(HubRequest responseKktRegInfo) {
        assertTrue(server.checkReceivedRequest(CASH_INFO_REPORT, 1), "JSON CASH_INFO_REPORT не был отправлен на HUB");
        String cashInfoReport = server.getLastRequest();
        assertTrue(steps.step().isJSONValid(cashInfoReport));
        hubRequest = new Gson().fromJson(cashInfoReport, HubRequest.class);

        assertEquals(responseKktRegInfo.getData().getShopInfo(), hubRequest.getData().getShopInfo());

        String kktRegistryName = null;
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            kktRegistryName = "Касса Ф";
        }
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            kktRegistryName = "Дримкас-Ф";
        }

        assertEquals(KktInfo.builder()
                        .ffdVersion("1.05")
                        .fnRegistered(true)
                        .fnRegistryName("Шифровальное (криптографическое) средство защиты фискальных данных фискальный накопитель <ФН-1>")
                        .kktFactoryNumber(manager.getConfigFields(KKT_PLANT_NUM).get(KKT_PLANT_NUM))
                        .fnNumber(fnNum)
                        .kktRegistryName(kktRegistryName)
                        .build(),
                hubRequest.getData().getKktInfo());

        assertEquals(responseKktRegInfo.getData().getKktRegistrationInfo(),
                hubRequest.getData().getKktRegistrationInfo(),
                "Поле kktRegistrationInfo не соответствует ожидаемому");

        assertNotNull(hubRequest.getData().getLocalTime());

        assertNotNull(hubRequest.getData().getVersions());

        String product = getVersionProduct(cashBox.getBoxType());
        assertEquals(hubRequest.getData().getVersions().size(), 1);
        assertEquals(hubRequest.getData().getVersions().get(0), Version.builder()
                .product(product)
                .project("fisgo")
                .type("CASH")
                .build());
    }

    private String getVersionProduct(CashBoxType type) {
        if (type.equals(CashBoxType.KASSA_F)) {
            return "kassaf";
        }
        if (type.equals(CashBoxType.DREAMKAS_F)) {
            return "dreamkasFRedirect";
        }
        return null;
    }

    @Step
    private void checkRegistrationReport(HubRequest responseKktRegInfo, RegistrationTypeEnum type) {
        assertTrue(server.checkReceivedRequest(REGISTRATION_REPORT, 1), "JSON REGISTRATION_REPORT не был отправлен на HUB");
        String registrationReport = server.getLastRequest();
        assertTrue(steps.step().isJSONValid(registrationReport));
        hubRequest = new Gson().fromJson(registrationReport, HubRequest.class);

        assertNotNull(hubRequest.getData().getRegistrationData(),
                "Отсутствует поле RegistrationData");

        assertNotNull(hubRequest.getData().getRegistrationData().getShopInfo(),
                "Отстутствует поле ShopInfo");

        assertEquals(responseKktRegInfo.getData().getShopInfo(),
                hubRequest.getData().getRegistrationData().getShopInfo(),
                "Поле ShopInfo не соответсвтует ожидаемому");

        assertNotNull(hubRequest.getData().getRegistrationData().getKktRegistrationInfo(),
                "Отстутствует поле RegistrationInfo");

        assertNotNull(hubRequest.getData().getRegistrationData().getKktRegistrationInfo().getAgents(),
                "Отстутствует поле Agents");

        assertEquals(hubRequest.getData().getRegistrationData().getKktRegistrationInfo().getAgents(),
                responseKktRegInfo.getData().getKktRegistrationInfo().getAgents(),
                "Отстутствует поле Agents");

        assertEquals(responseKktRegInfo.getData().getKktRegistrationInfo(),
                hubRequest.getData().getRegistrationData().getKktRegistrationInfo(),
                "Поле kktRegistrationInfo не соответствует ожидаемому");

        assertNotNull(hubRequest.getData().getRegistrationData().getNumberFd(),
                "Отстутствует поле numberFd");

        assertNotNull(hubRequest.getData().getRegistrationData().getFiscalSign(),
                "Отстутствует поле fiscalSign");

        assertNotNull(hubRequest.getData().getDate(),
                "Отстутствует поле Date");

        assertNotNull(hubRequest.getData().getCashier(),
                "Отстутствует поле Cashier");

        assertEquals(hubRequest.getData().getType(), type.name(), "Поле type не соответствует ожидаемому");
    }

    private void initResponses() {
        responseKktRegInfo = RepositoryRegistrationResponse.getKktRegCorrect(regNum);
        responseChangeParLegalEntity = RepositoryRegistrationResponse.getKktRegChangeParLegEntity(regNum);
        responseChangeOFD = RepositoryRegistrationResponse.getKktRegChangeOfd(regNum);
        responseChangeSettingKkt = RepositoryRegistrationResponse.getKktRegChangeSettingKkt(regNum);
        responseChangeAllParameters = RepositoryRegistrationResponse.getKktRegChangeAllParameters(regNum);
    }
}