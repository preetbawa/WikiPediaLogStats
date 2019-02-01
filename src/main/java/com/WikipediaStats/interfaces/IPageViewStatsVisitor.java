package com.WikipediaStats.interfaces;

import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.models.PageViewStats;

import java.util.List;
import java.util.Map;

/**
 * Created by Preet on 1/28/2019.
 */
public interface IPageViewStatsVisitor {
    Map<PageViewStatsAttribute, List<PageViewStats>> visitPageViewStats(Map<String, PageViewStats> pageViewStatsMap);
}
