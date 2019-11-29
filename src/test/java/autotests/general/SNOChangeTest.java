package autotests.general;

import application_manager.api_manager.json.request.data.enums.ConfigFieldsEnum;
import application_manager.cashbox.KeyEnum;
import autotests.BaseTestClass;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

@Log4j
public class SNOChangeTest extends BaseTestClass {

    @BeforeClass
    public void prepare() {
        openShift();
    }

    @AfterClass
    public void cleanUp() {
        closeShift();
    }

    @DataProvider (name = "taxSystemProvider")
    public Object [][] provideTaxSystemInfo() {
        return new Object[][] {{"ОСН", 1, 1}, {"УСН доход", 2, 2}, {"УСН дох./расх.", 3, 4}, {"ЕНВД", 4, 8}, {"ЕСХН", 5, 16}, {"Патент", 6, 32}};
    }

    @Test (dataProvider = "taxSystemProvider")
    public void true1(String taxSystem, int taxSystemMenuItemNumber, int taxSystemDbCode) {
        log.info("Тип СНО текущего теста: " + taxSystem);
        chooseTaxSystemOnCashbox(taxSystemMenuItemNumber);
        int currentTaxSystemCodeInDb = getCurrentTaxSystemCodeInConfigDb();
        Assert.assertEquals(currentTaxSystemCodeInDb, taxSystemDbCode);

        performIncomeOperation(500.50);
        performExpenseOperation(220.95);
        performReturnOfIncomeOperation(115.64);
        performReturnOfExpenseOperation(84.27);
    }

    @Step("Открытие смены")
    private void openShift() {
        manager.pressKey(KeyEnum.key1, 2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();
    }

    @Step("Закрытие смены")
    private void closeShift() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();
    }

    @Step("Выбор типа системы налогооблажения")
    private void chooseTaxSystemOnCashbox(int taxSystemItemNumber) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key7);
        manager.pressKey(String.valueOf(taxSystemItemNumber));
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.pressKey(KeyEnum.keyCancel,2);
        manager.sendCommands();
    }

    @Step("Получение кода текущей СНО в БД")
    private int getCurrentTaxSystemCodeInConfigDb() {
        Map<ConfigFieldsEnum, String> uuidConfigFields = manager.getConfigFields(ConfigFieldsEnum.CUR_TAX_SYSTEM);
        return Integer.parseInt(uuidConfigFields.get(ConfigFieldsEnum.CUR_TAX_SYSTEM));
    }

    @Step("Операция прихода")
    private void performIncomeOperation(double amount) {
        manager.pressKey(String.valueOf(amount));
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyPayByCash);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();
        manager.sleepPlease(5_000);
    }

    @Step("Операция расхода")
    private void performExpenseOperation(double amount) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(String.valueOf(amount));
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        steps.expectation().waitLoader();
        manager.sleepPlease(5_000);
        manager.pressKey(KeyEnum.keyCancel, 2);
        manager.sendCommands();
    }

    @Step("Операция возврата прихода")
    private void performReturnOfIncomeOperation(double amount) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(String.valueOf(amount));
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        steps.expectation().waitLoader();
        manager.sleepPlease(5_000);
        manager.pressKey(KeyEnum.keyCancel, 3);
        manager.sendCommands();
    }

    @Step("Операция возврата расхода")
    private void performReturnOfExpenseOperation(double amount) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(String.valueOf(amount));
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        steps.expectation().waitLoader();
        manager.sleepPlease(5_000);
        manager.pressKey(KeyEnum.keyCancel, 3);
        manager.sendCommands();
    }
}
