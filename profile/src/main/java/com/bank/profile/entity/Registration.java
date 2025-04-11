package com.bank.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registration", schema = "profile")
public class Registration {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "registration_id_seq")
    private Long id;

    @Size(max = 166)
    @NotNull
    @Column(name = "country", nullable = false, length = 166)
    private String country;

    @Size(max = 160)
    @Column(name = "city", length = 160)
    private String city;

    @NotNull
    @Column(name = "index", nullable = false)
    private Long index;

    @Size(max = 160)
    @Column(name = "region", length = 160)
    private String region;

    @Size(max = 160)
    @Column(name = "district", length = 160)
    private String district;

    @Size(max = 160)
    @Column(name = "locality", length = 160)
    private String locality;

    @Size(max = 160)
    @Column(name = "street", length = 160)
    private String street;

    @Size(max = 20)
    @Column(name = "house_number", length = 20)
    private String houseNumber;

    @Size(max = 20)
    @Column(name = "house_block", length = 20)
    private String houseBlock;

    @Size(max = 40)
    @Column(name = "flat_number", length = 40)
    private String flatNumber;
}