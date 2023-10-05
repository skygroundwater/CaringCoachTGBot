package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "about_trainer")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ToString
public class Trainer extends CaringCoachBotModel {

    @Id
    private Long id;

    @Column(name = "about")
    private String about;

    @Column(name = "cause")
    private String cause;

    @Column(name = "password")
    private String password;

    @Column(name = "login")
    private String login;

    @Column(name = "link")
    private String link;

    @Column(name = "dietary_guide")
    private String dietaryGuide;
}