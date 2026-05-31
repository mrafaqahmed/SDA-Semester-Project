package com.sdapro.incident.repo;

import com.sdapro.incident.domain.IncidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<IncidentEntity, UUID> {
}
