package com.caringcoachtelegrambot.repositories;

import com.caringcoachtelegrambot.models.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    List<Questionnaire> findQuestionnairesByChecked(boolean checked);
}
