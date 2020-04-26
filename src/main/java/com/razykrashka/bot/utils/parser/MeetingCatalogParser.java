package com.razykrashka.bot.utils.parser;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MeetingCatalogParser {

//    "bot/other/topics/101_150.txt"
    public static String catalogToSql(String filePath) {
        String sql = "INSERT INTO topic_catalogue (id, questions, speaking_level, topic)\n" +
                "VALUES ('%s',\n" +
                "'%s',\n" +
                "'%s',\n" +
                "'%s');";
        String s = null;
        try {
            File file = new ClassPathResource(filePath).getFile();
            s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] split = s.split("\r\n\r");
        List<Topic> topics = new ArrayList<>();

        for (String topic : split) {
            topics.add(Topic.builder()
                    .id(topic.split("\r")[0].split(" ")[1].trim())
                    .topic(topic.split("\r")[1].split(":")[1].trim())
                    .level(topic.split("\r")[2].split(":")[1].trim())
                    .questions(topic.split("Questions:")[1]
                            .replaceAll("(\r\n\\d{1,2}). ", ";")
                            .replace("'", "\\'")
                            .replace("’", "\\'")
                            .replace("`", "\\'")
                            .substring(1)
                            .trim())
                    .build());
        }

        return topics.stream().map(t -> String.format(sql,
                t.getId(), t.questions, t.level, t.getTopic()))
                .collect(Collectors.joining("\r\n\r"));
    }
}