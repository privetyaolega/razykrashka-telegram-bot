package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.api.YandexMapApi;
import com.razykrashka.bot.api.model.yandex.FeatureYandex;
import com.razykrashka.bot.db.entity.razykrashka.Location;
import com.razykrashka.bot.db.entity.razykrashka.TelegramLinkEmbedded;
import com.razykrashka.bot.exception.YandexMapApiException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class MapLocationHelper {
    String googleMapLinkPattern = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    Location location;

    public Location getLocation(String address) throws YandexMapApiException {
        // TODO: Filter by category (cafe, restaurants etc) and city
        List<FeatureYandex> yandexMapModelList = YandexMapApi.getYandexMapModel(address).getFeatures();

        FeatureYandex yandexMapModel;
        if (yandexMapModelList.size() == 0) {
            throw new YandexMapApiException("No features were found!");
        } else {
            yandexMapModel = yandexMapModelList.get(0);
            location = new Location();
            location.setAddress(address);
            location.setLongitude(yandexMapModel.getGeometry().getCoordinates().get(0));
            location.setLatitude(yandexMapModel.getGeometry().getCoordinates().get(1));
            location.setLocationLink(TelegramLinkEmbedded.builder()
                    .textLink(location.getAddress())
                    .link(String.format(googleMapLinkPattern, location.getLatitude(), location.getLongitude()))
                    .build());
            return location;
        }
    }
}