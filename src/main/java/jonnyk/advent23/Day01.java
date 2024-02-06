package jonnyk.advent23;

import jonnyk.util.RegexUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Day01 {
    private static final Logger logger = LogManager.getLogger(Day01.class);

    public final static void main(String[] args) {
        Day01 day01 = new Day01();
        day01.part1();
        day01.part2();
    }

    public void part1() {
        try {
            Scanner scanner = getScanner();
            Pattern firstDigit = Pattern.compile("^[^\\d]*(\\d)");
            Pattern lastDigit = Pattern.compile("(\\d)[^\\d]*$");
            Integer total = 0;

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Integer lineValue = Integer.valueOf(RegexUtil.getFirstMatch(firstDigit, line) +
                        RegexUtil.getFirstMatch(lastDigit, line));

                logger.info("Part1 Val={} Line={}", lineValue, line);
                total += lineValue;
            }

            logger.info("Part1 Total={}", total);
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void part2() {
        try {
            Scanner scanner = getScanner();
            Pattern digitPattern = Pattern.compile("(?=(\\d|one|two|three|four|five|six|seven|eight|nine)).");
            Integer total = 0;
            Map<String, String> numMap = Map.of("one", "1", "two", "2", "three", "3", "four", "4",
                    "five", "5", "six", "6", "seven", "7", "eight", "8", "nine", "9");

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                Integer lineValue = Integer.valueOf(getNumString(numMap, RegexUtil.getFirstMatch(digitPattern, line)) +
                        getNumString(numMap, RegexUtil.getLastMatch(digitPattern, line)));

                logger.info("Part2 Val={} Line={}", lineValue, line);
                total += lineValue;
            }

            logger.info("Part2 Total={}", total);
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    String getNumString(Map<String, String> map,
                        String value) {
        return map.getOrDefault(value, value);
    }

    public Scanner getScanner()
            throws IOException {
        File input = new File("/apps/adventofcode/2023/01_input.txt");
        return new Scanner(input);
    }
}