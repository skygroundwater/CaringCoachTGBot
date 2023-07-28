package com.caringcoachtelegrambot.models;


import jakarta.persistence.*;
import lombok.*;
import org.aspectj.bridge.IMessage;
import org.springframework.web.service.annotation.GetExchange;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;


@Entity
@Table(name = "online_trainings")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder
public class OnlineTraining {

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