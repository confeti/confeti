package org.confeti.db.model;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface BaseEntity<E extends BaseEntity<E>> {

    void updateFrom(@NotNull E entity);

    static <T> T updateValue(final T oldValue, final T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }
}
