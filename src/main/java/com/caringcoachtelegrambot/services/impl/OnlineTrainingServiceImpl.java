package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.OnlineTraining;
import com.caringcoachtelegrambot.repositories.OnlineTrainingRepository;
import com.caringcoachtelegrambot.services.OnlineTrainingService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OnlineTrainingServiceImpl implements OnlineTrainingService {

    private final OnlineTrainingRepository repository;

    public OnlineTrainingServiceImpl(OnlineTrainingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OnlineTraining> findAll() {
        return repository.findAll();
    }


    @Override
    public OnlineTraining postTraining(OnlineTraining training) {
        if (training == null) throw new NotValidDataException();
        else return repository.save(training);
    }

    @Override
    public void deleteTraining(OnlineTraining training) {
        if (training == null) throw new NotValidDataException();
        else repository.delete(training);
    }

    @Override
    public OnlineTraining putTraining(OnlineTraining training) {
        if (training == null) throw new NotValidDataException();
        else return repository.save(training);
    }

    @Override
    public OnlineTraining findTraining(OnlineTraining training) {
        if (training == null) throw new NotValidDataException();
        else return repository.findById(training.getId()).orElseThrow(NotFoundInDataBaseException::new);
    }

}
