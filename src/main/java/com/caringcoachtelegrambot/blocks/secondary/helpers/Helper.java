package com.caringcoachtelegrambot.blocks.secondary.helpers;

import lombok.Data;

@Data
public abstract class Helper {

    private boolean in;

    public Helper() {
        this.in = true;
    }
}