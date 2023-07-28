package com.caringcoachtelegrambot.repositories;

import com.caringcoachtelegrambot.models.OnlineTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnlineTrainingRepository extends JpaRepository<OnlineTraining, Long> {




}
