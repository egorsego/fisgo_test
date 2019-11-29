package hub_emulator.response.repository;

import hub_emulator.json.HubData;
import hub_emulator.json.HubRequest;
import hub_emulator.json.cash_info_report.Agent;
import hub_emulator.json.cash_info_report.KktRegistrationInfo;
import hub_emulator.json.cash_info_report.OfdProvider;
import hub_emulator.json.cash_info_report.ShopInfo;

import java.util.ArrayList;
import java.util.List;

public class RepositoryRegistrationResponse {

    //__________________________________________________________________________________________________________________
    //                                             KKT_REG_INFO
    //__________________________________________________________________________________________________________________

    //WITH_DEFAULT_TAX
    public static HubRequest getKktRegWithTax(String regNum) {

        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("Дримкас ОФД")
                .inn("7802870820")
                .serverHost("185.241.176.4")
                .serverPort(21101)
                .checkUrl("https://ofd.dreamkas.ru/check")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();

        return HubRequest.builder().data(hubData).result("OK").build();
    }

    public static HubRequest getKktRegWithNeedTaxMode(List<String> taxModes, String regNum){
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CORRECT
    public static HubRequest getKktRegCorrect(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CHANGE PARAM LEGAL ENTITY
    public static HubRequest getKktRegChangeParLegEntity(String regNum) {

        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("двоечка")
                .shopName("двоечка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ЛИСТИК\"")
                .address("новый адресс")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("INTERNET");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CHANGE PARAM OFD
    public static HubRequest getKktRegChangeOfd(String regNum) {

        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("двоечка")
                .shopName("двоечка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ЛИСТИК\"")
                .address("новый адресс")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamwwkasofd")
                .inn("7704215201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("INTERNET");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CHANGE PARAM SETTING KKT
    public static HubRequest getKktRegChangeSettingKkt(String regNum) {

        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("двоечка")
                .shopName("двоечка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ЛИСТИК\"")
                .address("новый адресс")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CHANGE_PARAMETERS_ALL
    public static HubRequest getKktRegChangeAllParameters(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("Новое название")
                .shopName("Новое название")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"FisGo Corporation\"")
                .address("новейший адресс")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .workMode(workMode)
                .ofdProvider(ofdProvider)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CHANGE_PARAMETERS_WITHOUT_SIGN_SERVICE
    public static HubRequest getKktRegWithoutServices(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //WITHOUT_DEFAULT_TAX
    public static HubRequest getKktRegWithoutDefaultTax(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();

        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //FOR_PULSE_CORRECT
    public static HubRequest getKktRegForPulse(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("AUTOMATIC");
        workMode.add("SERVICES");
        workMode.add("PAY_AGENTS");
        workMode.add("INTERNET");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .automaticDeviceNumber("12312312")
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //pulse change reg
    public static HubRequest getKktRegForPulseChange(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("новый магазин")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина 46")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("AUTOMATIC");
        workMode.add("SERVICES");
        workMode.add("PAY_AGENTS");
        workMode.add("INTERNET");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .automaticDeviceNumber("12312312")
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //FOR_PULSE_INCORRECT (without automaticDeviceNumber)
    public static HubRequest getKktRegForPulseIncorrect(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("AUTOMATIC");
        workMode.add("SERVICES");
        workMode.add("PAY_AGENTS");
        workMode.add("INTERNET");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //CORRECT
    public static HubRequest getKktForInternetShop(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(19086)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        // workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");
        workMode.add("INTERNET");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

    //C НЕВЕРНЫМ ОФД
    public static HubRequest getKktRegWithIncorrectOfd(String regNum) {
        ShopInfo shopInfo = ShopInfo.builder()
                .realAddress("пятерочка")
                .shopName("пятерочка")
                .legalName("ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"РОМАШКА\"")
                .address("Улица Пушкина дом колотушкrина")
                .inn("7802870820")
                .build();

        List<String> taxModes = new ArrayList<>();
        taxModes.add("DEFAULT");
        taxModes.add("SIMPLE");
        taxModes.add("SIMPLE_WO");
        taxModes.add("ENVD");
        taxModes.add("AGRICULT");
        taxModes.add("PATENT");

        OfdProvider ofdProvider = OfdProvider.builder()
                .name("dreamkasofd")
                .inn("7704211201")
                .serverHost("mail1.atlas-kard.ru")
                .serverPort(1922)
                .checkUrl("https://receipt.taxcom.ru")
                .build();

        List<String> workMode = new ArrayList<>();
        workMode.add("CIPHER");
        workMode.add("SERVICES");
        workMode.add("EXCISE");
        workMode.add("GAMBLING");
        workMode.add("LOTTERY");
        workMode.add("PAY_AGENTS");

        List<Agent> agents = new ArrayList<>();
        agents.add(new Agent("BANK_PAY_AGENT"));
        agents.add(new Agent("BANK_PAY_SUB_AGENT"));
        agents.add(new Agent("PAY_AGENT"));
        agents.add(new Agent("PAY_SUB_AGENT"));
        agents.add(new Agent("ATTORNEY"));
        agents.add(new Agent("COMMISSIONAIRE"));
        agents.add(new Agent("AGENT"));


        KktRegistrationInfo kktRegistrationInfo = KktRegistrationInfo.builder()
                .registryNumber(regNum)
                .taxModes(taxModes)
                .senderEmail("www.kassa@dreamkas.ru")
                .autonomic(false)
                .ofdProvider(ofdProvider)
                .workMode(workMode)
                .agents(agents)
                .build();

        HubData hubData = HubData.builder().shopInfo(shopInfo).kktRegistrationInfo(kktRegistrationInfo).build();
        return HubRequest.builder().data(hubData).result("OK").build();
    }

}
