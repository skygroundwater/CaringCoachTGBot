package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.Getter;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Getter
public abstract class AccountInterface<H extends AccountHelper> {

    private final H helper;

    public AccountInterface(H helper) {
        this.helper = helper;
    }

    public final TelegramSender sender() {
        return helper.getSender();
    }

    public final OnlineTrainingService onlineTrainingService() {
        return helper.getServiceKeeper().getOnlineTrainingService();
    }

    public final QuestionnaireService questionnaireService() {
        return helper.getServiceKeeper().getQuestionnaireService();
    }

    public final FAQService faqService() {
        return helper.getServiceKeeper().getFaqService();
    }

    public final TrainerService trainerService() {
        return helper.getServiceKeeper().getTrainerService();
    }

    public final AthleteService athleteService() {
        return helper.getServiceKeeper().getAthleteService();
    }


    public ReplyKeyboardMarkup backMarkup() {
        return new ReplyKeyboardMarkup(BACK);
    }
}
