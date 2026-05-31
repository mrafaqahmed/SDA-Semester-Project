package com.sdapro.response.facade;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import com.sdapro.response.action.BlockIPResponseAction;
import com.sdapro.response.action.GenericResponseAction;
import com.sdapro.response.strategy.*;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class IncidentResponseFacade {

    public void executeResponse(Incident incident, String strategyName) {
        log.info("Starting incident response for incident {} using strategy {}",
            incident.getId(), strategyName);

        ResponseStrategy strategy = selectStrategy(strategyName);
        List<String> actions = strategy.generateActions(incident);

        for (String actionName : actions) {
            ResponseAction action = createAction(actionName);
            ResponseActionResult result = action.execute(incident);
            log.info("Action {} result: {}", actionName, result.getMessage());
        }

        incident.initiateContainment();
        log.info("Incident {} response orchestration complete", incident.getId());
    }

    private ResponseStrategy selectStrategy(String strategyName) {
        return switch (strategyName) {
            case "AGGRESSIVE" -> new AggressiveContainmentStrategy();
            case "BALANCED" -> new BalancedResponseStrategy();
            case "CONSERVATIVE" -> new ConservativeStrategy();
            default -> new BalancedResponseStrategy();
        };
    }

    private ResponseAction createAction(String actionName) {
        return switch (actionName) {
            case "BLOCK_IP" -> new BlockIPResponseAction("192.168.1.1");
            case "ISOLATE_HOST_FROM_NETWORK" -> new BlockIPResponseAction("HOST_IP");
            case "KILL_MALICIOUS_PROCESSES" -> new GenericResponseAction("KILL_PROCESS");
            case "REVOKE_CREDENTIALS" -> new GenericResponseAction("REVOKE_CREDS");
            case "DISABLE_USER_ACCOUNT" -> new GenericResponseAction("DISABLE_USER");
            default -> new GenericResponseAction(actionName);
        };
    }
}

