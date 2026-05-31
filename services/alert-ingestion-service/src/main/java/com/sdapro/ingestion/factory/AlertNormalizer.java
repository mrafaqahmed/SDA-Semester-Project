package com.sdapro.ingestion.factory;

import com.sdapro.shared.domain.Alert;
import com.sdapro.shared.domain.AlertSourceType;

/**
 * PATTERN: Factory Method
 * RATIONALE: Different alert sources require different normalization logic.
 * This interface defines the contract for all normalizers.
 */
public interface AlertNormalizer {
    Alert normalize(Object rawData);
    AlertSourceType getSourceType();
}
