package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Certificate;

public interface CertificateService {
    Certificate postCertificate(Certificate certificate);

    void deleteCertificate(Certificate certificate);
}
