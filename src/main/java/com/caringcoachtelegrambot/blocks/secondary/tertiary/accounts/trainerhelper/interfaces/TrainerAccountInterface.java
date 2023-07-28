package com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.interfaces;

import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.tertiary.accounts.trainerhelper.TrainerHelper;
import com.caringcoachtelegrambot.services.*;
import com.caringcoachtelegrambot.utils.TelegramSender;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import lombok.Getter;

import static com.caringcoachtelegrambot.utils.Constants.BACK;

@Getter
public abstract class TrainerAccountInterface {

    private final TrainerHelper helper;

    public TrainerAccountInterface(TrainerHelper helper) {
        this.helper = helper;
    }

    public final TelegramSender sender() {
        return helper.getSender();
    }

    public final OnlineTrainingService onlineTrainingService() {
        return helper.getOnlineTrainingService();
    }

    public final QuestionnaireService questionnaireService() {
        return helper.getQuestionnaireService();
    }

    public final TrainerAccountBlockable trainerAccountBlockable(){
        return (TrainerAccountBlockable) helper.getTrainerAccountBlockable();
    }

    public final FAQService faqService() {
        return helper.getFaqService();
    }

    public final TrainerService trainerService() {
        return helper.getTrainerService();
    }

    public final AthleteService athleteService() {
        return helper.getAthleteService();
    }

    public ReplyKeyboardMarkup backMarkup() {
        return new ReplyKeyboardMarkup(BACK);
    }

}
