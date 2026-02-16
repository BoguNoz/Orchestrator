package com.bogunoz.projects.orchestrator.common.constant;

import org.springframework.modulith.NamedInterface;

@NamedInterface("error-const")
public final class Error {

    public static final String EXTERNAL_SERVICE_ERROR = "External service error";
    public static final String CANNOT_LOAD_PROMPT = "Cannot load default prompt";
    public static final String INCORRECT_SERVICE_REQUEST = "Incorrect service request";
    public static final String SEBASTIAN_ERROR_DEPTH = "Przepraszam, ale przekroczyłem limit prób pobrania danych dla tego pytania.";
    public static final String SEBASTIAN_ERROR_TOOL = "Przepraszam nie posiadam na to odpowiedzi";
    public static final String SEBASTIAN_FATAL_ERROR = "Przepraszam coś poszło nie tak";
    private Error() {}
}
