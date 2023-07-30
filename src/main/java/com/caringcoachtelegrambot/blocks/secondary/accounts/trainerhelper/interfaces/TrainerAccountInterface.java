package com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.interfaces;

import com.caringcoachtelegrambot.blocks.secondary.accounts.AccountInterface;
import com.caringcoachtelegrambot.blocks.secondary.accounts.TrainerAccountBlockable;
import com.caringcoachtelegrambot.blocks.secondary.accounts.trainerhelper.TrainerHelper;
import lombok.Getter;

@Getter
public abstract class TrainerAccountInterface extends AccountInterface<TrainerHelper> {

    public TrainerAccountInterface(TrainerHelper helper) {
        super(helper);
    }

    public final TrainerAccountBlockable trainerAccountBlockable() {
        return (TrainerAccountBlockable) getHelper().getTrainerAccountBlockable();
    }
}
