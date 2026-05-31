package com.sdapro.response.strategy;

import com.sdapro.incident.domain.Incident;
import java.util.List;

/**
 * PATTERN: Strategy
 * RATIONALE: Different incident severity levels require different response approaches.
 * Each strategy encapsulates a response algorithm.
 */
public interface ResponseStrategy {
    String getStrategyName();
    List<String> generateActions(Incident incident);
    int getPriority();
}
