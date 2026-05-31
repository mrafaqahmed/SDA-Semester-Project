package com.sdapro.incident;

import com.sdapro.incident.domain.Incident;
import com.sdapro.incident.state.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IncidentStateTest {

    @Test
    public void testIncidentStartsInNewState() {
        Incident incident = new Incident("Test", "Description", "HIGH");
        assertEquals("NEW", incident.getCurrentState());
    }

    @Test
    public void testValidStateTransition() {
        Incident incident = new Incident("Test", "Description", "HIGH");
        
        incident.beginTriage();
        assertEquals("UNDER_TRIAGE", incident.getCurrentState());
        
        incident.initiateContainment();
        assertEquals("CONTAINMENT", incident.getCurrentState());
    }

    @Test
    public void testInvalidStateTransitionThrows() {
        Incident incident = new Incident("Test", "Description", "HIGH");
        
        assertThrows(IllegalStateException.class, () -> incident.initiateContainment(),
            "Should not allow containment from NEW state");
    }

    @Test
    public void testCompleteIncidentLifecycle() {
        Incident incident = new Incident("Test", "Description", "CRITICAL");
        
        assertEquals("NEW", incident.getCurrentState());
        incident.beginTriage();
        assertEquals("UNDER_TRIAGE", incident.getCurrentState());
        
        incident.initiateContainment();
        assertEquals("CONTAINMENT", incident.getCurrentState());
        
        incident.initiateEradication();
        assertEquals("ERADICATION", incident.getCurrentState());
        
        incident.initiateRecovery();
        assertEquals("RECOVERY", incident.getCurrentState());
        
        incident.completePostIncidentReview();
        assertEquals("POST_INCIDENT_REVIEW", incident.getCurrentState());
        
        incident.close();
        assertEquals("CLOSED", incident.getCurrentState());
    }

    @Test
    public void testAllowedActionsVaryByState() {
        Incident incident = new Incident("Test", "Description", "HIGH");
        
        String newStateActions = incident.getAllowedActions();
        assertTrue(newStateActions.contains("beginTriage"));
        
        incident.beginTriage();
        String triageActions = incident.getAllowedActions();
        assertTrue(triageActions.contains("initiateContainment"));
    }
}
