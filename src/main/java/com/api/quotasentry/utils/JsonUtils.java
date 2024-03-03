package com.api.quotasentry.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class JsonUtils {

    private static GsonBuilder gsonBuilder = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .addDeserializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(JsonIgnore.class) != null;
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz.isAnnotationPresent(JsonIgnore.class);
                }})
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(JsonIgnore.class) != null;
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return clazz.isAnnotationPresent(JsonIgnore.class);
                }});

    private static final Gson gson = gsonBuilder.setPrettyPrinting().create();

    public static Gson getGson() {
        return gson;
    }

    public static <T>T fromJson(String json, Type type) {
        return getGson().fromJson(json, type);
    }

    public static String toJson(Object src) {
        return getGson().toJson(src);
    }
}
