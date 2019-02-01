package com.WikipediaStats.processors;

import com.WikipediaStats.aggregators.WikiStatsAggregator;
import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.constants.WikiPageType;
import com.WikipediaStats.interfaces.IPageViewStatsVisitor;
import com.WikipediaStats.models.PageViewStats;
import com.WikipediaStats.visitors.PageViewKFrequentStatsVisitor;
import com.WikipediaStats.visitors.PageViewMinMaxStatsVisitor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Preet on 1/29/2019.
 */
public class WikiLineProcessorTest {
    private static final int kFrequentPages = 3;

    private static final EnumMap<WikiPageType, List<IPageViewStatsVisitor>> pageViewVisitorsMap =
            new EnumMap<WikiPageType, List<IPageViewStatsVisitor>>(WikiPageType.class);
    static {
        pageViewVisitorsMap.put(WikiPageType.ARTICLE, Arrays.asList(new PageViewMinMaxStatsVisitor()));
        pageViewVisitorsMap.put(WikiPageType.CATEGORY, Arrays.asList(new PageViewKFrequentStatsVisitor(kFrequentPages)));
    }
    private static final String wikiLineDelimiter = " ";

    private static final WikiStatsAggregator wikiStatsAggregator = new WikiStatsAggregator(pageViewVisitorsMap);

    private static final WikiLineProcessor wikiLineProcessor = new WikiLineProcessor(wikiStatsAggregator, wikiLineDelimiter);

    @BeforeClass
    public static void setUp() throws Exception {

    }

    @AfterClass
    public static void tearDown() throws Exception {

    }

    @Before
    public void cleanAggregation() throws Exception {
        wikiStatsAggregator.clearOutAggregation();
    }

    @Test
    public void testProcessLineWithCategoryPrefix() throws Exception {
        String test1 = "en.z Category:Mathematics 4 I3J1";
        assertTrue(wikiLineProcessor.processLineForPageStats(test1));
    }

    @Test
    public void testProcessLineWithNoCategoryPrefix() throws Exception {
        String test1 = "en.z Big_Ben 3 K2P1";
        assertTrue(wikiLineProcessor.processLineForPageStats(test1));
    }

    @Test
    public void testProcessLineNotProperFormat() throws Exception {
        String testMalformed = "en.zBigBen 3K2P1";
        assertFalse(wikiLineProcessor.processLineForPageStats(testMalformed));
    }

    @Test
    public void testWikiAggregation() throws Exception {
        String testLine1 = "en.z Big_Ben 3 K2P1";
        String testLine2 = "en.z Big_Ben 7 K3P4";
        String testLine3 = "en.pt Ted_Talk 4 L2M2";

        String testLine4 = "en.z Category:Fashion 6 K3P3";
        String testLine5 = "en.z Category:Mathematics 9 K3P6";
        String testLine6 = "en.z Category:English 14 C7J7";
        String testLine7 = "en.z Category:Computers 20 A4B7C3D6";

        wikiLineProcessor.processLineForPageStats(testLine1);
        wikiLineProcessor.processLineForPageStats(testLine2);
        wikiLineProcessor.processLineForPageStats(testLine3);

        long expectedMinPageAggrCntForArticle = 4;
        long expectedMaxPageAggrCntForArticle = 10;

        long expectedPageAggrCntForCategory = 6;

        Map<PageViewStatsAttribute, List<PageViewStats>> resultStats = wikiStatsAggregator.processFinalStatsForPageType(WikiPageType.ARTICLE);

        assertEquals(expectedMaxPageAggrCntForArticle, resultStats.get(PageViewStatsAttribute.MAX_VIEWS).get(0).getPageVisits());
        assertEquals(expectedMinPageAggrCntForArticle, resultStats.get(PageViewStatsAttribute.MIN_VIEWS).get(0).getPageVisits());

        wikiLineProcessor.processLineForPageStats(testLine4);
        wikiLineProcessor.processLineForPageStats(testLine6);
        wikiLineProcessor.processLineForPageStats(testLine5);
        wikiLineProcessor.processLineForPageStats(testLine7);

        resultStats = wikiStatsAggregator.processFinalStatsForPageType(WikiPageType.CATEGORY);

        List<Long> actualStatsForCategory = new ArrayList();

        for (PageViewStats catPgStat: resultStats.get(PageViewStatsAttribute.KMOSTFREQUENT_VIEWS)) {
            actualStatsForCategory.add(catPgStat.getPageVisits());
        }

        Collections.sort(actualStatsForCategory);
        Long[] expectedKFreqStats = new Long[]{9L, 14L, 20L};

        Long[] actualCategoryLongCnts = new Long[actualStatsForCategory.size()];
        actualStatsForCategory.toArray(actualCategoryLongCnts);

        assertArrayEquals(expectedKFreqStats, actualCategoryLongCnts);

    }
}