package com.razykrashka.bot.api;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoсationiqApi extends RestBuilder {

    @Value("${api.locationiq.token}")
    static String token;
    @Value("${api.locationiq.endpoint}")
    static String endpoint;

    public static List<Locationiq> getLocationiq(String address) {
        return getRequest(endpoint, ImmutableMap.of("key", token, "q", address), Locationiq.class);
    }
}