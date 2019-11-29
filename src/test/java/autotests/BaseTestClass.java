package autotests;

import application_config.AppConfig;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.cashbox.CashBox;
import application_manager.api_manager.Manager;
import hub_emulator.Server;
import hub_emulator.response.ResponseHub;
import lombok.Getter;

import lombok.extern.log4j.Log4j;
import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import steps.Steps;

@Log4j
public class BaseTestClass {

    @Getter
    protected static CashBox cashBox;
    protected static Manager manager;
    protected static Server server;
    protected static ResponseHub hubQueue;
    protected static String regNum;

    public static Steps steps;
    public static String kktPlantNum;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        log.info("@BeforeSuite - начало тестов");
        init();
        startSettings();
    }

    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        manager.stop();
        server.stop();
        log.info("Teardown");
    }

    private void init() {
        server = new Server();
        hubQueue = server.getResponseHub();
        cashBox = new CashBox();
        log.info(cashBox);
        manager = new Manager(cashBox);
        setDefaultResponseFactoryFinish(manager.getCashBox().getBoxType());
        steps = new Steps(manager, server);
    }

    private void setDefaultResponseFactoryFinish(CashBoxType boxType) {
        switch (boxType) {
            case DREAMKAS_F:
                hubQueue.setResponseFactoryFinish("{}");
                break;
            default:
                hubQueue.setResponseFactoryFinish("{\"data\":{\"description\":{}}}");
        }
    }

    private void startSettings() {
        AppConfig config = ConfigFactory.create(AppConfig.class);
        if (config.startWithRebootFiscat()) {
            manager.executeSshCommand("rm /FisGo/outf");
            manager.rebootFiscat();
            //step.inputDataTime();
            steps.step().inputPassword();
        } else {
            manager.start();
        }

        //changeKktPlantNum();
        kktPlantNum = manager.getConfigFields(ConfigFieldsEnum.KKT_PLANT_NUM).get(ConfigFieldsEnum.KKT_PLANT_NUM);
        log.debug("Заводской номер кассы - " + kktPlantNum);
    }

    private void changeKktPlantNum() {
        switch (cashBox.getBoxType()) {
            case KASSA_F:
                kktPlantNum = "0497124323";
                break;
            case DREAMKAS_F:
                kktPlantNum = "0498010011";
                break;
            default:
                log.error("Невозможно изменить KKT_PLANT_NUM (неизвестный тип кассы ->" + cashBox.getBoxType());
        }

        String query = "echo \"attach '/FisGo/configDb.db' as CONFIG; update application_config.CONFIG set KKT_PLANT_NUM = '"
                + kktPlantNum + "';\" " + "| sqlite3 /FisGo/configDb.db";

        manager.executeSshCommand(query);

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.step().technicalZero();
        } else {
            manager.rebootFiscat();
            steps.step().inputPassword();
        }
    }
}
