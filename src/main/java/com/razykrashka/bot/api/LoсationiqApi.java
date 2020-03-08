package com.razykrashka.bot.api;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.api.model.locationiq.Locationiq;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class LoсationiqApi extends RestBuilder {

    @Value("${locationq.api.token}")
    static String token;
    @Value("${locationq.api.endpoint}")
    static String endpoint;

    public static List<Locationiq> getLocationModel(String address) {
        return getListRequest(endpoint, ImmutableMap.of("key", token, "q", address), Locationiq.class);
    }
}