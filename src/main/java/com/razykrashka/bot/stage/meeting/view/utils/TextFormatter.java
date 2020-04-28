package com.razykrashka.bot.stage.meeting.view.utils;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;

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

    public static String getLink(String textLink, String url) {
        return String.format("<a href='%s'>%s</a>", url, textLink);
    }

    public static String getTelegramLink(TelegramUser u) {
        String profileLinkTmpl = "tg://user?id=%s";
        String participantName = u.getFirstName() + " " + u.getLastName();
        String url = String.format(profileLinkTmpl, u.getId());
        return TextFormatter.getLink(participantName, url);
    }
}