package com.caringcoachtelegrambot.blocks.parents;

import com.pengrad.telegrambot.response.SendResponse;

public interface BlockableForNextStep {
    SendResponse goToNext(Long chatId);

    SendResponse jumpUnderHead(Long chatId);

    SendResponse goDownTwoSteps(Long chatId);
}
