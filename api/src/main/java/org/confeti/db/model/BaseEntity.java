package org.confeti.db.model;

import org.jetbrains.annotations.NotNull;

public interface BaseEntity<E extends BaseEntity<E>> {

    void updateFrom(@NotNull E entity);
}
