package com.issuehub.modules.developers.domain.exceptions;

import com.issuehub.shared.domain.model.EntityId;

public class DeveloperDeletedException extends RuntimeException {

    public DeveloperDeletedException(EntityId id) {
        super("Developer " + id.value() + "is deleted and cannot perform this action");
    }

}
