package org.aspire.test.domain.leon;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SportsElements {
    SPORT_ALL("all", "swiper-slide sport-event-list-filter__slide"),
    SPORT_SOCKER_ICON("socker", "sport-event-list-filter__item-icon--soccer"),
    SPORT_BASKETBALL_ICON("basketball", "sport-event-list-filter__item-icon--basketball"),
    SPORT_HOCKEY_ICON("hockey", "sport-event-list-filter__item-icon--icehockey"),
    SPORT_TENNIS_ICON("tennis", "sport-event-list-filter__item-icon--tennis");

    private String sportName;
    private String className;

    SportsElements(String shortName, String className) {
        this.sportName = shortName;
        this.className = className;
    }

    public static SportsElements fromShortName(String shortName) {
        return Arrays.stream(values()).filter(value -> value.getSportName().equalsIgnoreCase(shortName)).findFirst().orElse(null);
    }
}
