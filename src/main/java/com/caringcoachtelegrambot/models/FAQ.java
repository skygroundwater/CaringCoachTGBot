package com.caringcoachtelegrambot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "faq")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FAQ {

    @Id
    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    @Column(name = "athlete_id")
    private Long athleteId;

    public FAQ(String question, Long chatId) {
        this.question = question;
        this.athleteId = chatId;
        this.answer = null;
    }

}
