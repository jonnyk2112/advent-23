package jonnyk.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

    public static String getFirstMatch(Pattern pattern,
                                       String value) {
        Matcher matcher = pattern.matcher(value);
        matcher.find();
        return matcher.group(1);
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
