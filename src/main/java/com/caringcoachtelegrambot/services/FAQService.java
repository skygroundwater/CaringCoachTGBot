package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.FAQ;

public interface FAQService extends Service<FAQ, String>{

    FAQ findFAQWithoutAnswer();
}
