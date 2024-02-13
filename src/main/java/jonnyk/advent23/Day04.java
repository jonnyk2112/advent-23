package jonnyk.advent23;

import jonnyk.util.AdventUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day04 {
    static final String FILE_PREFIX = "04";
    static final Logger logger = LogManager.getLogger(Day04.class);
    Pattern rowPattern = Pattern.compile("^Card\\s+\\d+:(.*)(?=\\|)(.*)");
    Pattern numberPattern = Pattern.compile("(\\d+)");

    public final static void main(String[] args) {
        Day04 day04 = new Day04();
        //day04.part1();
        day04.part2();
    }

    public void part1() {
        String puzzle = "04.1";
        logger.info("-----------------------------");
        logger.info("Puzzle: {}", puzzle);
        logger.info("-----------------------------");
        String logEntry = puzzle + " Row=";
        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            List<Row> rowList = getRowList(scanner);
            int total = 0;

            for (int rowIdx = 0; rowIdx < rowList.size(); rowIdx++) {
                Row row = rowList.get(rowIdx);
                Set<Integer> matchSet = new HashSet<>(row.winningNumbers);
                matchSet.retainAll(row.cardNumbers);
                if (matchSet.size() == 0) {
                    logger.info("{}{} No winners Score=0", logEntry, rowIdx + 1);
                } else {
                    int score = (int) Math.pow((long) 2, matchSet.size() - 1);
                    logger.info("{}{} Score={} Winners={}", logEntry, rowIdx + 1, score, matchSet.toString());
                    total += score;
                }
            }

            logger.info("{} Total={}", puzzle, total);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void part2() {
        String puzzle = "04.2";
        logger.info("-----------------------------");
        logger.info("Puzzle: {}", puzzle);
        logger.info("-----------------------------");
        String logEntry = puzzle + " Row=";
        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            List<Row> rowList = getRowList(scanner);
            int cardCount = 0;

            for (int rowIdx = 0; rowIdx < rowList.size(); rowIdx++) {
                logger.info("{}{} Starting CardCount={}", logEntry, rowIdx + 1, cardCount);
                cardCount += processCard(logEntry, rowList.get(rowIdx), rowIdx, rowList, null);
            }

            logger.info("{} Total CardCount={}", puzzle, cardCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    int processCard(String logEntry, Row row, int rowIdx, List<Row>rowList, String callDesc) {
        int cardCount = 1;
        if (row.matches.size() == 0) {
            logger.trace("{}{} No winners Score=0 CallDesc={}", logEntry, rowIdx + 1, callDesc);
        } else {
            String newCallDesc = ((callDesc == null) ? "" : callDesc + ":") + (rowIdx+1);
            logger.trace("{}{} WinCount={} Recursing {}-{} {}",
                    logEntry, rowIdx + 1, row.matches.size(), rowIdx + 2, Math.min(rowIdx + row.matches.size(), rowList.size()) + 1, newCallDesc);
            for (int freeCardRow = rowIdx + 1; freeCardRow < Math.min(rowIdx + 1 + row.matches.size(), rowList.size()); freeCardRow++) {
                // recurse
                cardCount += processCard(logEntry, rowList.get(freeCardRow), freeCardRow, rowList, newCallDesc);
            }
            logger.trace("{}{} CardCount={} Winners={} CallDesc={}", logEntry, rowIdx + 1, cardCount, row.matches.toString(), callDesc);
        }

        return cardCount;
    }

    public List<Row> getRowList(Scanner scanner) {
        List<Row> rowList = new ArrayList<>();
        while (scanner.hasNext()) {
            Matcher rowMatcher = rowPattern.matcher(scanner.nextLine());
            Row row = new Row();
            rowMatcher.find();
            row.winningNumbers = AdventUtil.getIntSet(numberPattern, rowMatcher.group(1));
            row.cardNumbers = AdventUtil.getIntSet(numberPattern, rowMatcher.group(2));
            row.matches = new HashSet<>(row.winningNumbers);
            row.matches.retainAll(row.cardNumbers);

            rowList.add(row);
        }
        return rowList;
    }

    class Row {
        Set<Integer> winningNumbers = null;
        Set<Integer> cardNumbers = null;
        Set<Integer> matches = null;
    }
}
