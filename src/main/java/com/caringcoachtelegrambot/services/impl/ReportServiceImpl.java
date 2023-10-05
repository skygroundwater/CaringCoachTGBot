package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.models.Report;
import com.caringcoachtelegrambot.repositories.ReportRepository;
import com.caringcoachtelegrambot.services.AbstractRepoService;
import com.caringcoachtelegrambot.services.ReportService;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl extends AbstractRepoService<Report, Long> implements ReportService {
    public ReportServiceImpl(ReportRepository repository) {
        super(repository);
    }

    @Override
    public Report findOneUncheckedReport() {
        return getRepository().findAll().stream()
                .filter(report -> !report.isChecked())
                .findFirst().orElseThrow(NotFoundInDataBaseException::new);
    }
}
