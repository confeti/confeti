package org.confeti.controllers;


import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public final class ControllersUtils {
    public static final String REST_API_PATH = "/api/rest";
    public static final String CONFERENCE_NAME_URI_PARAMETER = "conference_name";
    public static final String YEAR_URI_PARAMETER = "year";
    public static final String COMPANY_NAME_URI_PARAMETER = "company_name";
    public static final String SPEAKER_ID_URI_PARAMETER = "speaker_id";
    public static final String SPEAKER_NAME_URI_PARAMETER = "speaker_name";
    public static final Marker CONTROLLER_MARKER = MarkerFactory.getMarker("controller");

    // Not instantiable
    private ControllersUtils() {
        throw new AssertionError();
    }
}
