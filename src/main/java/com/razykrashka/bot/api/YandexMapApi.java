package com.razykrashka.bot.api;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.api.model.yandex.FeaturesYandex;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class YandexMapApi extends RestBuilder {

    @Value("${yandex.api.token}")
    String token;
    @Value("${yandex.api.endpoint}")
    String endpoint;
    Map<String, Object> defaultQueryMap;

    @PostConstruct
    public void postConstruct() {
        defaultQueryMap = new HashMap<>();
        defaultQueryMap.put("apikey", token);
        defaultQueryMap.put("lang", "ru_RU");
    }

    public FeaturesYandex getYandexMapModel(String address) {
        return getRequest(endpoint, ImmutableMap.of(
                "apikey", token,
                "text", address,
                "lang", "ru_RU"
        ), FeaturesYandex.class);
    }

    public FeaturesYandex getYandexMapModel(String address, Map<String, Object> additionalQueryMap) {
        Map<String, Object> queryMap = new HashMap<>(defaultQueryMap);
        queryMap.put("text", address);
        queryMap.putAll(additionalQueryMap);
        return getRequest(endpoint, queryMap, FeaturesYandex.class);
    }
}