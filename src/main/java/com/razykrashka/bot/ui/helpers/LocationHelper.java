package com.razykrashka.bot.ui.helpers;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.api.YandexMapApi;
import com.razykrashka.bot.api.model.yandex.FeatureYandex;
import com.razykrashka.bot.api.model.yandex.Properties;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.exception.YandexMapApiException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class LocationHelper {

    @Autowired
    YandexMapApi yandexMapApi;
    Location location;

    public Location getLocation(String address) throws YandexMapApiException {
        // TODO: Filter by category (cafe, restaurants etc) and city
        String addresWithCountry = address + " Минск";
        List<FeatureYandex> yandexMapModelList = yandexMapApi.getYandexMapModel(addresWithCountry,
                ImmutableMap.of("type", "biz")).getFeatures();

        FeatureYandex yandexMapModel;
        if (yandexMapModelList.size() == 0) {
            throw new YandexMapApiException("No features were found!");
        } else {
            yandexMapModel = yandexMapModelList.get(0);
            location = new Location();
            location.setAddress(address);
            location.setLongitude(yandexMapModel.getGeometry().getCoordinates().get(0));
            location.setLatitude(yandexMapModel.getGeometry().getCoordinates().get(1));
            return location;
        }
    }

    public Location getLocationByCoordinate(org.telegram.telegrambots.meta.api.objects.Location location) throws YandexMapApiException {
        String text = location.getLatitude() + "," + location.getLongitude();
        Properties properties = yandexMapApi.getYandexMapModel(text).getFeatures().get(0).getProperties();
        return getLocation(properties.getName());
    }
}