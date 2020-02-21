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

import java.util.List;
import java.util.Map;


@Log4j2
@Component
@Getter
@Setter
public class RestBuilder {

    private final static ObjectMapper MAPPER = getMapper();

    public static <T> List<T> getRequest(String endpoint, Map<String, Object> queryMap, Class<T> classModel) {
        try {
            String request = Unirest.get(endpoint).queryString(queryMap)
                    .getUrl();
            log.info("Request: \n{}", request);

            HttpResponse<JsonNode> response = Unirest.get(request).asJson();
            Object json = getMapper().readValue(response.getBody().toString(), Object.class);
            log.info("Response : \n{}", getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(json));
            return MAPPER.readValue(response.getBody().toString(), TypeFactory.defaultInstance()
                    .constructCollectionType(List.class, classModel));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

//        @JsonRootName(value = "user") wrap object with root value(json)
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
//        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);

        return mapper;
    }
}

