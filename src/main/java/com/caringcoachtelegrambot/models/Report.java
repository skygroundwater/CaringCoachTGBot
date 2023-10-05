package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report extends CaringCoachBotModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "state")
    private String state;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "weight")
    private String weight;

    @Column(name = "wishes")
    private String wishes;

    @Column(name = "checked")
    private boolean checked;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id", name = "athlete")
    private Athlete athlete;

}
