package com.razykrashka.bot.api;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.api.model.yandex.FeaturesYandex;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@PropertySource("classpath:/props/api.properties")
public class YandexMapApi extends RestBuilder {

    @Value("${token}")
    static String token = "7efe26bd-92bc-4c6f-89b5-41b620790bd6";
    @Value("${endpoint}")
    static String endpoint = "https://search-maps.yandex.ru/v1/";

    public static FeaturesYandex getYandexMapModelBiz(String address) {
        return getRequest(endpoint, ImmutableMap.of(
                "apikey", token,
                "text", address,
                "lang", "ru_RU",
                "type", "biz"
        ), FeaturesYandex.class);
    }

    public static FeaturesYandex getYandexMapModel(String address) {
        return getRequest(endpoint, ImmutableMap.of(
                "apikey", token,
                "text", address,
                "lang", "ru_RU"
        ), FeaturesYandex.class);
    }
}
