package com.sdapro.response.decorator;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApprovalGateDecorator extends ResponseActionDecorator {
    private static final boolean AUTO_APPROVE = true;
    
    public ApprovalGateDecorator(ResponseAction wrappedAction) {
        super(wrappedAction);
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        if (AUTO_APPROVE) {
            log.info("APPROVAL: Action {} auto-approved", wrappedAction.getActionType());
            return wrappedAction.execute(incident);
        }
        log.warn("APPROVAL: Action {} awaiting approval", wrappedAction.getActionType());
        return new ResponseActionResult(false, "Action pending approval");
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        return wrappedAction.rollback(incident);
    }
}
