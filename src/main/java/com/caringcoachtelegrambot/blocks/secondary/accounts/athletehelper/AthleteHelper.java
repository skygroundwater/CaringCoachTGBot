package com.caringcoachtelegrambot.blocks.secondary.accounts.athletehelper;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.models.Athlete;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AthleteHelper extends Helper {

    private Athlete athlete;
    private boolean working;

    public AthleteHelper(Athlete athlete) {
        super();
        this.athlete = athlete;
        this.working = false;
    }
}
