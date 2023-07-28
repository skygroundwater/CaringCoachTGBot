package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.OnlineTraining;

import java.util.List;

public interface OnlineTrainingService {

    List<OnlineTraining> findAll();

    OnlineTraining postTraining(OnlineTraining training);

    void deleteTraining(OnlineTraining training);

    OnlineTraining putTraining(OnlineTraining training);

    OnlineTraining findTraining(OnlineTraining training);
}
