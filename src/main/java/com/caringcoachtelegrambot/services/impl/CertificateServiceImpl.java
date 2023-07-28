package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Certificate;
import com.caringcoachtelegrambot.repositories.CertificateRepository;
import com.caringcoachtelegrambot.services.CertificateService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository repository;

    private final CacheManager cacheManager;


    public CertificateServiceImpl(CertificateRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cacheManager = cacheManager;
    }

    @Override
    @Cacheable(cacheNames = "certificates", key = "#certificate.id")
    public Certificate postCertificate(Certificate certificate) {
        if (certificate != null && certificate.getFileAsArrayOfBytes() != null) {
            return repository.save(certificate);
        } else throw new NotValidDataException();
    }

    @Override
    @CacheEvict(cacheNames = "certificates", key = "#certificate.id")
    public void deleteCertificate(Certificate certificate) {
        if (certificate != null && certificate.getFileAsArrayOfBytes() != null) {
            repository.delete(certificate);
        } else throw new NotValidDataException();
    }


}
