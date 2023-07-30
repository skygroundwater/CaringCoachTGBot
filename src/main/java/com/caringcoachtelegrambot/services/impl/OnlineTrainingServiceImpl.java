package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.models.OnlineTraining;
import com.caringcoachtelegrambot.repositories.OnlineTrainingRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.OnlineTrainingService;
import org.springframework.stereotype.Service;

@Service
public class OnlineTrainingServiceImpl extends AbstractRepoService<OnlineTraining, Long> implements OnlineTrainingService {
    public OnlineTrainingServiceImpl(OnlineTrainingRepository repository) {
        super(repository);
    }
}