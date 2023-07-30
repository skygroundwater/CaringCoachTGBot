package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Questionnaire;

import java.util.List;

public interface QuestionnaireService extends Service<Questionnaire, Long> {
    Questionnaire findNotCheckedQuestionnaire();
}
