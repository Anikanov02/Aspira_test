package org.aspire.test.domain.leon;

import lombok.Getter;

@Getter
public enum ElementsClassNames {
    SPORT_ELEMENT_ITEM("sport-event-list-filter__item"),
    SPORT_EVENT_ITEM("sport-event-list-item__block"),
    EVENT_COMPETITOR_NAME("sport-event-list-item-competitor__name"),
    BUTTON_ALL_TOP_LEAGUES("sports-sidebar-top-leagues__all-header sports-level-1-spoiler__header"),
    SWIPER_WRAPPER("swiper__wrapper"),
    TOP_LEAGUES_LIST("leagues-list__list leagues-list__list--top"),
    TOP_LEAGUES_TABS_BUTTON("tabs-button tabs-button--is-bordered markets-tabs__button"),
    MARKET_GROUP("sport-event-details-market-group__wrapper"),
    MARKET_GROUP_TITLE_LABEL("sport-event-details-market-group__title-label"),
    MARKET_GROUP_DATA_HOLDER("sport-event-details-item__runner-holder"),
    COEFFICIENT_LEFT("sport-event-list-item-market__coefficient sport-event-list-item-market__coefficient--left"),
    COEFFICIENT_RIGHT("sport-event-list-item-market__coefficient sport-event-list-item-market__coefficient--right");


    private String className;

    ElementsClassNames(String className) {
        this.className = className;
    }
}
