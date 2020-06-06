package com.razykrashka.bot.controller.admin.users;


import com.razykrashka.bot.db.entity.infrastructure.BlackList;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.repo.BlackListRepository;
import com.razykrashka.bot.db.service.TelegramUserService;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

@Log4j2
@Controller
@RequestMapping("/admin/users")
public class UserInfoController {

    @Value("${my.log.folder}")
    private String logFolder;
    @Autowired
    TelegramUserService telegramUserService;
    @Autowired
    BlackListRepository blackListRepository;

    @GetMapping("/info")
    public String meetingProperties(Model model,
                                    @RequestParam("page") Optional<Integer> page,
                                    @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<TelegramUser> userPage = telegramUserService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("userPage", userPage);

        int totalPages = userPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "users/info.html";
    }

    @SneakyThrows
    @GetMapping("/download-log")
    public ResponseEntity<Resource> meetingProperties(@RequestParam("user-id") int userId) {
        File file = new File(logFolder + userId + File.separator + "console.log");
        Path path1 = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path1));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + userId + ".log");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .contentLength(file.length())
                .headers(header)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @GetMapping("/black-list")
    public String blackList(Model model) {
        List<BlackList> blackListUsers = StreamSupport.stream(blackListRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        model.addAttribute("blackListUsers", blackListUsers);
        model.addAttribute("blackListRecord", new BlackList());
        return "users/black-list.html";
    }

    @PostMapping("/black-list")
    public String addToBlackList(@ModelAttribute BlackList blackList) {
        blackListRepository.save(blackList);
        return "redirect:/admin/users/black-list";
    }
}