package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.repositories.FAQRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.FAQService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FAQServiceImpl extends AbstractRepoService<FAQ, String> implements FAQService {
    public FAQServiceImpl(FAQRepository repository) {
        super(repository);
    }
}
