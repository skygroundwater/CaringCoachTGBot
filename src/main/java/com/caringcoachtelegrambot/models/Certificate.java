package com.caringcoachtelegrambot.models;

import com.caringcoachtelegrambot.models.inserts.CaringCoachBotModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "certificates")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ToString
public class Certificate extends CaringCoachBotModel {

    @Id
    private Long id;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Column(name = "scan")
    private byte[] fileAsArrayOfBytes;

    @Column(name = "description")
    private String description;

}