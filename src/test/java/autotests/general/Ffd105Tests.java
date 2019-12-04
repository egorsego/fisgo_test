package autotests.general;

import application_manager.cashbox.KeyEnum;
import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.events.EventsContainer;
import autotests.BaseTestClass;
import hub_emulator.response.enums.RegistrationTypeEnum;
import hub_emulator.response.repository.RepositoryPollResponse;
import hub_emulator.response.repository.RepositoryRegistrationResponse;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static application_manager.api_manager.events.json.data.lcdData.NotificationType.NOTIFICATION_SALE_FINISH;
import static hub_emulator.response.enums.MethodsEnum.KKT_REGISTER_INFO;
import static org.testng.Assert.assertTrue;

public class Ffd105Tests extends BaseTestClass {

    @BeforeClass
    public void beforeClass() {
        manager.getEventsContainer().clearLcdEvents();
        regNum = manager.getRegNumKKT();
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            manager.clearDreamkasKey();
        }
        steps.step().technicalZero();
        manager.iAmHub(true);
        manager.setPollTime(10);
        steps.cab().connectToCabinet();
    }

    @AfterClass
    public void afterClass() {
        manager.iAmHub(false);
    }

    /**
     * Тест проверяет наличие ФФД 1.05 после активации ключа. Исходное состояние кассы - выполнено тех.обнуление, касса
     * подключена к эмулятору хаба, установлен polltime = 10, касса подключена к Кабинету
     * <p>
     * 1. Зарегистрировать кассу с ОСН
     * 2. Начать открывать смену
     * 3. Проверить, что при открытии смены возникает сообщение об именениях в законадательстве
     * 4. Прислать на кассу ключ активации лицензии
     * 5. Открыть смену
     * 6. Включить банковский терминал, чтобы в способах оплаты появился "Безналичные"
     * 7. Перейти в экран свободной продажи, добавить одну позицию и перейти на экран выбора способа платежа
     * 8. Проверить что на экране присутствует пункт "2. Безналичные" (без ключа "Электронные")
     * 9. Закончить оплату
     * 10. Проверить буфер печати, что на чеке "Безналичные"
     */
    @Test
    public void ffdTestWithTotalTax() {
        registrationWithDefaultTax();
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            startOpenShift();
            checkMessageAboutChangeFfd();
        }
        steps.hub().answerPoll(RepositoryPollResponse.getActivateKey(kktPlantNum));
        steps.shift().openShift();
        //Включить терминал
        steps.step().connectBankTerminal();
        goToLcdChoosePaymentsType();
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsLcdEvents("2. Безналичными"));
        } else {
            steps.expectation().waitListScreen(1, "1. Наличными", "2. Безналичными",
                    "3. Аванс", "4. В кредит");
        }
        overPaymentCashless();
        checkPrintBuffer();
    }

    //______________________________________________STEPS_______________________________________________________________

    @Step("Регистрация кассы c ОСН")
    private void registrationWithDefaultTax() {
        hubQueue.addResponse(KKT_REGISTER_INFO,
                RepositoryRegistrationResponse.getKktRegWithTax(regNum));

        steps.hub().answerPoll(RepositoryPollResponse.getRegistration(RegistrationTypeEnum.REGISTRATION));
    }

    @Step("Начало открытия смены")
    private void startOpenShift() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.key1, 2);
        manager.sendCommands();

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            manager.sleepPlease(6000);
        }
    }

    @Step("Проверка печатного буфера")
    private void checkPrintBuffer() {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            assertTrue(manager.getEventsContainer().isContainsPrintEvent("БЕЗНАЛИЧНЫМИ        =       1.00\n" +
                    "СУММА БЕЗ НДС       =       1.00\n" +
                    "--------------------------------\n" +
                    "      Спасибо за покупку!\n"));
        }
    }

    @Step("Оформить покупку безналичным способом платежа")
    private void overPaymentCashless() {
        if (cashBox.getBoxType().equals(CashBoxType.DREAMKAS_F)) {
            manager.pressKey(KeyEnum.key2);
        } else {
            manager.pressKey(KeyEnum.keyDown);
        }

        manager.pressKey(KeyEnum.keyEnter);
        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
        steps.expectation().waitLoader();

        if (cashBox.getBoxType().equals(CashBoxType.KASSA_F)) {
            steps.expectation().waitNotificationScreen(NOTIFICATION_SALE_FINISH, "Отдайте", "покупателю", "чек и карту");
        }

        manager.pressKey(KeyEnum.keyEnter);
        manager.sendCommands();
    }

    @Step("Перейти в экран выбора типа оплат")
    private void goToLcdChoosePaymentsType() {
        manager.pressKey(KeyEnum.keyMenu);
        manager.pressKey(KeyEnum.keyCancel);
        manager.pressKey(KeyEnum.key1);
        manager.pressKey(KeyEnum.keyEnter, 2);
        manager.sendCommands();
    }

    @Step("Проверить экран при открытии смены с сообщением об изменениях в законадательстве")
    private void checkMessageAboutChangeFfd() {
        steps.expectation().waitExpectedLcd("Вышли изменения");

        manager.pressKey(KeyEnum.keyDown, 5);
        manager.sendCommands();

        assertTrue(manager.getEventsContainer().isContainsLcdEvents("Вышли изменения", "в законодатель-", "стве.Обратитесь",
                "в сервис. центр", "Прод. работу?", "[Отмена]/[Ввод]"));

        manager.pressKey(KeyEnum.keyCancel);
        manager.sendCommands();
    }

}
