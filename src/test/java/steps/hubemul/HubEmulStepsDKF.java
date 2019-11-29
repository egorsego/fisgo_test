package steps.hubemul;

import application_manager.api_manager.Manager;
import hub_emulator.Server;
import hub_emulator.response.ResponseHub;
import lombok.extern.log4j.Log4j;

@Log4j
public class HubEmulStepsDKF extends AbstractHubEmulSteps{

    public HubEmulStepsDKF(Manager manager, Server server, ResponseHub hubQueue) {
        super(manager, server, hubQueue);
    }

}
