package com.bogunoz.projects.orchestrator.common.constant;

import lombok.Getter;
import org.springframework.modulith.NamedInterface;

@NamedInterface("constant")
public final class Error {
    public static final String CANNOT_LOAD_PROMPT = "Cannot load default prompt";

    private Error() {}
}
