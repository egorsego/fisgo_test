package steps.shift;

import application_manager.api_manager.Manager;
import application_manager.cashbox.CashBox;
import steps.main.MainSteps;

public class ShiftStepsKF extends AbstractShiftSteps {

    public ShiftStepsKF(Manager manager, CashBox cashBox, MainSteps mainSteps) {
        super(manager, cashBox, mainSteps);
    }
}
