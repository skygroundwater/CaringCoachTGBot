package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.FAQ;
import com.caringcoachtelegrambot.repositories.FAQRepository;
import com.caringcoachtelegrambot.services.FAQService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FAQServiceImpl implements FAQService {


    private final FAQRepository repository;

    public FAQServiceImpl(FAQRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(value = "faq", key = "#faq.question")
    public FAQ postFAQ(FAQ faq){
        return repository.save(faq);
    }

    @Override
    @Cacheable(value = "faq", key = "#question")
    public FAQ findFAQ(String question){
        return repository.findById(question).orElseThrow(NotFoundInDataBaseException::new);
    }

    @Override
    @CacheEvict(value = "faq", key = "#faq.question")
    public void deleteFAQ(FAQ faq){
        repository.deleteById(faq.getQuestion());
    }

    @Override
    @Cacheable("faq")
    public List<FAQ> findAll(){
        return repository.findAll();
    }

    @Override
    public FAQ findFAQWithoutAnswer(){
        return repository.findFAQSByAnswer(null).stream()
                .findAny().orElseThrow(NotFoundInDataBaseException::new);
    }
}
