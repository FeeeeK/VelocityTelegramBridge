package me.axd1x8a.velocitytelegrambridge.utils;

import java.util.Map;

public class StringFormatter {

    public static String format(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
