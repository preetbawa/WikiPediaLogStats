package com.WikipediaStats.models;

import com.WikipediaStats.constants.*;

/**
 * Created by Preet on 1/27/2019.
 */
public class PageViewStats {
    private String projectCode;
    private String pageTitle;
    private long pageVisits;
    private WikiPageType pageType;

    /**
     * @param pageTitle
     * @param pageNsPrefix
     * @param projectCode - this is  argument passed in as null mostly as likely Stats computed are for a page by its title or page type.
     */
    public PageViewStats(String pageTitle, String pageNsPrefix, String projectCode, long pageVisits) {
        this.projectCode = projectCode;
        this.pageTitle = pageTitle;
        this.pageType = WikiPageType.getWikiPageType(pageNsPrefix);
        this.pageVisits = pageVisits;
    }

    public String getProjectCode() { return projectCode; }

    public String getPageTitle() {
        return pageTitle;
    }

    public WikiPageType getPageType() {
        return pageType;
    }

    public long getPageVisits() { return pageVisits; }

    public void setPageVisits(long newPageVisits, boolean increment) {
        pageVisits = increment? pageVisits + newPageVisits : newPageVisits;
    }
}
