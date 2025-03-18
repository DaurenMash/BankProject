package com.bank.account.utils;

import com.bank.account.exception.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
            .setTimeZone(TimeZone.getTimeZone("UTC"));


    public static String convertToJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new JsonProcessingException("Failed to convert object to JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while converting object to JSON", e);
        }
    }

    public static Long extractEntityIdFromJson(String json) {
        try {
            return mapper.readTree(json).get("id").asLong();
        } catch (JsonProcessingException e) {
            throw new JsonProcessingException("Failed to extract entity id from JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while extracting entity id from JSON", e);
        }
    }
}

