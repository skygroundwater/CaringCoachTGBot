package com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers;

import com.caringcoachtelegrambot.models.Trainer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerHelper extends AccountHelper {

    private final Trainer trainer;

    public TrainerHelper(Trainer trainer) {
        super();
        this.trainer = trainer;
    }
}