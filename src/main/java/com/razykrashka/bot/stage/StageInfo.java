package com.razykrashka.bot.stage;

public enum StageInfo {

    WELCOME("WelcomeStage", "Welcome to our Speaking Club community 'Razykrashka'! \nNice to meet you!", null, "/start"),
    INFORMATION("InformationStage", "\uD83C\uDF08 Welcome to community whose main goal is cohesion of people learning English; improvement, development and comprehensive support of all skills related to language. \uD83C\uDF08\n" +
            "You can create some meeting to speak or join to existing one. \uD83D\uDE4F\uD83C\uDFFB", "Прежде всего, это площадка имеет цель объединить людей-единомышленников, для практики английского, в который каждый может организовать встречу, в любое для него удобное время, месте и по любым другим критериями, например, уровень языка, пол, расовая принадлежность (смотрите пример ниже).\n" +
            "Вы можете организовать свою встречу, можете присоединиться к существующей.\n" +
            "\n" +
            "Тема для встречи НЕ является обязательным пунктом, но данный подход желателен (рекомендовано), так как имеет ряд преимуществ.\n" +
            "Заданная в заранее тема, позволяет  подтянуть вокабуляр, кому-то подтянуть знания в предметной области, кому-то эта тема может быть не интересна вовсе.\n" +
            "Так разговор гораздо предметнее и эффективнее.\n" +
            "\n" +
            "Количество людей на встрече так же зависит только от ваших желаний. Вы можете найти просто одного собеседника, можете собрать 50 человек\n" +
            "По наблюдению и опыту было выявлено, что количество человек больше 3-4, превращает спикинг клаб в лисенинг со всеми вытекающими недостатками и нюансами. \n" +
            "\n" +
            "Так как у некоторых, был опыт посещения спикинг клаба в «Лидо» на Якуба Колоса, я попробую сказать какие проблемы «Лидо» и большинства спикинг клабов, будет решать данный подход:\n" +
            "\n" +
            "1) большое количество участников (за столом может сидеть 6-8 человек, что существенно снижает персональное время говорения)\n" +
            "2) примитивность и однообразность тем (знакомство, тревелинг, английский)\n" +
            "3) разброс по уровню\n" +
            "4) неудобное для кого-то время и локация\n" +
            "\n" +
            "Резюмируя\n" +
            "Это не коммерческая площадка, полностью держащаяся на инициативе участников\n" +
            "Вы можете создать запрос, используя шаблон (ищите его ниже); можете присоединиться к встрече\n" +
            "Задавайте вопросы, приглашайте друзей и, самое главное, будьте АКТИВНЫ.\n" +
            "https://t.me/razykrashkaen\n" +
            "\n" +
            "Хорошего дня. \uD83D\uDE4F\uD83C\uDFFB", "Information :P"),
    INTRO_CREATE_MEETING("CreateMeetingStage", "NEW MEETING REQUEST:\n" +
            "\n" +
            "DATE: \n" +
            "14.01.2020 19-00\n" +
            "\n" +
            "LOCATION: \n" +
            "ул. Немига 5, Лидо\n" +
            "\n" +
            "MAX PEOPLE: \n" +
            "4\n" +
            "\n" +
            "SPEAKING LEVEL: \n" +
            "Upper-Intermediate\n" +
            "\n" +
            "CONTACT NUMBER: \n" +
            "+375295508809\n" +
            "\n" +
            "TOPIC: \n" +
            "Internet and Computers\n" +
            "\n" +
            "QUESTIONS:\n" +
            "● What are two disadvantages of using smartphones and tablets?\n" +
            "● What do you think about Cybercafes? are they still useful?\n" +
            "● Do you like a job in which you have to use computers?\n" +
            "● Do you spend too much time online?\n" +
            "● Does your mother of father know how to use a computer?\n" +
            "● Do computers make life easier?\n" +
            "● What websites do you visit in a regular basis?\n" +
            "● What do you think about the Apple?\n" +
            "● What are some advantages of using social networks?\n" +
            "● Do you visit English websites?", "Вставьте шаблон и отправьте его", "Create Meeting"),
    UNDEFINED("UndefinedStage", "Bot doesn't know this commant", null),
    VIEW_EXISTING_MEETINGS("ViewExistingMeetingsStage", "Existing meetings", null, "View Meetings"),
    VIEW_SINGLE_MEETING("ViewSingleMeetingStage", "null", null, "/meeting"),
    NEW_MEETING_CREATION("NewMeetingCreationStage", "Your request in moderation...", "Запрос в обработке", "NEW MEETING REQUEST");

    private final String stageName;
    private final String welcomeMessageRu;
    private final String welcomeMessageEn;
    private final String keyword;

    private StageInfo(String stageName, String welcomeMessageEn, String keyword) {
        this.stageName = stageName;
        this.welcomeMessageEn = welcomeMessageEn;
        this.keyword = keyword;
        this.welcomeMessageRu = null;
    }

    private StageInfo(String stageName, String welcomeMessageEn, String welcomeMessageRu, String keyword) {
        this.stageName = stageName;
        this.welcomeMessageEn = welcomeMessageEn;
        this.welcomeMessageRu = welcomeMessageRu;
        this.keyword = keyword;
    }

    public String getStageName() {
        return stageName;
    }

    public String getWelcomeMessageRu() {
        return welcomeMessageRu;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getWelcomeMessageEn() {
        return welcomeMessageEn;
    }
}
