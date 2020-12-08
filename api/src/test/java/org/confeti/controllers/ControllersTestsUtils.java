package org.confeti.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class ControllersTestsUtils {
    public static final String STATUS = "{\"status\":\"success\"}";

    public static <T> void assertEqualsList(final List<?> listResponse, final List<T> expectedList, final Class<T> clazz) {
        final ObjectMapper mapper = new ObjectMapper();
        assertEquals(expectedList.size(), listResponse.size());
        List<T> convertedResponse = listResponse.stream()
                .map(obj -> mapper.convertValue(obj, clazz))
                .collect(Collectors.toList());
        for (T t : convertedResponse) {
            assertTrue(expectedList.contains(t));
        }
        for (T t : expectedList) {
            assertTrue(convertedResponse.contains(t));
        }
    }

    // Not instantiable
    private ControllersTestsUtils() {
        throw new AssertionError();
    }
}
