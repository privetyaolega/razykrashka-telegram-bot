package com.razykrashka.bot.stage.meeting.view.utils;

public class TextFormatter {

    public static String getBoldString(String text) {
        return String.format("<b>%s</b>", text);
    }

    public static String getItalicString(String text) {
        return String.format("<i>%s</i>", text);
    }

    public static String getBoldString(Integer text) {
        return getBoldString(String.valueOf(text));
    }

    public static String getCodeString(String text) {
        return String.format("<code>%s</code>", text);
    }

    public static String getFramedString(String text) {
        return String.format("⋅%s⋅", text);
    }

    public static String getFramedString(Integer text) {
        return getFramedString(String.valueOf(text));
    }

    public static String getLink(String textLink, String url) {
        return String.format("<a href='%s'>%s</a>", url, textLink);
    }
}