package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.api.YandexMapApi;
import com.razykrashka.bot.api.model.yandex.FeatureYandex;
import com.razykrashka.bot.api.model.yandex.Properties;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import com.razykrashka.bot.exception.YandexMapApiException;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
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
    final static String GOOGLE_MAP_LINK_PATTERN = "https://www.google.com/maps/search/?api=1&query=%s,%s";

    public Location getLocation(String address) throws YandexMapApiException {
        // ImmutableMap.of("type", "biz")
        // TODO: Filter by category (cafe, restaurants etc) and city
        List<FeatureYandex> yandexMapModelList = yandexMapApi.getYandexMapModel(address).getFeatures();

        FeatureYandex yandexMapModel;
        if (yandexMapModelList.size() == 0) {
            throw new YandexMapApiException("No Yandex Map features were found!");
        } else {
            yandexMapModel = yandexMapModelList.get(0);
            Location location = new Location();
            location.setAddress(yandexMapModel.getProperties().getName());
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

    public String getLocationLink(Meeting meeting) {
        Location location = meeting.getLocation();
        String url = String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
        return TextFormatter.getLink(location.getAddress(), url);
    }

    public String getLocationUrl(Meeting meeting) {
        Location location = meeting.getLocation();
        return String.format(GOOGLE_MAP_LINK_PATTERN, location.getLatitude(), location.getLongitude());
    }
}