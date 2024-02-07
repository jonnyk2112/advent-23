package jonnyk.advent23;

import jonnyk.util.AdventUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day02 {
    private static final Logger logger = LogManager.getLogger(Day02.class);

    public final static void main(String[] args) {
        Day02 day02 = new Day02();
        day02.part1();
        day02.part2();
    }

    public void part1() {
        try {
            Scanner scanner = AdventUtil.getScanner("02");
            Pattern gameNumberPattern = Pattern.compile("(\\d+)[:]");
            Pattern gamePattern = Pattern.compile("[:|;]([^;])*");
            Pattern gameRevealPattern = Pattern.compile("(\\d* (green|red|blue))");
            Map<Cube, Integer> maxCounts = Map.of(Cube.red, 12, Cube.green, 13, Cube.blue, 14);
            Integer total = 0;

            scannerLoop:
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                int gameNumber = AdventUtil.getFirstInt(gameNumberPattern, line);
                List<GameReveal> gameRevealList = getGameReveals(gamePattern, gameRevealPattern, line);

                for (GameReveal gameReveal : gameRevealList) {
                    for (Cube gameCube : gameReveal.keySet()) {
                        if (gameReveal.get(gameCube) > maxCounts.get(gameCube)) {
                            logger.info("Part1 Game {} excluded ({} > {} {} cubes) Line={}",
                                    gameNumber, gameReveal.get(gameCube), maxCounts.get(gameCube), gameCube, line);
                            continue scannerLoop;
                        }
                    }
                }

                logger.info("Part1 Game {} included Line={}", gameNumber, line);
                total += gameNumber;
            }

            logger.info("Part1 Total={}", total);
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void part2() {
        try {
            Scanner scanner = AdventUtil.getScanner("02");
            Pattern gameNumberPattern = Pattern.compile("(\\d+)[:]");
            Pattern gamePattern = Pattern.compile("[:|;]([^;])*");
            Pattern gameRevealPattern = Pattern.compile("(\\d* (green|red|blue))");
            GameReveal maxGameReveal = new GameReveal();
            Integer total = 0;

            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                int gameNumber = AdventUtil.getFirstInt(gameNumberPattern, line);
                List<GameReveal> gameRevealList = getGameReveals(gamePattern, gameRevealPattern, line);
                maxGameReveal.reset();

                for (GameReveal gameReveal : gameRevealList) {
                    for (Cube gameCube : gameReveal.keySet()) {
                        if (gameReveal.get(gameCube) > maxGameReveal.get(gameCube)) {
                            maxGameReveal.put(gameCube, gameReveal.get(gameCube));
                        }
                    }
                }

                logger.info("Part1 Game {} Power={} Line={}", gameNumber, maxGameReveal.getPower(), line);
                total += maxGameReveal.getPower();
            }

            logger.info("Part1 Total={}", total);
            scanner.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    List<GameReveal> getGameReveals(Pattern gamePattern,
                                    Pattern gameRevealPattern,
                                    String line) {

        Matcher gameMatcher = gamePattern.matcher(line);
        String val = null;
        List<GameReveal> gameList = new ArrayList<>();
        while (gameMatcher.find()) {
            String gameStr = gameMatcher.group(gameMatcher.groupCount());
            gameStr = gameMatcher.group();
            GameReveal game = new GameReveal();
            Matcher gameDetailMatcher = gameRevealPattern.matcher(gameStr);

            while (gameDetailMatcher.find()) {
                String gameDetailStr = gameDetailMatcher.group(1);
                int spaceIdx = gameDetailStr.indexOf(' ');
                game.put(Cube.valueOf(gameDetailStr.substring(spaceIdx).trim()),
                        Integer.valueOf(gameDetailStr.substring(0, spaceIdx)));
            }
            gameList.add(game);
        }

        return gameList;
    }

    enum Cube {
        red, green, blue;
    }

    class GameReveal extends HashMap<Cube, Integer> {
        int getPower() {
            int power = 1;
            for (int cubePower : values()) {
                power *= cubePower;
            }
            return power;
        }

        void reset() {
            for (Cube cube : Cube.values()) {
                put(cube, 0);
            }
        }
    }
}