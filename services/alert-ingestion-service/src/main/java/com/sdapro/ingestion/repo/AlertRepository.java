package com.sdapro.ingestion.repo;

import com.sdapro.ingestion.domain.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlertRepository extends JpaRepository<AlertEntity, UUID> {
}
