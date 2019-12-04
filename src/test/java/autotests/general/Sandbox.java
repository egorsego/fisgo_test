package autotests.general;

import application_manager.api_manager.CashBoxType;
import application_manager.api_manager.Manager;
import application_manager.api_manager.events.EventsContainer;
import application_manager.api_manager.events.enums.EventType;
import application_manager.cashbox.CashBox;
import autotests.BaseTestClass;
import lombok.extern.log4j.Log4j;
import org.testng.annotations.Test;
import steps.Steps;

@Log4j
public class Sandbox extends BaseTestClass {

    @Test
    public void true1() {

        CashBox cashBoxDKF = new CashBox(CashBoxType.DREAMKAS_F, "192.168.243.82");
        Manager managerDKF = new Manager(cashBoxDKF);
        Steps stepsDKF = new Steps(managerDKF, server);

        managerDKF.rebootFiscat();
        stepsDKF.step().inputPassword();

        System.out.println("1 - > " + cashBoxDKF.getIpAddr());
        System.out.println("2 - > " + cashBox.getIpAddr());

        while (true) {
            managerDKF.sleepPlease(10_000);
            System.out.println(manager.getEventsContainer().getEventsMap().get(EventType.LCD).size());

        }
    }
}
