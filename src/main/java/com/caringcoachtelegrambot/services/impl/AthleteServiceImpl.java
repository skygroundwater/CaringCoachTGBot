package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.repositories.AthleteRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.AthleteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AthleteServiceImpl extends AbstractRepoService<Athlete, Long> implements AthleteService {

    public AthleteServiceImpl(AthleteRepository repository) {
        super(repository);
    }
}