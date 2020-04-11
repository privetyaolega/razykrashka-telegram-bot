package com.razykrashka.bot.controller.admin;


import com.razykrashka.bot.service.config.job.properties.JobProperties;
import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import com.razykrashka.bot.ui.helpers.UpdateHelper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    UpdateHelper updateHelper;
    @Autowired
    MeetingProperties meetingProperties;
    @Autowired
    JobProperties jobProperties;
    @Autowired
    DefaultListableBeanFactory beanFactory;
    @Value("${logging.file}")
    private String loggingPath;

    @GetMapping("/meeting-properties")
    public String meetingProperties(Model model) {
        model.addAttribute("properties", meetingProperties);
        return "properties/meeting-properties.html";
    }


    @GetMapping("/meeting-properties-edit")
    public String meetingPropertiesEdit(Model model) {
        model.addAttribute("properties", meetingProperties);
        return "properties/meeting-properties-edit.html";
    }

    @GetMapping("/meeting-properties-reset")
    public String resetMeetingProperties(Model model) {
        beanFactory.destroySingleton("meetingProperties");
        meetingProperties = (MeetingProperties) updateHelper.getBot().getContext().getBean("meetingProperties");
        model.addAttribute("properties", meetingProperties);
        log.info("MEETING PROPERTIES: Properties have been reset to default values");
        return "properties/meeting-properties.html";
    }

    @GetMapping("/logs")
    public String meetingLogs(Model model) throws IOException {
        String log = getLastLogLines();
        model.addAttribute("lastLog", log);
        return "logs.html";
    }

    @RequestMapping(value = "/logs-ajax", method = RequestMethod.GET)
    public @ResponseBody
    String getTime() {
        log.info("TEST");
        return "<pre>" + getLastLogLines() + "</pre>";
    }

    private String getLastLogLines() {
        File file = new File(System.getProperty("user.dir") + File.separator + loggingPath);
        int limitLines = 100;
        int counter = 0;
        StringBuilder sb = new StringBuilder();
        try {
            ReversedLinesFileReader reader = new ReversedLinesFileReader(file);
            while (counter < limitLines) {
                sb.append(reader.readLine()).append("\n");
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    @GetMapping("/log-download")
    public ResponseEntity<Resource> downloadLogFile() throws IOException {
        File file = new File(System.getProperty("user.dir") + File.separator + loggingPath);
        Path path1 = Paths.get(file.getAbsolutePath());
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path1));

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=log.log");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.ok()
                .contentLength(file.length())
                .headers(header)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping("/save")
    public String saveProperties(@ModelAttribute("prop") MeetingProperties newProps) {
        meetingProperties.setViewPerPage(newProps.getViewPerPage());
        meetingProperties.setSession(newProps.getSession());
        meetingProperties.getCreation().setHourAdvance(newProps.getCreation().getHourAdvance());
        meetingProperties.getCreation().setUpperHourLimitToday(newProps.getCreation().getUpperHourLimitToday());
        meetingProperties.getCreation().setNotificationGroup(newProps.getCreation().getNotificationGroup());

        log.info("MEETING PROPERTIES: Properties have been changed: {}", newProps.toString());
        return "redirect:/admin/meeting-properties";
    }
}