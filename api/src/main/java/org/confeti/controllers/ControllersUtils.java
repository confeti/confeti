package org.confeti.controllers;

public final class ControllersUtils {
    public static final String REST_API_PATH = "/api/rest";
    public static final String CONFERENCE_NAME_URI_PARAMETER = "conference_name";
    public static final String YEAR_URI_PARAMETER = "year";
    public static final String COMPANY_URI_PARAMETER = "company_name";
    public static final String SPEAKER_ID_URI_PARAMETER = "speaker_id";

    // Not instantiable
    private ControllersUtils() {
        throw new AssertionError();
    }
}
