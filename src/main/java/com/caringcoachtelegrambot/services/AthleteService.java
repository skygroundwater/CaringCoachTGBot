package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.Athlete;

import java.util.List;

public interface AthleteService {

    Athlete findAthleteById(Long id);

    Athlete postAthlete(Athlete athlete);

    void deleteAthlete(Athlete athlete);

    Athlete putAthlete(Athlete athlete);

    List<Athlete> findAll();
}

