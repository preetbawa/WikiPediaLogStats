package com.WikipediaStats.constants;

/**
 * Created by Preet on 1/31/2019.
 */

public enum PageViewStatsAttribute {
    MAX_VIEWS("MaxiumVisitedPage"),
    MIN_VIEWS("MiniumVisitedPage"),
    KMOSTFREQUENT_VIEWS("k-MostFrequentlyVisitedPages"),
    ;
    private final String pageViewAttributeName;

    PageViewStatsAttribute(String pageViewAttribute) {
        this.pageViewAttributeName = pageViewAttribute;
    }

    public String getPageStatAttributeName() {
        return pageViewAttributeName;
    }
}