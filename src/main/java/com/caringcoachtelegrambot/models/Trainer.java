package com.caringcoachtelegrambot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@Entity
@Table(name = "about_trainer")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ToString
public class Trainer {

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

    public Trainer(Long id) {
        this.id = id;
    }
}