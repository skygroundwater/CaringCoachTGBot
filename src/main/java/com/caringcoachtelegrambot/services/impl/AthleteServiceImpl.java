package com.caringcoachtelegrambot.services.impl;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.Athlete;
import com.caringcoachtelegrambot.repositories.AthleteRepository;
import com.caringcoachtelegrambot.services.AthleteService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AthleteServiceImpl implements AthleteService {

    private final AthleteRepository repository;

    public AthleteServiceImpl(AthleteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Athlete findAthleteById(Long id){
        return repository.findById(id).orElseThrow(NotFoundInDataBaseException::new);
    }

    @Override
    public Athlete postAthlete(Athlete athlete){
        if(athlete == null) throw new NotValidDataException();
        return repository.save(athlete);
    }

    @Override
    public void deleteAthlete(Athlete athlete){
        if(athlete == null) throw new NotValidDataException();
        repository.delete(athlete);
    }

    @Override
    public Athlete putAthlete(Athlete athlete){
        if(athlete == null) throw new NotValidDataException();
        return repository.save(athlete);
    }

    @Override
    public List<Athlete> findAll(){
        return repository.findAll();
    }

}
