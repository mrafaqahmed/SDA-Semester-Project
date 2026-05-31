package com.sdapro.response.action;

import com.sdapro.domain.Incident;
import com.sdapro.domain.ResponseAction;
import com.sdapro.domain.ResponseActionResult;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

@Slf4j
public class BlockIPResponseAction extends ResponseAction {
    private final String ipAddress;

    public BlockIPResponseAction(String ipAddress) {
        super("BLOCK_IP", "Block IP address: " + ipAddress);
        this.ipAddress = ipAddress;
    }

    @Override
    public ResponseActionResult execute(Incident incident) {
        try {
            log.info("Executing BlockIPAction for IP: {}", ipAddress);
            setExecutionStatus("EXECUTING");
            setExecutedAt(LocalDateTime.now());

            // Would call firewall API here
            String message = "IP " + ipAddress + " blocked successfully";
            setExecutionStatus("SUCCESS");
            return new ResponseActionResult(true, message);
        } catch (Exception e) {
            setExecutionStatus("FAILED");
            return new ResponseActionResult(false, "Failed to block IP: " + e.getMessage());
        }
    }

    @Override
    public ResponseActionResult rollback(Incident incident) {
        log.info("Rolling back BlockIPAction for IP: {}", ipAddress);
        setExecutionStatus("ROLLBACK_PENDING");
        try {
            // Would call firewall API to unblock
            return new ResponseActionResult(true, "IP unblocked");
        } catch (Exception e) {
            return new ResponseActionResult(false, "Rollback failed: " + e.getMessage());
        }
    }
}
