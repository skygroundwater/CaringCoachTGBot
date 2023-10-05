package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Report;

public interface ReportService extends Service<Report, Long> {

    Report findOneUncheckedReport();

}
