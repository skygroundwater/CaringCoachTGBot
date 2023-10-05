package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.repositories.FAQRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.FAQService;
import org.springframework.stereotype.Service;

@Service
public class FAQServiceImpl extends AbstractRepoService<FAQ, String> implements FAQService {
    public FAQServiceImpl(FAQRepository repository) {
        super(repository);
    }

    @Override
    public FAQ findFAQWithoutAnswer() {
        return getRepository()
                .findAll()
                .stream()
                .filter(faq -> faq.getAnswer() == null)
                .findFirst()
                .orElseThrow(NotFoundInDataBaseException::new);
    }
}
