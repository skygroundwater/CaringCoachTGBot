package com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers;

import com.caringcoachtelegrambot.models.Athlete;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AthleteHelper extends AccountHelper {

    private Athlete athlete;
    private boolean working;

    public AthleteHelper(Athlete athlete) {
        super();
        this.athlete = athlete;
        this.working = false;
    }
}
