package com.WikipediaStats.constants;

import com.WikipediaStats.models.*;

/**
 * Created by Preet on 1/27/2019.
 */
/*defines the different type of Page visits that can be encountered within Wikipedia ecosystem with different string patterns*/
public enum WikiPageType {
    ARTICLE(""),
    USER("User"),
    USER_TALK("User_talk"),
    TEMPLATE("Template"),
    MAIN_PAGE("Main_Page"),
    WIKIPEDIA("Wikipedia"),
    MEDIA_WIKI("Media"),
    WIKIPEDIA_TALK("Wikipedia_talk"),
    FILE("File"),
    SPECIAL("Special"),
    TALK("Talk"),
    CATEGORY("Category"),
    HELP("Help"),
    UNKNOWN(".*")
    ;

    // This regex pattern is the namespace pattern for wiki page which determines type of -
    // - wikipedia page stat that is recorded in the logs.
    // E.g.
    // a) if stat line in logs commons.m User:..." then its user page
    // b) line in logs is commons.m Category:..." then its category page ..etc.
    private final String wikiPageTypeRegex;

    WikiPageType(String wikiPageTypePrefix) {
        this.wikiPageTypeRegex = wikiPageTypePrefix;
    }

    public String getWikiPageTypePrefix() {
        return wikiPageTypeRegex;
    }

    public static WikiPageType getWikiPageType(String wikiPageTypeRegex) {
        WikiPageType wikiPgType = null;

        for (WikiPageType pageType: WikiPageType.values()) {
            if (pageType.getWikiPageTypePrefix().equalsIgnoreCase(wikiPageTypeRegex)) {
                wikiPgType = pageType;
                break;
            }
        }
        return wikiPgType;
    }
}
