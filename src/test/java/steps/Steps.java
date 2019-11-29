package steps;

import application_manager.api_manager.Manager;
import hub_emulator.Server;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j;
import steps.cabinet.CabinetSteps;
import steps.cabinet.CabinetStepsDKF;
import steps.cabinet.CabinetStepsKF;
import steps.hubemul.HubEmulSteps;
import steps.hubemul.HubEmulStepsDKF;
import steps.hubemul.HubEmulStepsKF;
import steps.main.MainSteps;
import steps.main.MainStepsDKF;
import steps.main.MainStepsKF;
import steps.payment.PaymentSteps;
import steps.payment.PaymentStepsDKF;
import steps.payment.PaymentStepsKF;
import steps.shift.ShiftSteps;
import steps.shift.ShiftStepsDKF;
import steps.shift.ShiftStepsKF;
import steps.wait.WaitSteps;
import steps.wait.WaitStepsDKF;
import steps.wait.WaitStepsKF;

@Log4j
@Getter
@Setter
@Accessors(fluent = true)
public class Steps {

    private MainSteps step;
    private CabinetSteps cab;
    private PaymentSteps payment;
    private ShiftSteps shift;
    private WaitSteps expectation;
    private HubEmulSteps hub;

    public Steps(Manager manager, Server server) {
        switch (manager.getCashBox().getBoxType()) {
            case DREAMKAS_F:
                expectation = new WaitStepsDKF(manager, server);
                step = new MainStepsDKF(manager, manager.getCashBox(), expectation);
                hub = new HubEmulStepsDKF(manager, server, server.getResponseHub());
                cab = new CabinetStepsDKF(manager, manager.getCashBox(), expectation);
                payment = new PaymentStepsDKF(manager, manager.getCashBox(), server, expectation, hub);
                shift = new ShiftStepsDKF(manager, manager.getCashBox(), step);
                break;
            case KASSA_F:
            case KASSA_BUS:
            case PULSE_FA:
                expectation = new WaitStepsKF(manager, server);
                step = new MainStepsKF(manager, manager.getCashBox(), expectation);
                hub = new HubEmulStepsKF(manager, server, server.getResponseHub());
                cab = new CabinetStepsKF(manager, manager.getCashBox(), expectation);
                payment = new PaymentStepsKF(manager, manager.getCashBox(), server, expectation, hub);
                shift = new ShiftStepsKF(manager, manager.getCashBox(), step);
                break;
            case KASSA_RB:
                //TODO
                break;
            default:
                log.error("ERROR! Неизвестный тип кассы - невозможно продолжить работу");
                System.exit(1);
        }
    }

}
