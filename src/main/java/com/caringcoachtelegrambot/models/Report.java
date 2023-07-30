package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reports")
public class Report extends CaringCoachBotModel {

    @Id
    private Long id;

}
