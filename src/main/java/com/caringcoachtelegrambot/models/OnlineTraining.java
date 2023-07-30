package com.caringcoachtelegrambot.models;


import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "online_trainings")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class OnlineTraining extends CaringCoachBotModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id", name = "athlete")
    private Athlete athlete;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "time")
    private LocalTime time;

    @Column(name = "done")
    private boolean done;

    @Column(name = "description")
    private String description;

}