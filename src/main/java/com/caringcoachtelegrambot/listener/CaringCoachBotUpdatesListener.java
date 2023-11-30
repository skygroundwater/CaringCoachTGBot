package com.caringcoachtelegrambot.listener;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CaringCoachBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final Blockable startCommunicationBlockable;

    @Autowired
    private List<Blockable> blocks;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.stream()
                .filter(Objects::nonNull)
                .forEach(update -> {
                    Message message = update.message();
                    String text = message.text();
                    Long chatId = message.chat().id();
                    if (text != null && !text.isEmpty()) {
                        if (text.equals("/start")) {
                            startCommunicationBlockable.uniqueStartBlockMessage(chatId);
                        } else {
                            checkBlocks(chatId, message);
                        }
                    }
                });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void checkBlocks(Long chatId, Message message) {
        blocks.stream()
                .filter(blockable -> blockable.checkIn(chatId))
                .findFirst()
                .ifPresent(blockable -> blockable.block(chatId, message));
    }
}
