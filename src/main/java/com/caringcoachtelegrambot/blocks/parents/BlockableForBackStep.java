package com.caringcoachtelegrambot.blocks.parents;

import com.pengrad.telegrambot.response.SendResponse;

public interface BlockableForBackStep extends Blockable {
    SendResponse goToBack(Long chatId);
}
