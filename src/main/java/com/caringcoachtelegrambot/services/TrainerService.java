package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Trainer;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface TrainerService extends Service<Trainer, Long> {

    PasswordEncoder getEncoder();

    Trainer getTrainer();
}
