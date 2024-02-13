package jonnyk.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdventUtil {

    /* FILE */

    public static Scanner getScanner(String prefix)
            throws IOException {
        File input = new File("/apps/adventofcode/2023/" + prefix + "_input.txt");
        return new Scanner(input);
    }

    /* REGEX */
    public static Integer getFirstInt(Pattern pattern,
                                      String value) {
        return Integer.valueOf(getFirstMatch(pattern, value));
    }

    public static String getFirstMatch(Pattern pattern,
                                       String value) {
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        return matcher.group(1);
    }

    public static List<Long> getLongList(Pattern pattern,
                                         String value) {
        Matcher matcher = pattern.matcher(value);
        List<Long> longList = new ArrayList<>();
        while (matcher.find()) {
            longList.add(Long.valueOf(matcher.group()));
        }

        return longList;
    }

    public static Set<Integer> getIntSet(Pattern pattern,
                                         String value) {
        Matcher matcher = pattern.matcher(value);
        Set<Integer> intSet = new HashSet<>();
        while (matcher.find()) {
            intSet.add(Integer.valueOf(matcher.group(1)));
        }

        return intSet;
    }

    public static String getLastMatch(Pattern pattern,
                                      String value) {
        Matcher matcher = pattern.matcher(value);
        String val = null;
        while (matcher.find()) {
            val = matcher.group(matcher.groupCount());
        }
        return val;
    }

    public static String logHeader(String puzzle,
                                   Logger logger) {
        logger.info("-----------------------------");
        logger.info("Puzzle: {}", puzzle);
        logger.info("-----------------------------");
        return puzzle;
    }

    public static String logHeader(String puzzle,
                                   Logger logger,
                                   Level newLevel) {
        logLevel(logger, newLevel);
        return logHeader(puzzle, logger);
    }

    public static void logLevel(Logger logger,
                                Level newLevel) {
        Configurator.setLevel(logger, Level.INFO);
    }
}
