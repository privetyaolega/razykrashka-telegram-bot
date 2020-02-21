package com.razykrashka.bot.transformer;

public interface Transformer<FROM, TO> {
    TO transform(FROM chat);
}
