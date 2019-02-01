package com.WikipediaStats.visitors;
import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.interfaces.IPageViewStatsVisitor;
import com.WikipediaStats.models.PageViewStats;

import java.util.*;

/**
 * Created by Preet on 1/30/2019.
 */
public class PageViewKFrequentStatsVisitor  implements IPageViewStatsVisitor {

    private int kFrequentCnt;
    private static PageViewStatsAttribute kmostFreqView = PageViewStatsAttribute.KMOSTFREQUENT_VIEWS;

    public PageViewKFrequentStatsVisitor(int kFrequentCnt) {
        this.kFrequentCnt = kFrequentCnt;
    }

    /**
     * method generates K Most frequently visited wikipedia pages for specific page type using priority queue technique.
     * @param pageViewStatsMap
     * @return
     */
    public Map<PageViewStatsAttribute, List<PageViewStats>> visitPageViewStats(Map<String, PageViewStats> pageViewStatsMap) {
        Queue<PageViewStats> kmostFrequentPageViewQueue = new PriorityQueue(kFrequentCnt, new Comparator<PageViewStats>() {
            @Override
            public int compare(PageViewStats o1, PageViewStats o2) {
                return (int)(o1.getPageVisits() - o2.getPageVisits());
            }
        });

        int iter = 0;
        for (String pageKey : pageViewStatsMap.keySet()) {
            PageViewStats pageStats = pageViewStatsMap.get(pageKey);
            if (iter < kFrequentCnt) {
                kmostFrequentPageViewQueue.offer(pageStats);
            }
            else {
                if (kmostFrequentPageViewQueue.peek().getPageVisits() < pageStats.getPageVisits()) {
                    kmostFrequentPageViewQueue.poll();
                    kmostFrequentPageViewQueue.offer(pageStats);
                }
            }
            iter++;
        }
        Map<PageViewStatsAttribute, List<PageViewStats>> kFrequentPageStatsMap = new HashMap<>();
        kFrequentPageStatsMap.put(kmostFreqView, new ArrayList(kmostFrequentPageViewQueue));
        return kFrequentPageStatsMap;
    }
}
