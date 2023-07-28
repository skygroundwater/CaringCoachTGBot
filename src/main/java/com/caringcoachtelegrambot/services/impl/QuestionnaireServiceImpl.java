package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Questionnaire;
import com.caringcoachtelegrambot.repositories.QuestionnaireRepository;
import com.caringcoachtelegrambot.services.QuestionnaireService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {

    private final QuestionnaireRepository repository;

    public QuestionnaireServiceImpl(QuestionnaireRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable("questionnaires")
    public Questionnaire findQuestionnaireById(Long id) {
        return repository.findById(id).orElseThrow(NotFoundInDataBaseException::new);
    }

    @Override
    @Cacheable(value = "questionnaires", key = "#questionnaire.id")
    public Questionnaire postQuestionnaire(Questionnaire questionnaire) {
        if (questionnaire == null) throw new NotValidDataException();
        return repository.save(questionnaire);
    }

    @Override
    @Cacheable(value = "questionnaires", unless = "#result.checked = true")
    public Questionnaire findNotCheckedQuestionnaire() {
        return repository.findQuestionnairesByChecked(false)
                .stream().findFirst().orElseThrow(
                        NotFoundInDataBaseException::new);
    }

    @Override
    @CacheEvict(value = "questionnaires", key = "#questionnaire.id")
    public void deleteQuestionnaire(Questionnaire questionnaire) {
        repository.delete(questionnaire);
    }

    @Override
    @CachePut(value = "questionnaires", key = "#questionnaire.id")
    public Questionnaire putQuestionnaire(Questionnaire questionnaire) {
        if (questionnaire == null) throw new NotValidDataException();
        return repository.save(questionnaire);
    }

    @Override
    @Cacheable("questionnaires")
    public List<Questionnaire> findAll() {
        return repository.findAll();
    }
}
