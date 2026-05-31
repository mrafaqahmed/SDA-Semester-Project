package com.sdapro.response.decorator;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuditLogDecorator extends ResponseActionDecorator {
    
    public AuditLogDecorator(ResponseAction wrappedAction) {
        super(wrappedAction);
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        log.info("AUDIT: Starting execution of action: {}", wrappedAction.getActionType());
        ResponseActionResult result = wrappedAction.execute(incident);
        log.info("AUDIT: Action {} completed with status: {}", wrappedAction.getActionType(), result.isSuccess());
        return result;
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        log.info("AUDIT: Starting rollback of action: {}", wrappedAction.getActionType());
        ResponseActionResult result = wrappedAction.rollback(incident);
        log.info("AUDIT: Rollback completed with status: {}", result.isSuccess());
        return result;
    }
}
