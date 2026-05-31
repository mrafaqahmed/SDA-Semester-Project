package com.sdapro.response.decorator;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;

public abstract class ResponseActionDecorator extends ResponseAction {
    protected ResponseAction wrappedAction;

    public ResponseActionDecorator(ResponseAction wrappedAction) {
        super(wrappedAction.getActionType(), wrappedAction.getDescription());
        this.wrappedAction = wrappedAction;
        this.id = wrappedAction.getId();
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        return wrappedAction.execute(incident);
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        return wrappedAction.rollback(incident);
    }
}
