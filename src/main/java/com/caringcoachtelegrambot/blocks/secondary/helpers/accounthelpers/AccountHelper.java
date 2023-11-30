package com.caringcoachtelegrambot.blocks.secondary.helpers.accounthelpers;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class AccountHelper extends Helper {

    private boolean isWorking;

    public AccountHelper(){
        super();
    }
}
