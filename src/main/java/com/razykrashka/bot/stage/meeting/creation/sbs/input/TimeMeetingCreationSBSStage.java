package com.razykrashka.bot.stage.meeting.creation.sbs.input;

import com.razykrashka.bot.constants.Emoji;
import com.razykrashka.bot.stage.meeting.creation.sbs.BaseMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.creation.sbs.accept.AcceptTimeMeetingCreationSBSStage;
import com.razykrashka.bot.stage.meeting.view.utils.TextFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Arrays;

@Log4j2
@Component
public class TimeMeetingCreationSBSStage extends BaseMeetingCreationSBSStage {

    @Override
    public void handleRequest() {
        meeting = getMeetingInCreation();
        meeting.setMeetingDateTime(meeting.getMeetingDateTime()
                .withHour(0)
                .withMinute(0));
        meetingRepository.save(meeting);

        String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                + TextFormatter.getItalicString(getString("input"));
        messageManager.deleteLastBotMessageIfHasKeyboard()
                .sendSimpleTextMessage(messageText, getFirstNumberKeyboard());
        super.setActiveNextStage(AcceptTimeMeetingCreationSBSStage.class);
    }

    @Override
    public void processCallBackQuery() {
        if (updateHelper.isCallBackDataContains(EDIT)) {
            handleRequest();
        } else if (updateHelper.isCallBackDataContains("noTime")) {
            messageManager.sendAlertMessage("Please, enter or write meeting time");
        } else {
            String callBackData = updateHelper.getStringPureCallBackData();
            int stageNum = Integer.parseInt(callBackData.split("\\$")[0]);
            String input = callBackData.split("\\$")[1];

            String timeToShow = input.length() > 2 ?
                    input.substring(0, 2) + ":" + input.substring(2) :
                    input;

            String messageText = meetingMessageUtils.createMeetingInfoDuringCreation(meeting)
                    + TextFormatter.getItalicString(getString("input")) + "\n\uD83D\uDCCC "
                    + TextFormatter.getBoldString(timeToShow);
            ReplyKeyboard keyboard;
            switch (stageNum) {
                case 0:
                    keyboard = getFirstNumberKeyboard();
                    break;
                case 1:
                    if (input.startsWith("1")) {
                        keyboard = getSecondNumberKeyboardV1(input);
                    } else {
                        keyboard = getSecondNumberKeyboardV2(input);
                    }
                    break;
                case 2:
                    keyboard = getThirdNumberKeyboard(input);
                    break;
                case 3:
                    keyboard = getForthNumberKeyboard(input);
                    break;
                default:
                    razykrashkaBot.getContext()
                            .getBean(AcceptTimeMeetingCreationSBSStage.class)
                            .handleRequest(input);
                    return;
            }
            messageManager.updateMessage(messageText, keyboard);
        }
    }

    protected String getCallBackString(String callBackData) {
        return this.getClass().getSimpleName() + callBackData;
    }

    public ReplyKeyboard getFirstNumberKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(Arrays.asList(
                        Pair.of("1", getCallBackString("1$1")),
                        Pair.of("2", getCallBackString("1$2")),
                        getCrossButton()
                ))
                .setRow(Arrays.asList(getCrossButton(), getCrossButton(), getCrossButton()))
                .setRow(Arrays.asList(getCrossButton(), getCrossButton(), getCrossButton()))
                .setRow(getCrossButton())
                .setRow(getBackButton())
                .build();
    }

    public ReplyKeyboard getSecondNumberKeyboardV1(String input) {
        return keyboardBuilder.getKeyboard()
                .setRow(Arrays.asList(
                        Pair.of("1", getCallBackString("2$" + input + "1")),
                        Pair.of("2", getCallBackString("2$" + input + "2")),
                        Pair.of("3", getCallBackString("2$" + input + "3"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("4", getCallBackString("2$" + input + "4")),
                        Pair.of("5", getCallBackString("2$" + input + "5")),
                        Pair.of("6", getCallBackString("2$" + input + "6"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("7", getCallBackString("2$" + input + "7")),
                        Pair.of("8", getCallBackString("2$" + input + "8")),
                        Pair.of("9", getCallBackString("2$" + input + "9"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("0", getCallBackString("2$" + input + "0")),
                        Pair.of("<", getCallBackString("0$ "))
                ))
                .setRow(getBackButton())
                .build();
    }

    public ReplyKeyboard getSecondNumberKeyboardV2(String input) {
        return keyboardBuilder.getKeyboard()
                .setRow(Arrays.asList(
                        Pair.of("1", getCallBackString("2$" + input + "1")),
                        Pair.of("2", getCallBackString("2$" + input + "2")),
                        Pair.of("3", getCallBackString("2$" + input + "3"))
                ))
                .setRow(Arrays.asList(getCrossButton(), getCrossButton(), getCrossButton()))
                .setRow(Arrays.asList(getCrossButton(), getCrossButton(), getCrossButton()))
                .setRow(Arrays.asList(
                        Pair.of("0", getCallBackString("2$" + input + "0")),
                        Pair.of("<", getCallBackString("0$ "))
                ))
                .setRow(getBackButton())
                .build();
    }

    public ReplyKeyboard getThirdNumberKeyboard(String input) {
        return keyboardBuilder.getKeyboard()
                .setRow(Arrays.asList(
                        Pair.of("1", getCallBackString("3$" + input + "1")),
                        Pair.of("2", getCallBackString("3$" + input + "2")),
                        Pair.of("3", getCallBackString("3$" + input + "3"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("4", getCallBackString("3$" + input + "4")),
                        Pair.of("5", getCallBackString("3$" + input + "5")),
                        getCrossButton()
                ))
                .setRow(Arrays.asList(getCrossButton(), getCrossButton(), getCrossButton()))
                .setRow(Arrays.asList(
                        Pair.of("0", getCallBackString("3$" + input + "0")),
                        Pair.of("<", getCallBackString("1$" + input.substring(0, 1)))
                ))
                .setRow(getBackButton())
                .build();
    }

    public ReplyKeyboard getForthNumberKeyboard(String input) {
        return keyboardBuilder.getKeyboard()
                .setRow(Arrays.asList(
                        Pair.of("1", getCallBackString("4$" + input + "1")),
                        Pair.of("2", getCallBackString("4$" + input + "2")),
                        Pair.of("3", getCallBackString("4$" + input + "3"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("4", getCallBackString("4$" + input + "4")),
                        Pair.of("5", getCallBackString("4$" + input + "5")),
                        Pair.of("6", getCallBackString("4$" + input + "6"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("7", getCallBackString("4$" + input + "7")),
                        Pair.of("8", getCallBackString("4$" + input + "8")),
                        Pair.of("9", getCallBackString("4$" + input + "9"))
                ))
                .setRow(Arrays.asList(
                        Pair.of("0", getCallBackString("4$" + input + "0")),
                        Pair.of("<", getCallBackString("2$" + input.substring(0, 2)))
                ))
                .setRow(getBackButton())
                .build();
    }

    private Pair<String, String> getCrossButton() {
        return Pair.of(Emoji.GRAY_CROSS, getCallBackString("noTime"));
    }

    private Pair<String, String> getBackButton() {
        return Pair.of(getString("backButton"), DateMeetingCreationSBSStage.class.getSimpleName());
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        return keyboardBuilder.getKeyboard()
                .setRow(getString("backButton"), DateMeetingCreationSBSStage.class.getSimpleName())
                .build();
    }

    @Override
    public boolean isStageActive() {
        return super.isStageActive()
                || updateHelper.isCallBackDataContains();
    }
}