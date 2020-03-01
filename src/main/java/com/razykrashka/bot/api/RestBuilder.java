package com.razykrashka.bot.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@Getter
@Setter
public class RestBuilder {

    private final static ObjectMapper MAPPER = getMapper();

    public static <T> List<T> getListRequest(String endpoint, Map<String, Object> queryMap, Class<T> classModel) {
        String json = executeAndGetJson(endpoint, queryMap);
        try {
            return MAPPER.readValue(json, TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, classModel));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getRequest(String endpoint, Map<String, Object> queryMap, Class<T> classModel) {
        String json = executeAndGetJson(endpoint, queryMap);
        try {
            return MAPPER.readValue(json, classModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String executeAndGetJson(String endpoint, Map<String, Object> queryMap) {
        try {
            String request = Unirest.get(endpoint).queryString(queryMap)
                    .getUrl();
            log.info("Request: \n{}", request);

            HttpResponse<JsonNode> response = Unirest.get(request).asJson();
            Object json = getMapper().readValue(response.getBody().toString(), Object.class);
            log.info("Response : \n{}", getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
            return response.getBody().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }
}

