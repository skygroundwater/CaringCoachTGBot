package com.caringcoachtelegrambot.configs;

import com.caringcoachtelegrambot.listener.CaringCoachBotUpdatesListener;
import com.pengrad.telegrambot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TelegramBotConfig {

    @Value("${bot.name}")
    String botName;

    @Value("${bot.owner}")
    Long ownerId;

    @Value("${bot.token}")
    String botToken;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Logger getLogger() {
        return LoggerFactory.getLogger(CaringCoachBotUpdatesListener.class);
    }
}