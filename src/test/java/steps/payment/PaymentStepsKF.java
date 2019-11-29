package steps.payment;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.Manager;
import application_manager.cashbox.CashBox;
import application_manager.cashbox.KeyEnum;
import application_manager.cashbox.Keyboard;
import hub_emulator.Server;
import hub_emulator.response.enums.TypeResponseExPurchase;
import hub_emulator.response.repository.RepositoryPollResponse;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j;
import steps.hubemul.HubEmulSteps;
import steps.wait.WaitSteps;

import static hub_emulator.response.enums.MethodsEnum.PURCHASE_DOCUMENT_REPORT;
import static org.testng.Assert.assertTrue;

@Log4j
public class PaymentStepsKF implements PaymentSteps {

    private Manager manager;
    private CashBox cashBox;
    private Keyboard keyboard;
    private Server server;
    private WaitSteps wait;
    private HubEmulSteps hub;

    public PaymentStepsKF(Manager manager, CashBox cashBox, Server server, WaitSteps wait, HubEmulSteps hub) {
        this.manager = manager;
        this.cashBox = cashBox;
        this.keyboard = cashBox.getKeyboard();
        this.server = server;
        this.wait = wait;
        this.hub = hub;
    }

    @Override
    @Step("Оплатить чек на одну позицию наличными")
    public void payOnePositionCash(int sum) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.keyCancel, 2);
        manager.sendCommands();
        manager.pressKey(String.valueOf(sum));
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        manager.sleepPlease(2000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(2000);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.sleepPlease(2000);
    }

    @Override
    @Step("Оплатить чек на одну позицию картой")
    public void payOnePositionCard(int sum) {
        manager.pressKey(String.valueOf(sum));
        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyPayByCard);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.sleepPlease(2000);
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(2000);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Override
    @Step("Продать товар из базы товаров")
    public void payGoodsFromDb(String code) {
        manager.pressKey(KeyEnum.keyGoods);
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.keyEnter);
        }
        manager.pressKey(code);
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.keyEnter);
        }
        manager.pressKey(KeyEnum.keyEnter, 4);
        manager.sendCommands();
        while (manager.getLoaderStatus()) {
            manager.sleepPlease(2000);
        }
    }

    @Override
    @Step("Добавить позиции в чек")
    public void addPositions(String sum, int count) {
        for (int i = 0; i < count; i++) {
            manager.pressKey(sum);
            manager.pressKey(KeyEnum.keyEnter);
        }
        manager.sendCommands();
    }

    @Override
    @Step("Завершить формирование чека и выполнить оплату")
    public void completePurchase() {
        manager.pressKey(KeyEnum.keyEnter, 3);
        manager.sendCommands();

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            wait.waitUpdateLcd();
        }

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Override
    public void completePurchase(PaymentsType paymentsType) {
        manager.pressKey(KeyEnum.keyEnter);
        switch (paymentsType) {
            case CASH:
                manager.pressKey(KeyEnum.key1);
                break;
            case CASHLESS:
                manager.pressKey(KeyEnum.key2);
                break;
            case PREPAID:
                manager.pressKey(KeyEnum.key3);
                break;
            case CREDIT:
                manager.pressKey(KeyEnum.key4);
                break;
            case CONSIDERATION:
                manager.pressKey(KeyEnum.key5);
                break;
            default:
                log.error("Необрабатываемый тип платежа");
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        while (manager.getLoaderStatus()) {
            manager.sleepPlease(1000);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Override
    @Step("Послать на кассу EXTERNAL_PURCHASE")
    public void sendExternalPurchase(TypeResponseExPurchase type) {
        if (type.equals(TypeResponseExPurchase.ALL)) {
            hub.answerPoll(RepositoryPollResponse.getWithAllTypes());
        } else {
            hub.answerPoll(RepositoryPollResponse.getExPurch(type));
        }
        int count = 1;
        if (type.equals(TypeResponseExPurchase.ALL)) {
            count = 5;
        }
        hub.checkPollSuccessResults();
        assertTrue(server.checkReceivedRequest(PURCHASE_DOCUMENT_REPORT, count));
    }
}
