package com.WikipediaStats;

import com.WikipediaStats.constants.PageViewStatsAttribute;
import com.WikipediaStats.constants.WikiPageType;
import com.WikipediaStats.models.PageViewStats;
import com.WikipediaStats.visitors.PageViewKFrequentStatsVisitor;
import com.WikipediaStats.visitors.PageViewMinMaxStatsVisitor;
import com.WikipediaStats.interfaces.IPageViewStatsVisitor;
import com.WikipediaStats.processors.WikiLineProcessor;
import com.WikipediaStats.aggregators.WikiStatsAggregator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.System;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import java.time.Duration;
import java.time.Instant;


/**
 * Created by Preet on 1/27/2019.
 */
public class WikiMain {
    private static final Logger logger = LogManager.getLogger(WikiMain.class.getName());
    private static final String commentLineRegex = "#";
    private static final int kFrequentPages = 5;

    private static final EnumMap<WikiPageType, List<IPageViewStatsVisitor>> pageViewVisitorsMap =
            new EnumMap<WikiPageType, List<IPageViewStatsVisitor>>(WikiPageType.class);
            static {
                pageViewVisitorsMap.put(WikiPageType.ARTICLE, Arrays.asList(new PageViewMinMaxStatsVisitor()));
                pageViewVisitorsMap.put(WikiPageType.CATEGORY, Arrays.asList(new PageViewKFrequentStatsVisitor(kFrequentPages)));
            }

    private static final WikiStatsAggregator wikiStatsAggregator = new WikiStatsAggregator(pageViewVisitorsMap);
    private static final String wikiDataLineDelimiter = " ";

    private static final WikiLineProcessor wikiLineProcessor = new WikiLineProcessor(wikiStatsAggregator, wikiDataLineDelimiter);


    public static void main(String[] args) {
        String dirPath = args[0];
        logger.info("Directory path string for processing {} ", dirPath);

        int fileCnt = 1;
        try(Stream<Path> paths= Files.walk(Paths.get(dirPath)) ) {
            paths.filter(Files::isRegularFile).forEach(WikiMain::processWikiFile);
        }

        catch (Exception ex) {
            logger.error("Exception occurred in processing Wikipedia stats files in a directory {}: Exception - {}", dirPath, ex);
        }
    }

    private static void processWikiFile(Path path) {
        logger.info("processing file " + path.getFileName());

        try (Stream<String> lines = Files.lines(path))
        {
            // For every day file we load from disk we will clear out previous cache from the memory to process per day stats.
            wikiStatsAggregator.clearOutAggregation();

            Instant startTime = Instant.now();

            Map<PageViewStatsAttribute, List<PageViewStats>> pageTypeFinalStatsMap = new HashMap();

            lines.filter(line-> !line.startsWith(commentLineRegex)).forEach(line -> wikiLineProcessor.processLineForPageStats(line));
            for (WikiPageType pageType : pageViewVisitorsMap.keySet()) {
                pageTypeFinalStatsMap.putAll(wikiStatsAggregator.processFinalStatsForPageType(pageType));
            }

            Instant endTime = Instant.now();

            Duration timeElapsed = Duration.between(startTime, endTime);
            System.out.println("Time spent in processing the above file is " + timeElapsed.getSeconds() + " seconds ");

            pageTypeFinalStatsMap.forEach( (k,v) -> {
                System.out.println(k.getPageStatAttributeName());
                System.out.println("\t" + "Page Type: " + v.get(0).getPageType().toString());

                for(PageViewStats pgStats: v) {
                    System.out.println("\t\t" + "Title: " + pgStats.getPageTitle() + "  Page Visits: " + pgStats.getPageVisits());
                }
            });
            System.out.println();
        }

        catch (Exception ex) {
            logger.error("Exception occurred in processing Wikipedia statistics log file {}: Exception - {}", path.toString(), ex);
        }
    }

}
