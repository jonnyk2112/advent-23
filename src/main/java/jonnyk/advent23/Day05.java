package jonnyk.advent23;

import jonnyk.util.AdventUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.*;
import java.util.regex.Pattern;

public class Day05 {
    static final String FILE_PREFIX = "05";
    static final Logger logger = LogManager.getLogger(Day05.class);
    Pattern seedPattern = Pattern.compile("\\d+");
    Pattern transformationNamePattern = Pattern.compile("(.+):");
    Pattern rangePattern = Pattern.compile("\\d+");

    public final static void main(String[] args) {
        Day05 day05 = new Day05();
        day05.part1();
        day05.part2();
    }

    public void part1() {
        String puzzle = AdventUtil.logHeader("05.1", logger);

        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            Almanac almanac = getAlmanac(scanner);
            TransValue minTransVal = new TransValue();

            for (long seed : almanac.seedList) {
                processSeed(almanac, seed, minTransVal, puzzle);
            }

            logger.info("{} MinSeed={} MinVal={}", puzzle, minTransVal.seed, minTransVal.location);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void part2() {
        String puzzle = AdventUtil.logHeader("05.2", logger, Level.INFO);
        TransValue minTransVal = new TransValue();

        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            Almanac almanac = getAlmanac(scanner);

            for (int seedIdx = 0; seedIdx < almanac.seedList.size(); seedIdx += 2) {
                logger.info("{} BaseSeed={} Iterations={}", puzzle, almanac.seedList.get(seedIdx), almanac.seedList.get(seedIdx + 1));
                for (long seedOffset = 0; seedOffset < almanac.seedList.get(seedIdx + 1); seedOffset++) {
                    processSeed(almanac, almanac.seedList.get(seedIdx) + seedOffset, minTransVal, puzzle);
                }
            }

            logger.info("{} MinSeed={} MinVal={}", puzzle, minTransVal.seed, minTransVal.location);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    void processSeed(Almanac almanac,
                     long seed,
                     TransValue minTransVal,
                     String puzzle) {

        long val = seed;

        transLoop:
        for (TransMap transMap : almanac.transMapList) {
            for (Range range : transMap.rangeList) {
                if (range.isPast(val)) {
                    break;
                } else if (range.isIsRange(val)) {
                    logger.debug("{} Seed={} Val={} {} <= {} < {} Diff={} Trans={}",
                            puzzle, seed, range.transform(val), range.start, val, range.end, range.diff ,transMap.name);
                    val = range.transform(val);
                    continue transLoop;
                }
            }
            // no range found
            logger.debug("{} Seed={} Val={} No transformation Trans={}", puzzle, seed, val, transMap.name);
        }

        if (minTransVal.isLower(val)) {
            logger.info("{} Seed={} Val={} Replaces MinSeed={} MinVal={}", puzzle, seed, val, minTransVal.seed, minTransVal.location);
            minTransVal.replace(seed, val);
        } else {
            logger.debug("{} Seed={} Val={} !Replaces MinSeed={} MinVal={}", puzzle, seed, val, minTransVal.seed, minTransVal.location);
        }
    }

    public Almanac getAlmanac(Scanner scanner) {
        Almanac almanac = new Almanac();
        almanac.seedList = AdventUtil.getLongList(seedPattern, scanner.nextLine());
        scanner.nextLine();

        transLoop:
        while (scanner.hasNext()) {
            TransMap trans = new TransMap();
            almanac.transMapList.add(trans);
            trans.name = AdventUtil.getFirstMatch(transformationNamePattern, scanner.nextLine());
            while (scanner.hasNext()) {
                String row = scanner.nextLine();
                if (row.length() == 0) {
                    // end of transformation
                    continue transLoop;
                }

                List<Long> rangeList = AdventUtil.getLongList(rangePattern, row);
                trans.rangeList.add(new Range(rangeList.get(1), rangeList.get(2), rangeList.get(0) - rangeList.get(1)));
            }
        }

        for (TransMap trans : almanac.transMapList) {
            trans.rangeList.sort(new Comparator<Range>() {
                @Override
                public int compare(Range o1, Range o2) {
                    return Long.compare(o1.start, o2.start);
                }
            });
        }

        return almanac;
    }

    class Range {
        long start, end, len, diff;
        Range(long start, long len, long diff) {
            this.start = start;
            this.end = start + len;
            this.len = len;
            this.diff = diff;
        }
        boolean isIsRange(long val) {
            return (val >= start) && (val < end);
        }
        boolean isPast(long val) {
            return val < this.start;
        }
        long transform(long val) {
            return val + diff;
        }
        public String toString() {
            return this.start + "-" + this.end;
        }
    }

    class TransMap {
        String name;
        List<Range> rangeList = new ArrayList<>();
    }

    class TransValue {
        long seed = -1;
        long location = -1;

        boolean isLower(long newLocation) {
            return (this.location < 0) || (newLocation < this.location);
        }

        void replace(long newSeed,
                     long newLocation) {
            this.seed = newSeed;
            this.location = newLocation;
        }
    }

    class Almanac {
        List<Long> seedList;
        List<TransMap> transMapList = new ArrayList<>();
    }
}
