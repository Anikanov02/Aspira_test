package org.aspire.test.domain.leon;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum SportsElements {
    SPORT_ALL("all"),
    SPORT_SOCKER_ICON("soccer"),
    SPORT_BASKETBALL_ICON("basketball"),
    SPORT_HOCKEY_ICON("icehockey"),
    SPORT_TENNIS_ICON("tennis");

    private String name;

    SportsElements(String name) {
        this.name = name;
    }

    public static SportsElements fromShortName(String shortName) {
        return Arrays.stream(values()).filter(value -> value.getName().equalsIgnoreCase(shortName)).findFirst().orElse(null);
    }
}
