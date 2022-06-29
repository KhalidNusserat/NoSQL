package com.atypon.nosql.document.constraints;

import com.google.gson.JsonElement;

public interface Constraint {
    boolean validate(JsonElement jsonElement);
}
