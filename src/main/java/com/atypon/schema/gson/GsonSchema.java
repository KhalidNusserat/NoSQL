package com.atypon.schema.gson;

import com.atypon.schema.Schema;
import com.google.gson.JsonElement;

public interface GsonSchema<ArgsType extends JsonElement> extends Schema<JsonElement, ArgsType> {
}