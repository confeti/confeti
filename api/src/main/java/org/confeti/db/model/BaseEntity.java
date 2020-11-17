package org.confeti.db.model;

import java.util.Optional;

public interface BaseEntity<E extends BaseEntity<E>> {

    void updateFrom(E entity);

    static <T> T updateValue(final T oldValue, final T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }
}
