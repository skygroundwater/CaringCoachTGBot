package com.caringcoachtelegrambot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.ToString;

@Entity
@Table(name = "certificates")
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
@ToString
public class Certificate {

    @Id
    private Long id;

    @Column(name = "trainer_id")
    private Long trainerId;

    @Column(name = "scan")
    private byte[] fileAsArrayOfBytes;

    @Column(name = "description")
    private String description;

}