package com.caringcoachtelegrambot.blocks.secondary.accounts;

import com.caringcoachtelegrambot.blocks.secondary.helpers.Helper;
import com.caringcoachtelegrambot.services.ServiceKeeper;
import com.caringcoachtelegrambot.utils.TelegramSender;
import lombok.Getter;

@Getter
public abstract class AccountHelper extends Helper {

    private final ServiceKeeper serviceKeeper;

    private final TelegramSender sender;

    public AccountHelper(ServiceKeeper serviceKeeper,
                         TelegramSender sender) {
        this.serviceKeeper = serviceKeeper;
        this.sender = sender;
    }


}
