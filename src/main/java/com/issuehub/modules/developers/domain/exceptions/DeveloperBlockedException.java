package com.issuehub.modules.developers.domain.exceptions;

import com.issuehub.shared.domain.exceptions.DomainException;
import com.issuehub.shared.domain.model.EntityId;

public class DeveloperBlockedException extends DomainException {

    public DeveloperBlockedException(EntityId id) {
        super("Developer " + id.value() + "is blocked and cannot perform this action");
    }

}
