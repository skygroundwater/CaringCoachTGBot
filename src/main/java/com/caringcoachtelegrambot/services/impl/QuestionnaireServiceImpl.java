package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.repositories.QuestionnaireRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.QuestionnaireService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class QuestionnaireServiceImpl extends AbstractRepoService<Questionnaire, Long> implements QuestionnaireService {

    public QuestionnaireServiceImpl(QuestionnaireRepository repository) {
        super(repository);
    }

    @Override
    @Cacheable(value = "questionnaires", unless = "#result.checked = true")
    public Questionnaire findNotCheckedQuestionnaire() {
        QuestionnaireRepository repository = (QuestionnaireRepository) getRepository();
        return repository.findQuestionnairesByChecked(false)
                .stream().findFirst().orElseThrow(
                        NotFoundInDataBaseException::new);
    }
}