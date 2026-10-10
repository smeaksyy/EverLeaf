package com.everleaf.util;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GsonUtil {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return src == null ? JsonNull.INSTANCE : new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return json == null || json.isJsonNull() ? null : LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();

    public static Gson getGson() {
        return GSON;
    }
}
