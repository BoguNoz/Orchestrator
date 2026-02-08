package com.bogunoz.projects.orchestrator.common.constant;

import org.springframework.modulith.NamedInterface;

@NamedInterface("constant")
public final class Error {

    public static final String EXTERNAL_SERVICE_ERROR = "External service error";
    public static final String CANNOT_LOAD_PROMPT = "Cannot load default prompt";
    public static final String INCORRECT_SERVICE_REQUEST = "Incorrect service request";
    private Error() {}
}
