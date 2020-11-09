package org.confeti.exception;

import org.jetbrains.annotations.NotNull;

public class NotFoundException extends Exception {

    public NotFoundException(@NotNull final Entity entity,
                             @NotNull final String primaryKey) {
        super(String.format("%s %s not found", entity.name(), primaryKey));
    }

    public enum Entity {
        CONFERENCE,
        SPEAKER,
        REPORT;
    }
}
