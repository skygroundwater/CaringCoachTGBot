package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Certificate;
import com.caringcoachtelegrambot.repositories.CertificateRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.CertificateService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl extends AbstractRepoService<Certificate, Long> implements CertificateService {

    public CertificateServiceImpl(CertificateRepository repository) {
        super(repository);
    }

}
