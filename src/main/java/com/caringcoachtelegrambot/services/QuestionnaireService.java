package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Questionnaire;

public interface QuestionnaireService extends Service<Questionnaire, Long> {
    Questionnaire findNotCheckedQuestionnaire();
}
