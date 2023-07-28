package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Questionnaire;

import java.util.List;

public interface QuestionnaireService {

    Questionnaire findQuestionnaireById(Long id);

    Questionnaire postQuestionnaire(Questionnaire questionnaire);

    Questionnaire findNotCheckedQuestionnaire();

    void deleteQuestionnaire(Questionnaire questionnaire);

    Questionnaire putQuestionnaire(Questionnaire questionnaire);

    List<Questionnaire> findAll();
}
