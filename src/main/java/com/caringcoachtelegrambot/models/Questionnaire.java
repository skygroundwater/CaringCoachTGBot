package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.models.inserts.Parameters;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "questionnaires")
@NoArgsConstructor
@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Questionnaire extends Parameters {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "target")
    private String targetOfTrainings;

    @Column(name = "restrictions")
    private String restrictions;

    @Column(name = "experience")
    private String experience;

    @Column(name = "nutrition")
    private String nutrition;

    @Column(name = "equipment")
    private String equipment;

    @Column(name = "preferences")
    private String preferences;

    @Column(name = "checked")
    private boolean checked;

    @Column(name = "registered")
    private boolean registered;

}