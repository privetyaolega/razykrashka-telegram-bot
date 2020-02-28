package com.razykrashka.bot.ui.helpers;

import com.razykrashka.bot.api.YandexMapApi;
import com.razykrashka.bot.api.model.yandex.FeatureYandex;
import com.razykrashka.bot.db.entity.Location;
import com.razykrashka.bot.db.entity.TelegramLinkEmbedded;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Log4j2
public class MapLocationHelper {

    String googleMapLinkPattern = "https://www.google.com/maps/search/?api=1&query=%s,%s";
    Location location;

    public Location getLocation(String address) {
        String addressWithCity = address + " г. Минск";

        // TODO: Filter by category (cafe, restaurants etc)
        FeatureYandex yandexMapModel = YandexMapApi.getYandexMapModel(addressWithCity).getFeatures().get(0);
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
