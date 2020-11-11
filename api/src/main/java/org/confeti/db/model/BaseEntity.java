package org.confeti.db.model;

import java.io.Serializable;
import java.util.Optional;

public interface BaseEntity extends Serializable {

    static <T> T updateValue(T oldValue, T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }
}
