package autotests.dreamkasf;

import application_manager.api_manager.events.json.data.lcdData.NotificationType;
import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;

import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static application_manager.api_manager.events.json.data.lcdData.NotificationType.NOTIFICATION_SALE_FINISH;
import static application_manager.api_manager.events.json.data.lcdData.NotificationType.NOTIFICATION_SALE_FINISH_SUM;
import static org.testng.Assert.assertTrue;

public class ReturnTest extends BaseTestClass {

    @BeforeClass
    public void beforeClass() {
        steps.shift().openShift();
        turnBankTerminal(true);
    }

    @AfterClass
    public void afterClass() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();
    }

    @Test
    public void returnReceipt() {
        steps.step().goToFreeMode();
        steps.payment().payOnePositionCash(1100);
        steps.payment().payOnePositionCard(100);

        //turnBankTerminal(true);
        goToReturnReceipt();
        for (int i = 1; i < 6; i++) {
            steps.payment().addPositions("100", 1);
            chooseMethodPay(i, true);

            if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                manager.pressKey(KeyEnum.keyEnter);
                manager.sendCommands();
            }
        }

        manager.pressKey(KeyEnum.keyMenu);
        manager.sendCommands();

        manager.getEventsContainer().clearLcdEvents();

        turnBankTerminal(false);
        goToReturnReceipt();
        for (int i = 1; i < 5; i++) {
            steps.payment().addPositions("100", 1);
            chooseMethodPay(i, false);

            if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                manager.pressKey(KeyEnum.keyEnter);
                manager.sendCommands();
            }
        }
    }

    /**
     * 1 - Наличные
     * 2 - Электронными
     * 3 - Аванс
     * 4 - В кредит
     * 5 - Иная форма
     */
    @Step("Выбрать способ оплаты")
    private void chooseMethodPay(int type, boolean isTerminalOn) {
        manager.pressKey(KeyEnum.keyEnter);
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            manager.pressKey(Integer.toString(type));
        }
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.pressKey(KeyEnum.keyDown, type - 1);
            manager.sendCommands();
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        if (isTerminalOn) {
            if (type == 1) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "прихода наличными", "на сумму?"),
                            "Нет экрана подтверждения возврата (наличные)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат",
                            "прихода наличными", "на сумму", "100 руб.");
                }
            }
            if (type == 2) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформите возврат", "на терминале,", "сумма:"),
                            "Нет экрана подтверждения возврата (электроные)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформите возврат", "на терминале,", "сумма:", "100 руб.");
                }
            }
            if (type == 3) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "аванса", "на сумму:"),
                            "Нет экрана подтверждения возврата (аванс)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "аванса", "на сумму:", "100 руб.");
                }
            }
            if (type == 4) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "кредита", "на сумму:"),
                            "Нет экрана подтверждения возврата (кредит)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "кредита", "на сумму:", "100 руб.");
                }
            }
            if (type == 5) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "иной формы", "оплаты на сумму:"),
                            "Нет экрана подтверждения возврата (иная форма)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "иной формы", "оплаты на сумму:", "100 руб.");
                }
            }
        } else {
            if (type == 1) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "прихода наличными", "на сумму?"),
                            "Нет экрана подтверждения возврата (наличные)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат",
                            "прихода наличными", "на сумму", "100 руб.");
                }
            }

            if (type == 2) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "аванса", "на сумму:"),
                            "Нет экрана подтверждения возврата (аванс)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "аванса", "на сумму:", "100 руб.");
                }
            }
            if (type == 3) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "кредита", "на сумму:"),
                            "Нет экрана подтверждения возврата (кредит)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "кредита", "на сумму:", "100 руб.");
                }
            }
            if (type == 4) {
                if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
                    assertTrue(manager.getEventsContainer().isContainsLcdEvents("Оформить возврат", "иной формы", "оплаты на сумму:"),
                            "Нет экрана подтверждения возврата (иная форма)");
                } else {
                    steps.expectation().waitNotificationScreen(NotificationType.NOTIFICATION_SALE_CONFIRM, "Оформить возврат", "иной формы", "оплаты на сумму:", "100 руб.");
                }
            }
        }

        manager.getEventsContainer().clearLcdEvents();

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();

        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            while (manager.getLoaderStatus()) {
                manager.sleepPlease(1001);
            }
        } else {
            if (isTerminalOn && type == 2) {
                steps.expectation().waitNotificationScreen(NOTIFICATION_SALE_FINISH, "Отдайте",
                        "покупателю", "чек и карту");
            } else {
                steps.expectation().waitNotificationScreen(NOTIFICATION_SALE_FINISH_SUM, "Отдайте", "покупателю чек",
                        "возврата и сумму", "100 руб.");
            }
        }

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        manager.sleepPlease(1500);
    }

    @Step("Перейти в пункт меню возврат")
    private void goToReturnReceipt() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key3);
        manager.pressKey(KeyEnum.key2);
        manager.pressKey(KeyEnum.key1);
        manager.sendCommands();
        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.expectation().waitUpdateLcd();
        }
    }

    @Step("Включить банковский терминал")
    private void turnBankTerminal(boolean isOn) {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key5);
        manager.pressKey(KeyEnum.key1);
        if (isOn) {
            manager.pressKey(KeyEnum.key2);
        } else {
            manager.pressKey(KeyEnum.key1);
        }
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }
}
