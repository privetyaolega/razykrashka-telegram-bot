package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.google.common.collect.ImmutableMap;
import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptDateMeetingCreationSBSStage;
import com.razykrashka.bot.ui.helpers.keyboard.KeyboardBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DateMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    static String NO_DATE = "noDate";

    @Override
    public void handleRequest() {
        processCallBackQuery();
    }

    @Override
    public boolean processCallBackQuery() {
        ReplyKeyboard keyboard;

        String callBackData = updateHelper.getCallBackData();


        if (this.getClass().getSimpleName().equals(callBackData) || meetingRepository
                .findByCreationStatusEqualsInProgress(updateHelper.getTelegramUserId()).get()
                .getCreationState().getActiveStage().equals(this.getClass().getSimpleName())) {

            // TODO: Informative error message;
            keyboard = generateCalendarKeyboard(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
            if (razykrashkaBot.getRealUpdate().hasMessage()) {
                messageManager.disableKeyboardLastBotMessage()
                        .sendSimpleTextMessage("Please, choose meeting date.", keyboard);
            } else {
                messageManager.updateOrSendDependsOnLastMessageOwner("Please, choose meeting date.", keyboard);
            }
        } else {
            if (callBackData.contains(NO_DATE)) {
                messageManager.sendAlertMessage("Пожалуйста, введита дату");
                setActiveNextStage(this.getClass());
                return true;
            }
            String monthYear = callBackData.replace(this.getClass().getSimpleName(), "");
            int month = Integer.parseInt(monthYear.substring(0, 2));
            int year = Integer.parseInt("20" + monthYear.substring(2, 4));
            keyboard = generateCalendarKeyboard(month, year);
            messageManager.updateOrSendDependsOnLastMessageOwner("Please, choose meeting date.", keyboard);
        }
        return true;
    }

    private InlineKeyboardMarkup generateCalendarKeyboard(int month, int year) {
        KeyboardBuilder keyboard = keyboardBuilder.getKeyboard();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyy");
        LocalDate date = LocalDate.of(year, month, 1);

        List<Pair<String, String>> list = new ArrayList<>();
        // First row: Month + Year (April 2020)
        String header = getSeasonEmoji(date) + " " + date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + date.getYear();
        keyboard.setRow(header, getCallBackString(NO_DATE))
                // Second row: Days of week (Mon, Tue, Whd, Thu, Fri, Sun, Sat)
                .setRow(getDayOfWeekRow());
        // Create empty cells for first week
        for (int i = 0; i < date.withDayOfMonth(1).getDayOfWeek().getValue() - 1; i++) {
            list.add(Pair.of(" ", getCallBackString(NO_DATE)));
        }
        int dayNum = 1;
        int monthLength = YearMonth.of(year, month).lengthOfMonth();
        while (dayNum != monthLength + 1) {
            String textButton;
            if (date.withDayOfMonth(dayNum).isBefore(LocalDate.now())) {
                textButton = "✖️";
            } else if (dayNum == LocalDate.now().getDayOfMonth() && month == LocalDate.now().getMonthValue()) {
                textButton = dayNum + ".";
            } else {
                textButton = String.valueOf(dayNum);
            }

            list.add((Pair.of(textButton, getCallBackDate(year, month, dayNum))));
            if (list.size() == 7) {
                keyboard.setRow(list);
                list = new ArrayList<>();
            }
            dayNum++;
        }
        if (list.size() != 0) {
            while (list.size() != 7) {
                list.add(Pair.of(" ", getCallBackString(NO_DATE)));
            }
        }

        this.setActiveNextStage(AcceptDateMeetingCreationSBSStage.class);
        return keyboard
                .setRow(list)
                .setRow(ImmutableMap.of(
                        "\uD83D\uDC48 " + date.minusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        this.getClass().getSimpleName() + date.minusMonths(1).format(formatter),
                        date.plusMonths(1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " \uD83D\uDC49",
                        this.getClass().getSimpleName() + date.plusMonths(1).format(formatter)))
                .build();
    }

    private String getCallBackDate(int year, int month, int day) {
        return validateOneSymbolDate(day) + validateOneSymbolDate(month) + year;
    }

    private String validateOneSymbolDate(int date) {
        return date <= 9 ? "0" + date : String.valueOf(date);
    }

    private List<Pair<String, String>> getDayOfWeekRow() {
        List<Pair<String, String>> map = new ArrayList<>();
        map.add(Pair.of("Mon", getCallBackString(NO_DATE)));
        map.add(Pair.of("Tue", getCallBackString(NO_DATE)));
        map.add(Pair.of("Whd", getCallBackString(NO_DATE)));
        map.add(Pair.of("Thu", getCallBackString(NO_DATE)));
        map.add(Pair.of("Fri", getCallBackString(NO_DATE)));
        map.add(Pair.of("Sat", getCallBackString(NO_DATE)));
        map.add(Pair.of("Sun", getCallBackString(NO_DATE)));
        return map;
    }

    private String getSeasonEmoji(LocalDate localDate) {
        switch (localDate.getMonthValue()) {
            case 1:
            case 2:
                return Emoji.SNOWFLAKE;
            case 3:
                return Emoji.SEEDLING;
            case 4:
                return Emoji.HERB;
            case 5:
                return Emoji.TULIP;
            case 6:
            case 7:
                return Emoji.SUNNY;
            case 8:
                return Emoji.SUNFLOWER;
            case 9:
                return Emoji.WATERMELON;
            case 10:
                return Emoji.MAPLE_LEAF;
            case 11:
                return Emoji.FALLEN_LEAF;
            case 12:
                return Emoji.CLINKING_GLASS;
            default:
                return null;
        }
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.isCallBackDataContains() || super.isStageActive();
    }
}