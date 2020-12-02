package org.confeti.util;

import java.util.Optional;
import java.util.function.Function;

public final class EntityUtil {

    private EntityUtil() {
        // not instantiable
    }

    public static <T> T updateValue(final T oldValue, final T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }

    public static <T1, T2> T2 updateValue(final T1 value, final Function<T1, T2> map) {
        return Optional.ofNullable(value)
                .map(map)
                .orElse(null);
    }
}
