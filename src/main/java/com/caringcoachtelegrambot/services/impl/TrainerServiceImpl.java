package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Trainer;
import com.caringcoachtelegrambot.repositories.TrainerRepository;
import com.caringcoachtelegrambot.services.TrainerService;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository repository;

    private final PasswordEncoder encoder;

    public TrainerServiceImpl(TrainerRepository repository,
                              PasswordEncoder encoder) {
        this.encoder = encoder;
        this.repository = repository;
    }

    @Override
    public PasswordEncoder getEncoder() {
        return encoder;
    }

    @Override
    @Cacheable(value = "trainer")
    public Trainer getTrainer() {
        return repository.findAll().stream()
                .findAny().orElseThrow(NotFoundInDataBaseException::new);
    }

    @Override
    @CachePut(value = "trainer")
    public Trainer setTrainer(Trainer trainer) {
        return repository.save(trainer);
    }
}