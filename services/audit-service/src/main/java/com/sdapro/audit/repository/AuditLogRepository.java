package com.sdapro.audit.repository;

import com.sdapro.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByEventType(String eventType);
    List<AuditLog> findByAggregateId(String aggregateId);
    List<AuditLog> findByTimestampBetween(Instant start, Instant end);
    List<AuditLog> findByActor(String actor);

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp >= ?1 ORDER BY a.timestamp DESC")
    List<AuditLog> findRecentLogs(Instant since);
}
