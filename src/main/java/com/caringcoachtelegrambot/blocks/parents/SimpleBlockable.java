package com.caringcoachtelegrambot.blocks.parents;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.blocks.secondary.helpers.Node;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.services.keeper.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Setter
@Getter
public abstract class SimpleBlockable<T extends Helper> implements BlockableForBackStep {

    private final TelegramSender sender;

    private final Node node;

    private final ConcurrentMap<Long, T> helpers;

    private final ServiceKeeper serviceKeeper;

    public SimpleBlockable(TelegramSender sender,
                           ServiceKeeper serviceKeeper) {
        this.serviceKeeper = serviceKeeper;
        this.sender = sender;
        this.node = new Node();
        this.helpers = new ConcurrentHashMap<>();
    }

    public abstract SendResponse process(Long chatId, Message message);

    public SendResponse block(Long chatId, Message message) {
        String txt = message.text();
        if (txt != null && !txt.isEmpty()) {
            try {
                return process(chatId, message);
            } catch (NotValidDataException e) {
                return sender.sendResponse(new SendMessage(chatId, e.getMessage()));
            }
        } else return sender.sendResponse(new SendMessage(chatId, "Пустое сообщение"));
    }

    public final TelegramSender sender() {
        return sender;
    }

    public final ConcurrentMap<Long, T> helpers() {
        return helpers;
    }

    public final Node node() {
        return node;
    }

    public final OnlineTrainingService onlineTrainingService() {
        return serviceKeeper.getOnlineTrainingService();
    }

    public final AthleteService athleteService() {
        return serviceKeeper.getAthleteService();
    }

    public final FAQService faqService() {
        return serviceKeeper.getFaqService();
    }

    public final QuestionnaireService questionnaireService() {
        return serviceKeeper.getQuestionnaireService();
    }

    public final CertificateService certificateService() {
        return serviceKeeper.getCertificateService();
    }

    public final TrainerService trainerService() {
        return serviceKeeper.getTrainerService();
    }

    public final ReportService reportService() {
        return serviceKeeper.getReportService();
    }

    @Override
    public final boolean checkIn(Long chatId) {
        T helper = helpers.get(chatId);
        if (helper != null) {
            return helper.isIn();
        } else return false;
    }

    @Override
    public final SendResponse goToBack(Long chatId) {
        Helper helper = helpers.get(chatId);
        if (helper != null) {
            helper.setIn(false);
        }
        return node.getPrevBlockable().uniqueStartBlockMessage(chatId);
    }

    protected final SendResponse msg(Long chatId, String txt) {
        return sender.sendResponse(new SendMessage(chatId, txt));
    }

    @Override
    public final void setPrevBlockable(Blockable blockable) {
        node.setPrevBlockable(blockable);
    }
}