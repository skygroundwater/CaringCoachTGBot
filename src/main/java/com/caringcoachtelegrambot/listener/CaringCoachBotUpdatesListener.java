package com.caringcoachtelegrambot.listener;

import com.caringcoachtelegrambot.blocks.parents.Blockable;
import com.caringcoachtelegrambot.blocks.secondary.*;
import com.caringcoachtelegrambot.blocks.StartCommunicationBlockable;
import com.caringcoachtelegrambot.blocks.secondary.PopularFAQBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.AthleteAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class CaringCoachBotUpdatesListener implements UpdatesListener {

    private final TelegramBot telegramBot;

    private final Blockable firstMeetingBlock;

    private final Blockable startCommunicationBlockable;

    private final Blockable faqBlockable;

    private final Blockable questionnaireRecordingBlockable;

    private final Blockable priceListBlockable;

    private final Blockable registrationBlockable;

    private final Blockable authorizationBlockable;

    private final Blockable athleteAccountBlockable;

    private final Blockable trainerAccountBlockable;

    private final Blockable popularFaqBlockable;

    private List<Blockable> blocks;

    public CaringCoachBotUpdatesListener(TelegramBot telegramBot,
                                         MeetingBlockable firstMeetingBlock,
                                         StartCommunicationBlockable startCommunicationBlockable,
                                         FAQBlockable faqBlockable,
                                         PopularFAQBlockable popularFAQBlockable,
                                         QuestionnaireRecordingBlockable questionnaireRecordingBlockable,
                                         PriceListBlockable priceListBlockable,
                                         RegistrationBlockable registrationBlockable,
                                         AuthorizationBlockable authorizationBlockable,
                                         AthleteAccountBlockable athleteAccountBlockable,
                                         TrainerAccountBlockable trainerAccountBlockable) {
        this.telegramBot = telegramBot;
        this.firstMeetingBlock = firstMeetingBlock;
        this.startCommunicationBlockable = startCommunicationBlockable;
        this.faqBlockable = faqBlockable;
        this.questionnaireRecordingBlockable = questionnaireRecordingBlockable;
        this.priceListBlockable = priceListBlockable;
        this.registrationBlockable = registrationBlockable;
        this.authorizationBlockable = authorizationBlockable;
        this.athleteAccountBlockable = athleteAccountBlockable;
        this.trainerAccountBlockable = trainerAccountBlockable;
        this.popularFaqBlockable = popularFAQBlockable;
    }

    @PostConstruct
    public void init() {
        blocks = new ArrayList<>(List.of(
                firstMeetingBlock,
                startCommunicationBlockable,
                faqBlockable,
                questionnaireRecordingBlockable,
                priceListBlockable,
                registrationBlockable,
                authorizationBlockable,
                athleteAccountBlockable,
                trainerAccountBlockable,
                popularFaqBlockable
        ));
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.stream().filter(Objects::nonNull)
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
