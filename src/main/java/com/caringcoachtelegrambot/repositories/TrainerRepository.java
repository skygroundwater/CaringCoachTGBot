package com.caringcoachtelegrambot.repositories;

import com.caringcoachtelegrambot.models.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query(nativeQuery = true, value = "select * from about_trainer join athletes on trainer_id where athletes.id =:athleteId")
    Trainer findTrainerByAthleteId(Long athleteId);

}
