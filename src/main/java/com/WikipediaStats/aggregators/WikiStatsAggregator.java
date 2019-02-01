package com.WikipediaStats.aggregators;

import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.constants.WikiPageType;
import com.WikipediaStats.interfaces.IPageViewStatsVisitor;
import com.WikipediaStats.models.PageViewStats;

import java.util.*;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Created by Preet on 1/28/2019.
 */
public class WikiStatsAggregator {
    // Store temporarily all pagetype, and their corresponding counts to do final computation.
    private static final Logger logger = LogManager.getLogger(WikiStatsAggregator.class.getName());
    private Map<WikiPageType, Map<String, PageViewStats>> pagesStatsMap;

    // Map configuration to have different computation done on different page types. It allows visitor design kind of
    // pattern where object or storage can be decoupled from operation performed by the visitor (inheriting from interface like IPageViewBasicStatsVisitor)
    // like min, max, median or kfrequent kind of stats.
    private EnumMap<WikiPageType, List<IPageViewStatsVisitor>> pageTypesVisitorMap;

    /** method is constructor that accepts configurable type of page types to run different visitor(stats visitor) operations.
     * @param processablePageTypes - this indicates what type of visitors to be invoked on page types u want to process.
    **/
    public WikiStatsAggregator(EnumMap<WikiPageType, List<IPageViewStatsVisitor>> processablePageTypes) {
        pageTypesVisitorMap = processablePageTypes;
        pagesStatsMap = new HashMap<WikiPageType, Map<String, PageViewStats>>();
    }

    public Set<WikiPageType> getProcessablePageTypes() { return pageTypesVisitorMap.keySet();}

    public void clearOutAggregation() {
        pagesStatsMap.clear();
    }

    /**
     * Method that accepts pageview statistics per line.
     * @param pageViewStats
     * @return
     **/
    public boolean acceptPageViewStats(PageViewStats pageViewStats) {
        WikiPageType newPageType = pageViewStats.getPageType();
        String pageTitle = pageViewStats.getPageTitle();
        long newPageVisits = pageViewStats.getPageVisits();

        // Set the visits to zero as after initialization, we set it anyways either increment from zero or existing.
        pageViewStats.setPageVisits(0, false);

        try {
           PageViewStats pgViewStats = (PageViewStats)pagesStatsMap.computeIfAbsent( newPageType,
                                           pType -> new HashMap()
                                          )
                          .computeIfAbsent( pageTitle,
                                            pTitle -> pageViewStats
                                          );
           pgViewStats.setPageVisits(newPageVisits, true);

            return true;
        }
        catch (Exception ex) {
            logger.error("Exception occurred in storing new pageview stats with page Title {} : Exception - {} ", pageViewStats.getPageTitle(), ex);
        }
        return false;
    }


    /**
     * Method is invoked after each line from wikipedia log file has been processed for final stats aggregation using visitor pattern.
     * @param pageType
     * @return
     */
    public Map<PageViewStatsAttribute, List<PageViewStats>> processFinalStatsForPageType(WikiPageType pageType) {
        try {
            List<IPageViewStatsVisitor> statsVisitors = pageTypesVisitorMap.get(pageType);
            if (statsVisitors == null) return null;

            Map<PageViewStatsAttribute, List<PageViewStats>> pageViewFinalStatsMap = new HashMap<>();

            for (IPageViewStatsVisitor statVisitor : statsVisitors) {
                pageViewFinalStatsMap.putAll(statVisitor.visitPageViewStats(pagesStatsMap.get(pageType)));
            }
            return pageViewFinalStatsMap;
        }

        catch (Exception ex) {
            logger.error("Exception occurred in processing final stats for page type {} : Exception - {}", pageType, ex);
        }
        return null;
    }

}
