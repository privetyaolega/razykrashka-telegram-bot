package com.razykrashka.bot.api.model.yandex;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyMetaData {

    @JsonProperty("id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("address")
    public String address;
    @JsonProperty("url")
    public String url;
    @JsonProperty("Phones")
    public List<Phone> phones = null;
    @JsonProperty("Categories")
    public List<Category> categories = null;
    @JsonProperty("Hours")
    public Hours hours;
}