package com.caringcoachtelegrambot.services;

import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;

import java.util.List;

public interface Service<C extends CaringCoachBotModel, O> {

    C post(C c);

    C put(C c);

    void delete(C c);

    C findById(O o);

    List<C> findAll();

}
