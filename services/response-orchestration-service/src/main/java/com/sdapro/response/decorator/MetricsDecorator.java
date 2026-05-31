package com.sdapro.response.decorator;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MetricsDecorator extends ResponseActionDecorator {
    
    public MetricsDecorator(ResponseAction wrappedAction) {
        super(wrappedAction);
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        long startTime = System.currentTimeMillis();
        log.info("METRICS: Starting action execution: {}", wrappedAction.getActionType());
        
        ResponseActionResult result = wrappedAction.execute(incident);
        
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("METRICS: Action {} execution completed in {}ms with result: {}", 
                 wrappedAction.getActionType(), executionTime, result.isSuccess());
        
        return result;
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        long startTime = System.currentTimeMillis();
        log.info("METRICS: Starting rollback for action: {}", wrappedAction.getActionType());
        
        ResponseActionResult result = wrappedAction.rollback(incident);
        
        long executionTime = System.currentTimeMillis() - startTime;
        log.info("METRICS: Rollback completed in {}ms with result: {}", executionTime, result.isSuccess());
        
        return result;
    }
}
