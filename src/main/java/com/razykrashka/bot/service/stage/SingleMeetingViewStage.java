package com.razykrashka.bot.service.stage;

import com.razykrashka.bot.entity.Meeting;
import com.razykrashka.bot.repository.MeetingRepository;
import com.razykrashka.bot.repository.TelegramUserRepository;
import com.razykrashka.bot.service.stage.utils.SendMessageUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVenue;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Log4j2
@Component
public class SingleMeetingViewStage extends MainStage {

    @Autowired
    private SendMessageUtils sendMessageUtils;

    @Autowired
    private TelegramUserRepository telegramUserRepository;

    private Meeting meeting;

    public SingleMeetingViewStage() {
        stageInfo = StageInfo.SINGLE_MEETING_VIEW;
    }

    @Override
    public List<String> getValidKeywords() {
        return null;
    }

    @Override
    public void handleRequest() {
        Integer id = Integer.valueOf(razykrashkaBot.getUpdate().getMessage().getText()
                .replace(this.getStageInfo().getKeyword(), ""));

        meeting = meetingRepository.findById(id).orElse(null);

        if (meeting == null) {
            razykrashkaBot.sendSimpleTextMessage("NO SUCH MEETING :(");
        } else {
            SendMessage sendMessage = sendMessageUtils.createSendMessageForSingleMeeting(this, meeting, razykrashkaBot);
            razykrashkaBot.executeBot(sendMessage);
        }
    }

    @Override
    public ReplyKeyboard getKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Information").setCallbackData(stageInfo.getStageName() + "_information"));

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact"));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map"));

        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }

    public ReplyKeyboard getKeyboard(Meeting model) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

//                .setCallbackData(stageInfo.getStageName() + "_information" + model.getId()));
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        if (razykrashkaBot.getTelegramUser().getToGoMeetings().stream().anyMatch(m -> m.getId().equals(meeting.getId()))) {
            keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Unsubscribe").setCallbackData(stageInfo.getStageName() + "_unsubscribe" + meeting.getId()));
        } else {
            keyboardButtonsRow1.add(new InlineKeyboardButton().setText("Join").setCallbackData(stageInfo.getStageName() + "_join" + meeting.getId()));
        }

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Contact").setCallbackData(stageInfo.getStageName() + "_contact" + meeting.getId()));
        keyboardButtonsRow2.add(new InlineKeyboardButton().setText("Map").setCallbackData(stageInfo.getStageName() + "_map" + meeting.getId()));
        inlineKeyboardMarkup.setKeyboard(Arrays.asList(keyboardButtonsRow1, keyboardButtonsRow2));
        return inlineKeyboardMarkup;
    }


    @Override
    public boolean processCallBackQuery() {
        String callBackData = razykrashkaBot.getCallbackQuery().getData();

        meeting = StreamSupport.stream(meetingRepository.findAll().spliterator(), false).
                filter(x -> callBackData.contains(String.valueOf(x.getId()))).findFirst().get();

        if (callBackData.equals(stageInfo.getStageName() + "_contact" + meeting.getId())) {
            razykrashkaBot.sendContact(new SendContact().setLastName(meeting.getTelegramUser().getLastName())
                    .setFirstName(meeting.getTelegramUser().getFirstName())
                    .setPhoneNumber(meeting.getTelegramUser().getPhoneNumber()));
        }
        if (callBackData.equals(stageInfo.getStageName() + "_map" + meeting.getId())) {
            razykrashkaBot.sendVenue(new SendVenue().setTitle("TEST TITLE")
                    .setLatitude(meeting.getLocation().getLatitude())
                    .setLongitude(meeting.getLocation().getLongitude())
                    .setAddress("TEST ADDRESS"));
        }

        if (callBackData.equals(stageInfo.getStageName() + "_join" + meeting.getId())) {
            razykrashkaBot.getTelegramUser().addMeetingTotoGoMeetings(meeting);
            telegramUserRepository.save(razykrashkaBot.getTelegramUser());
            razykrashkaBot.sendSimpleTextMessage("yyyyyyyyyyyyyyyyyeah");
        }

        if (callBackData.equals(stageInfo.getStageName() + "_unsubscribe" + meeting.getId())) {
            //meetingRepository.deleteMeeting(meeting.getId());
            razykrashkaBot.getTelegramUser().removeFromToGoMeetings(meeting);
            telegramUserRepository.save(razykrashkaBot.getTelegramUser());
            razykrashkaBot.sendSimpleTextMessage(":(");

        }
        return true;
    }

    @Override
    public boolean isStageActive() {
        return razykrashkaBot.getUpdate().getMessage().getText().startsWith(stageInfo.getKeyword());
    }
}
