package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.enums.Role;
import com.caringcoachtelegrambot.models.inserts.Parameters;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.AccessLevel;

import java.util.List;

@Table(name = "athletes")
@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = true)
public class Athlete extends Parameters {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "athlete")
    private List<OnlineTraining> trainings;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Trainer trainer;

    public Athlete buildParameters(String firstName, String secondName,
                                   String age, String height, String weight){
        this.setFirstName(firstName);
        this.setSecondName(secondName);
        this.setAge(age);
        this.setHeight(height);
        this.setWeight(weight);
        return this;
    }

}