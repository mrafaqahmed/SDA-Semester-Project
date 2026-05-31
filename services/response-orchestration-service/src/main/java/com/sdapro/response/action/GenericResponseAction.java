package com.sdapro.response.action;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
public class GenericResponseAction extends ResponseAction {

    public GenericResponseAction(String actionType) {
        super(actionType, "Execute action: " + actionType);
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        try {
            log.info("Executing generic action: {}", getActionType());
            setExecutionStatus("EXECUTING");
            setExecutedAt(LocalDateTime.now());

            String message = "Action " + getActionType() + " executed";
            setExecutionStatus("SUCCESS");
            return new ResponseActionResult(true, message);
        } catch (Exception e) {
            setExecutionStatus("FAILED");
            return new ResponseActionResult(false, "Failed to execute action: " + e.getMessage());
        }
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        log.info("Rolling back action: {}", getActionType());
        setExecutionStatus("ROLLBACK_PENDING");
        return new ResponseActionResult(true, "Action rolled back");
    }
}
