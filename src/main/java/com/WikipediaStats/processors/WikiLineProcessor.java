package com.WikipediaStats.processors;

import com.WikipediaStats.aggregators.WikiStatsAggregator;
import com.WikipediaStats.constants.WikiPageType;
import com.WikipediaStats.constants.WikiPageViewTokenPosition;
import com.WikipediaStats.models.PageViewStats;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * Created by Preet on 1/28/2019.
 */
// This class processes each line and generates model of PageViewStats per line.
public class WikiLineProcessor {
    private static final Logger logger = LogManager.getLogger(WikiLineProcessor.class.getName());
    private WikiStatsAggregator pageStatsAggregator;

    private String lineDelimiter;
    private static final Pattern wikiPageTitleWithTypePtrn = Pattern.compile("(\\S+?):(\\S+)");
    private static final Pattern wikiPageTitleNoTypePtrn = Pattern.compile("(\\S+)");
    private static final Pattern wikiPageVisitPtrn = Pattern.compile("(\\d+)");

    private static final Pattern wikiNonArticlePtrn = Pattern.compile("(?i:MAIN_PAGE|-)");

    private Set<WikiPageType> processablePageTypes;

    public WikiLineProcessor(WikiStatsAggregator pageStatsAggregator, String lineDelimiter) {
        this.pageStatsAggregator = pageStatsAggregator;
        this.processablePageTypes = pageStatsAggregator.getProcessablePageTypes();
        this.lineDelimiter = lineDelimiter;
    }

    /**
     * Method processes each line from wiki stats file and forwards to the wikistatsaggregator -
     * - to store in memory for final stat operation of min, max, kfrequent etc.
     * @param line parameter is full line from wiki stats log file.
     * @return boolean return whether line is processed successfully or not.
    */
    public boolean processLineForPageStats(String line) {
        try {
            String[] wikiTokens = line.split(lineDelimiter);
            String pageTitleToken = wikiTokens[WikiPageViewTokenPosition.PAGE_TITLE.ordinal()];
            String pageVisitsToken = wikiTokens[WikiPageViewTokenPosition.PAGE_VISITS.ordinal()];

            Matcher pageTitleWithTypeMatcher = wikiPageTitleWithTypePtrn.matcher(pageTitleToken);
            Matcher pageTitleWithNoTypeMatcher = wikiPageTitleNoTypePtrn.matcher(pageTitleToken);

            PageViewStats pageStats = null;
            boolean pageTitleWithTypeMatch = pageTitleWithTypeMatcher.matches();
            boolean pageTitleWithNoTypeMatch = pageTitleWithNoTypeMatcher.matches();

            if (!pageTitleWithNoTypeMatch && !pageTitleWithTypeMatch) {
                logger.error("Wikipedia page stats line {} does not match extraction pattern for getting page title with or without pagetype prefix", line);
                return false;
            }

            // Essentially page type prefix indicates type of wikipedia page like Category, Special, or no prefix for Article pages.
            String pageTypePrefix = (pageTitleWithTypeMatch) ? pageTitleWithTypeMatcher.group(1): "";
            WikiPageType pageType = WikiPageType.getWikiPageType(pageTypePrefix);

            if (!processablePageTypes.contains(pageType)) {
                return false;
            }

            //ARTICLE Page namespace can have Main_Page or '-' -  we want to ignore this type page as it is not technically an article page.
            if (pageType == WikiPageType.ARTICLE && wikiNonArticlePtrn.matcher(pageTitleToken).matches()) {
                return false;
            }

            String pageTitle = (pageTitleWithTypeMatch) ? pageTitleWithTypeMatcher.group(2):pageTitleWithNoTypeMatcher.group(1);

            Matcher pageVisitsMatcher = wikiPageVisitPtrn.matcher(pageVisitsToken);
            if (!pageVisitsMatcher.matches())
                return false;

            long pageVisits = Long.valueOf(pageVisitsMatcher.group(0));
            pageStats = new PageViewStats(pageTitle, pageTypePrefix, null, pageVisits);
            pageStatsAggregator.acceptPageViewStats(pageStats);
            return true;
        }
        catch (Exception ex) {
            logger.error("Exception occurred while processing line {} for stats: Exception - {}", line, ex);
        }
        return false;
    }
}
