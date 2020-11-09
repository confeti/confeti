package org.confeti.util.converter;

import org.confeti.db.model.udt.SpeakerCompanyUDT;
import org.confeti.db.model.udt.SpeakerLocationUDT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class HelperConverter {

    private HelperConverter() {
        // not instantiable
    }

    @Nullable
    public static <T, E> E mapValue(@Nullable final T value,
                                    @NotNull final Function<T, E> mapping) {
        return Optional.ofNullable(value)
                .map(mapping)
                .orElse(null);
    }

    @Nullable
    public static <E,T> Set<T> mapSet(@Nullable final Set<E> set,
                                      @NotNull final Function<E, T> mapping) {
        if (set == null) {
            return null;
        }
        return set.stream()
                .map(mapping)
                .collect(Collectors.toSet());
    }

    @Nullable
    public static String getLastSpeakerCompany(@NotNull final Set<SpeakerCompanyUDT> companies) {
        final var company = getLastSetValue(
                companies,
                Comparator.comparing(SpeakerCompanyUDT::getYear)
                        .thenComparing(SpeakerCompanyUDT::getAddedDate));
        if (company != null) {
            return company.getName();
        }
        return null;
    }

    @Nullable
    public static String getLastSpeakerLocation(@NotNull final Set<SpeakerLocationUDT> locations) {
        final var location = getLastSetValue(
                locations,
                Comparator.comparing(SpeakerLocationUDT::getAddedDate));
        if (location != null) {
            return location.getName();
        }
        return null;
    }

    @Nullable
    public static <T> T getLastSetValue(@Nullable final Set<T> set,
                                        @NotNull final Comparator<T> comparator) {
        if (set != null) {
            return set.stream()
                    .max(comparator)
                    .orElse(null);
        }
        return null;
    }
}
