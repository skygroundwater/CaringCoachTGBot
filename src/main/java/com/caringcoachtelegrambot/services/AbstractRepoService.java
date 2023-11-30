package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.exceptions.NotFoundInDataBaseException;
import com.caringcoachtelegrambot.exceptions.NotValidDataException;
import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import lombok.Getter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Getter
public abstract class AbstractRepoService<C extends CaringCoachBotModel, O> implements Service<C, O> {

    private final JpaRepository<C, O> repository;

    public AbstractRepoService(JpaRepository<C, O> repository) {
        this.repository = repository;
    }

    @Override
    public C post(C c) {
        if (c != null) {
            return repository.save(c);
        } else throw new NotValidDataException();
    }

    @Override
    public C put(C c) {
        if (c != null) {
            return repository.save(c);
        } else throw new NotValidDataException();
    }

    @Override
    public void delete(C c) {
        if (c != null) {
            repository.delete(c);
        } else throw new NotValidDataException();
    }

    @Override
    public C findById(O o) {
        return repository.findById(o).orElseThrow(NotFoundInDataBaseException::new);
    }

    @Override
    public List<C> findAll() {
        return repository.findAll();
    }
}