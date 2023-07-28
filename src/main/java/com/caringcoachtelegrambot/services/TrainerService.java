package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Trainer;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface TrainerService {

    PasswordEncoder getEncoder();

    Trainer getTrainer();

    Trainer setTrainer(Trainer trainer);
}
