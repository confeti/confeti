package org.confeti.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public final class EntityUtil {

    private EntityUtil() {
        // not instantiable
    }

    /**
     * Returns the second argument if it is non-{@code null}, otherwise returns
     * the first argument.
     *
     * @param oldValue the value to be returned, if {@code newValue} is {@code null}
     * @param newValue the value to be returned, if it is non-{@code null}
     * @param <T> the type of the value
     * @return {@code newValue} if it is not {@code null} else {@code oldValue}
     */
    public static <T> T updateValue(final T oldValue, final T newValue) {
        return Optional.ofNullable(newValue)
                .orElse(oldValue);
    }

    /**
     * If the second argument is non-{@code null} then the {@code mapper} is applied to
     * it and result will be returned, otherwise returns the first argument.
     *
     * @param oldValue the value to be returned, if a {@code newValue} is {@code null}
     * @param newValue the value to which the {@code mapper} will be applied
     * @param mapper the mapping function to apply to {@code newValue}, if it is non-{@code null}
     * @param <T> the type of the value
     * @return the result of applying a mapping function to {@code newValue} if it is non-{@code null},
     *         otherwise {@code oldValue}
     */
    public static <T> T updateValue(final T oldValue,
                                    final T newValue,
                                    final Function<? super T, T> mapper) {
        return Optional.ofNullable(newValue)
                .map(mapper)
                .orElse(oldValue);
    }

    /**
     * Maps the first argument if it is non-{@code null}.
     *
     * @param value the value to which the {@code mapper} will be applied
     * @param mapper the mapping function to apply to {@code value}, if {@code value} is non-{@code null}
     * @param <T1> the type of the value
     * @param <T2> The type of value returned by the mapping function
     * @return the result of applying a mapping function to {@code value}, if {@code value} is non-{@code null},
     *         otherwise {@code null}
     */
    public static <T1, T2> T2 convertValue(final T1 value,
                                           final Function<? super T1, ? extends T2> mapper) {
        return Optional.ofNullable(value)
                .map(mapper)
                .orElse(null);
    }

    /**
     * Maps the first argument if it is non-{@code null}, otherwise returns the second argument.
     *
     * @param value the value to which the {@code mapper} will be applied
     * @param other the value to return if {@code value} is {@code null}
     * @param mapper the mapping function to apply to {@code value}, if {@code value} is non-{@code null}
     * @param <T1> the type of the value
     * @param <T2> The type of value returned
     * @return the result of applying a mapping function to {@code value}, if {@code value} is non-{@code null},
     *         otherwise {@code other}
     */
    public static <T1, T2> T2 convertValue(final T1 value,
                                           final T2 other,
                                           final Function<? super T1, T2> mapper) {
        return Optional.ofNullable(value)
                .map(mapper)
                .orElse(other);
    }

    /**
     * Maps the first argument if it is non-{@code null} and it matches the given predicate.
     *
     * @param value the value to which the {@code mapper} will be applied
     * @param filter the predicate to apply to a value, if value is non-{@code null}
     * @param mapper the mapping function to apply to {@code value}, if {@code value} is non-{@code null}
     * @param <T1> the type of the value
     * @param <T2> The type of value returned by the mapping function
     * @return the result of applying a mapping function to {@code value}, if {@code value} is non-{@code null}
     *         and it matches the given predicate, otherwise {@code null}
     */
    public static <T1, T2> T2 convertValue(final T1 value,
                                           final Predicate<? super T1> filter,
                                           final Function<? super T1, ? extends T2> mapper) {
        return Optional.ofNullable(value)
                .filter(filter)
                .map(mapper)
                .orElse(null);
    }
}
