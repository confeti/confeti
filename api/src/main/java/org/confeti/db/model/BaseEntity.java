package org.confeti.db.model;

import java.io.Serializable;
import java.util.Optional;

public interface BaseEntity<E extends BaseEntity<E>> extends Serializable {

    void updateFrom(E entity);

    static <T> T updateValue(T oldValue, T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }
}
