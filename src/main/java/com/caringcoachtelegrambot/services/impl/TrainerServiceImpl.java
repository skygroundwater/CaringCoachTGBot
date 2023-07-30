package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.repositories.TrainerRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.TrainerService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl extends AbstractRepoService<Trainer, Long> implements TrainerService {
    private final PasswordEncoder encoder;

    public TrainerServiceImpl(TrainerRepository repository,
                              PasswordEncoder encoder) {
        super(repository);
        this.encoder = encoder;
    }

    @Override
    public PasswordEncoder getEncoder() {
        return encoder;
    }

    @Override
    @Cacheable(value = "trainer")
    public Trainer getTrainer() {
        TrainerRepository repository = (TrainerRepository) getRepository();
        return repository.findAll().stream()
                .findAny().orElseThrow(NotFoundInDataBaseException::new);
    }
}