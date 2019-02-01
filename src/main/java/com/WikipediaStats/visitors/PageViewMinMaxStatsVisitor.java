package com.WikipediaStats.visitors;

import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.interfaces.IPageViewStatsVisitor;
import com.WikipediaStats.models.PageViewStats;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Preet on 1/28/2019.
 */

/**
 * this class follows Visitor design pattern of implementing operation on PageView data by computing min, max stats for certain pagetype pages.
 */
public class PageViewMinMaxStatsVisitor implements IPageViewStatsVisitor {

    private static PageViewStatsAttribute max_views = PageViewStatsAttribute.MAX_VIEWS;
    private static PageViewStatsAttribute min_views = PageViewStatsAttribute.MIN_VIEWS;

    /**
     * Method generates max and min visited wikipedia page by using simple scanning algorithm.
     * @param pageViewStatsMap - hashmap of page title with pageview stats as value for all the pages belonging to specific page type.
     * @return
     */
    public Map<PageViewStatsAttribute, List<PageViewStats>> visitPageViewStats(Map<String, PageViewStats> pageViewStatsMap) {

        PageViewStats minPageViewStats = null;
        PageViewStats maxPageViewStats = null;

        boolean start = true;

        for (String pageKey : pageViewStatsMap.keySet()) {
            PageViewStats pageStats = pageViewStatsMap.get(pageKey);
            if (start){
                minPageViewStats = pageStats;
                maxPageViewStats = pageStats;
                start = false;
                continue;
            }

            if (maxPageViewStats.getPageVisits() < pageStats.getPageVisits()) {
                maxPageViewStats = pageStats;
            }

            else if (minPageViewStats.getPageVisits() > pageStats.getPageVisits()) {
                minPageViewStats = pageStats;
            }
        }

        Map<PageViewStatsAttribute, List<PageViewStats>> minmaxPageStatsMap = new HashMap();

        minmaxPageStatsMap.put(max_views, Stream.of(maxPageViewStats).collect(Collectors.toList()) );
        minmaxPageStatsMap.put(min_views, Stream.of(minPageViewStats).collect(Collectors.toList()));
        return minmaxPageStatsMap;
    }


}
