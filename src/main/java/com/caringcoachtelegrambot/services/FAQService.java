package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.FAQ;

import java.util.List;

public interface FAQService {
    FAQ postFAQ(FAQ faq);

    FAQ findFAQ(String question);

    void deleteFAQ(FAQ faq);

    List<FAQ> findAll();

    FAQ findFAQWithoutAnswer();
}
