package jonnyk.advent23;

import jonnyk.util.AdventUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 {

    static final String FILE_PREFIX = "03";
    static final Logger logger = LogManager.getLogger(Day03.class);
    Pattern partNumberPattern = Pattern.compile("(\\d+)");
    Pattern symbolPattern = Pattern.compile("[^.\\d]");

    public final static void main(String[] args) {
        Day03 day03 = new Day03();
        // part1() was the first attempt, it works but 1a is better, use it instead
        // day03.part1();
        day03.part1a();
        day03.part2();
    }

    public void part1() {
        logger.info("-----------------------------");
        logger.info("Part1");
        logger.info("-----------------------------");
        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            List<Line> lineList = getLineList(scanner, true);
            Integer total = 0;

            // all var names row=index of the row in the file, idx=index of partnum or symbol in list, pos=character position in the line
            // this searches each line efficiently, going through each partNumber once, and each symbol only once per partNumber
            for (int searchLineRow = 0; searchLineRow < lineList.size(); searchLineRow++) {
                Line currentLine = lineList.get(searchLineRow);
                Map<Integer,Integer> symbolLineRowIdxMap = new HashMap<>();
                for (int symbolLineRow = Math.max(0, searchLineRow - 1); symbolLineRow < Math.min(lineList.size(), searchLineRow + 2); symbolLineRow++) {
                    // initialize the map of rows->list indices to search for symbols to loop through
                    // go through symbols for current - 1, current, current + 1 only once each, tracking idx values
                    symbolLineRowIdxMap.put(symbolLineRow, 0);
                }

                partNumberLoop:
                for (PartNumber partNumber : currentLine.partNumberList) {
                    for (int symbolLineRow : symbolLineRowIdxMap.keySet()) {
                        List<Symbol> symbolList = lineList.get(symbolLineRow).symbolList;
                        int symbolIdx = symbolLineRowIdxMap.get(symbolLineRow);
                        while (symbolIdx < symbolList.size() &&
                                symbolList.get(symbolIdx).pos <= partNumber.endPos) {
                            int symbolPos = symbolList.get(symbolIdx).pos;
                            if ((symbolPos >= partNumber.startPos - 1) &&
                                    (symbolPos <= partNumber.endPos)) {
                                // it's a valid part number, next to a symbol
                                logger.info("Part 1 Valid part Row={} PartNum={} SymbolRow={} Symbol={}@{}",
                                        searchLineRow, partNumber.partNumber, symbolLineRow, symbolList.get(symbolIdx).symbol, symbolPos);
                                total += partNumber.partNumber;
                                continue partNumberLoop;
                            } else {
                                // try the next symbol
                                symbolLineRowIdxMap.put(symbolLineRow, ++symbolIdx);
                            }
                        }
                    }
                    // after searching symbols from all 3 lines with pos before the end pos of this value, no match
                    logger.info("Part 1 Invalid part Row={} PartNum={}", searchLineRow, partNumber.partNumber);
                }
            }

            logger.info("-----------------------------");
            logger.info("Part1 Total={}", total);
            logger.info("-----------------------------");
            scanner.close();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    public void part1a() {
        final String PART = "Part 1a";
        // same as part1 but use linked lists to simplify
        logger.info("-----------------------------");
        logger.info(PART);
        logger.info("-----------------------------");
        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            List<Line> lineList = getLineList(scanner, true);
            Integer total = 0;

            for (int searchLineRow = 0; searchLineRow < lineList.size(); searchLineRow++) {
                Line currentLine = lineList.get(searchLineRow);
                List<Symbol> searchSymbols = new ArrayList<>();
                for (int symbolLineRow = Math.max(0, searchLineRow - 1); symbolLineRow < Math.min(lineList.size(), searchLineRow + 2); symbolLineRow++) {
                    // initialize the list of symbols to search for, each is the head of a linked list
                    if (lineList.get(symbolLineRow).symbolList.size() > 0) {
                        searchSymbols.add(lineList.get(symbolLineRow).symbolList.get(0));
                    }
                }

                partNumberLoop:
                for (PartNumber partNumber : currentLine.partNumberList) {
                    for (Symbol searchSymbol : searchSymbols) {
                        while ((searchSymbol != null) &&
                                (searchSymbol.pos <= partNumber.endPos)) {
                            if (isAdjacent(searchSymbol, partNumber)) {
                                logger.info("{} Row={} Valid part PartNum={} SymbolRow={} Symbol='{}'@{}",
                                        PART, searchLineRow, partNumber.partNumber, searchSymbol.row, searchSymbol.symbol, searchSymbol.pos);
                                total += partNumber.partNumber;
                                continue partNumberLoop;
                            } else {
                                // grab the next in the linked list
                                searchSymbols.set(searchSymbols.indexOf(searchSymbol), searchSymbol.nextSymbol);
                                searchSymbol = searchSymbol.nextSymbol;
                            }
                        }
                    }

                    // after searching symbols from all 3 lines with pos before the end pos of this value, no match
                    logger.info("{} Row={} Invalid part PartNum={}", PART, searchLineRow, partNumber.partNumber);
                }
            }

            logger.info("-----------------------------");
            logger.info("{} Total={}", PART, total);
            logger.info("-----------------------------");
            scanner.close();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    public void part2() {
        final String PART = "Part 2";
        logger.info("-----------------------------");
        logger.info(PART);
        logger.info("-----------------------------");
        try {
            Scanner scanner = AdventUtil.getScanner(FILE_PREFIX);
            List<Line> lineList = getLineList(scanner, false);
            Integer total = 0;

            for (int searchLineRow = 0; searchLineRow < lineList.size(); searchLineRow++) {
                if (lineList.get(searchLineRow).symbolList.size() == 0) {
                    // nothing to search for
                    logger.info("{} Row={} No candidates", PART, searchLineRow + 1);
                    continue;
                }

                // set up the search for the row
                Line currentLine = lineList.get(searchLineRow);
                List<PartNumber> searchPartNumbers = new ArrayList<>();
                for (int partNumberLineRow = Math.max(0, searchLineRow - 1);
                     partNumberLineRow < Math.min(lineList.size(), searchLineRow + 2);
                     partNumberLineRow++) {
                    // initialize the list of partNumbers to search for, each is the head of a linked list
                    if (lineList.get(partNumberLineRow).partNumberList.size() > 0) {
                        searchPartNumbers.add(lineList.get(partNumberLineRow).partNumberList.get(0));
                    }
                }

                // search each '*'
                symbolLoop:
                for (Symbol symbol : currentLine.symbolList) {
                    GearRatio gearRatio = new GearRatio();

                    partNumberLoop:
                    for (PartNumber searchPartNumber : searchPartNumbers) {
                        while ((searchPartNumber != null) &&
                                (searchPartNumber.startPos <= (symbol.pos + 1))) {
                            if (isAdjacent(symbol, searchPartNumber)) {
                                gearRatio.addPartNumber(searchPartNumber.partNumber);
                                if (gearRatio.isValidRatio()) {
                                    // found two, this one is a gear, on to the next
                                    total += gearRatio.ratio;
                                    logger.info("{} Row={} Found a gear Pos={} GearVal={} Current Total={}",
                                            PART, searchLineRow + 1, symbol.pos + 1, gearRatio.ratio, total);
                                    continue symbolLoop;
                                }
                            }

                            // grab the next in the linked list
                            searchPartNumbers.set(searchPartNumbers.indexOf(searchPartNumber), searchPartNumber.nextPartNumber);
                            searchPartNumber = searchPartNumber.nextPartNumber;
                        }
                    }

                    // after searching part numbers from all 3 lines with pos before the end pos of this value, no match
                    logger.info("{} Row={} Not a gear Pos={} Current Total={}", PART, searchLineRow + 1, symbol.pos + 1, total);
                }
            }

            logger.info("-----------------------------");
            logger.info("{} Total={}", PART, total);
            logger.info("-----------------------------");
            scanner.close();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

    List<Line> getLineList(Scanner scanner,
                           boolean allSymbols) {
        List<Line> lineList = new ArrayList<>();
        while (scanner.hasNext()) {
            String inputLine = scanner.nextLine();
            Line line = new Line();

            Matcher partNumberMatcher = partNumberPattern.matcher(inputLine);
            while (partNumberMatcher.find()) {
                line.partNumberList.add(new PartNumber(
                        Integer.valueOf(partNumberMatcher.group()), partNumberMatcher.start(), partNumberMatcher.end()));
                if (line.partNumberList.size() > 1) {
                    // set up linked list
                    line.partNumberList.get(line.partNumberList.size() - 2).nextPartNumber = line.partNumberList.get(line.partNumberList.size() - 1);
                }
            }

            Matcher symbolMatcher = symbolPattern.matcher(inputLine);
            while (symbolMatcher.find()) {
                if (allSymbols || symbolMatcher.group().equals("*")) {
                    line.symbolList.add(new Symbol(symbolMatcher.group(), lineList.size(), symbolMatcher.start()));
                    if (line.symbolList.size() > 1) {
                        // set up linked list
                        line.symbolList.get(line.symbolList.size() - 2).nextSymbol = line.symbolList.get(line.symbolList.size() - 1);
                    }
                }
            }
            lineList.add(line);
        }

        return lineList;
    }

    boolean isAdjacent(Symbol symbol,
                       PartNumber partNumber) {
        return (symbol != null) &&
                (partNumber != null) &&
                (symbol.pos >= partNumber.startPos - 1) &&
                (symbol.pos <= partNumber.endPos);
    }

    class PartNumber {
        int partNumber, startPos, endPos;
        PartNumber nextPartNumber = null;
        PartNumber(int partNumber,
                   int startPos,
                   int endPos) {
            this.partNumber = partNumber;
            this.startPos = startPos;
            this.endPos = endPos;
        }
    }

    class Symbol {
        int row, pos;
        String symbol;
        Symbol nextSymbol = null;
        Symbol(String symbol, int row, int pos) {
            this.symbol = symbol;
            this.row = row;
            this.pos = pos;
        }
    }

    class Line {
        List<PartNumber> partNumberList = new ArrayList<>();
        List<Symbol> symbolList = new ArrayList<>();
    }

    class GearRatio {
        int foundCnt = 0;
        int ratio = 1;

        void addPartNumber(int partNumber) {
            foundCnt++;
            ratio *= partNumber;
        }

        boolean isValidRatio() {
            return foundCnt == 2;
        }
    }
}