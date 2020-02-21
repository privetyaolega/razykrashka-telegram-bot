package com.razykrashka.bot.api.model.locationiq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Locationiq {
    @JsonProperty("place_id")
    String placeId;
    String lat;
    String lon;
    @JsonProperty("display_name")
    String displayName;
    String type;
}