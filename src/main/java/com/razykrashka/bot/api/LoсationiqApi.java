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

    @Value("${api.locationiq.token}")
    static String token = "9ee08192129152";
    @Value("${api.locationiq.endpoint}")
    static String endpoint = "https://eu1.locationiq.com/v1/search.php?&format=json";

    public static List<Locationiq> getLocationModel(String address) {
        return getRequest(endpoint, ImmutableMap.of("key", token, "q", address), Locationiq.class);
    }
}