package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.enums.Role;
import com.caringcoachtelegrambot.models.inserts.Parameters;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import lombok.*;

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