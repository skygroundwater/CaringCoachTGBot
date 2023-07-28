package com.caringcoachtelegrambot.models.inserts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

@NoArgsConstructor
@Data
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@MappedSuperclass
public abstract class Parameters {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "age")
    private String age;

}