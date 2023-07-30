package com.caringcoachtelegrambot.services;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServiceKeeper {

    private final AthleteService athleteService;

    private final QuestionnaireService questionnaireService;

    private final CertificateService certificateService;

    private final TrainerService trainerService;

    private final FAQService faqService;

    private final OnlineTrainingService onlineTrainingService;

    public ServiceKeeper(AthleteService athleteService,
                         QuestionnaireService questionnaireService,
                         CertificateService certificateService,
                         TrainerService trainerService,
                         FAQService faqService,
                         OnlineTrainingService onlineTrainingService) {
        this.athleteService = athleteService;
        this.questionnaireService = questionnaireService;
        this.certificateService = certificateService;
        this.trainerService = trainerService;
        this.faqService = faqService;
        this.onlineTrainingService = onlineTrainingService;
    }
}