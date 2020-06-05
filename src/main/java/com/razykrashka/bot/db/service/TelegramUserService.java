package com.razykrashka.bot.db.service;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramUserService {

    TelegramUserRepository telegramUserRepository;

    public TelegramUserService(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    public Page<TelegramUser> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<TelegramUser> fullList = StreamSupport.stream(telegramUserRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        List<TelegramUser> subList = fullList.stream().skip(startItem)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PageImpl<>(subList, PageRequest.of(currentPage, pageSize), fullList.size());
    }
}
