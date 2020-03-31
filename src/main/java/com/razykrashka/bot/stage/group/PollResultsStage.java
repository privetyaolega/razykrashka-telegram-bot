package com.razykrashka.bot.stage.group;

import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPoll;
import com.razykrashka.bot.db.entity.razykrashka.poll.TelegramPollOption;
import com.razykrashka.bot.db.repo.PollOptionRepository;
import com.razykrashka.bot.db.repo.PollRepository;
import com.razykrashka.bot.stage.MainStage;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

import java.util.HashSet;
import java.util.Set;

@Log4j2
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PollResultsStage extends MainStage {

    PollRepository pollRepository;
    PollOptionRepository pollOptionRepository;

    public PollResultsStage(PollRepository pollRepository, PollOptionRepository pollOptionRepository) {
        this.pollRepository = pollRepository;
        this.pollOptionRepository = pollOptionRepository;
    }

    @Override
    public void handleRequest() {
        Poll poll = updateHelper.getBot().getRealUpdate().getPoll();
        TelegramPoll telegramPoll = pollRepository.findByTelegramIdEquals(poll.getId()).get();

        Set<TelegramPollOption> telegramPollOptions = new HashSet<>();
        for (PollOption po : poll.getOptions()) {
            TelegramPollOption o = pollOptionRepository.findByTelegramIdAndTextOption(poll.getId(), po.getText()).get();
            o.setCount(po.getVoterCount());
            pollOptionRepository.save(o);
        }
        telegramPoll.setTelegramPollOptions(telegramPollOptions);
        pollRepository.save(telegramPoll);
    }

    @Override
    public boolean isStageActive() {
        return updateHelper.getBot().getRealUpdate().hasPoll();
    }
}