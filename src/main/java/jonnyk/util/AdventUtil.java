package jonnyk.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
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
}
