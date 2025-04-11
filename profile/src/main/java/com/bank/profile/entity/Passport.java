package com.bank.profile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passport", schema = "profile")
public class Passport {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "passport_id_seq")
    private Long id;

    @NotNull
    @Column(name = "series", nullable = false)
    private Integer series;

    @NotNull
    @Column(name = "number", nullable = false)
    private Long number;

    @Size(max = 255)
    @NotNull
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(max = 255)
    @NotNull
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Size(max = 255)
    @Column(name = "middle_name")
    private String middleName;

    @Size(max = 3)
    @NotNull
    @Column(name = "gender", nullable = false, length = 3)
    private String gender;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Size(max = 480)
    @NotNull
    @Column(name = "birth_place", nullable = false, length = 480)
    private String birthPlace;

    @NotNull
    @Column(name = "issued_by", nullable = false, length = Integer.MAX_VALUE)
    private String issuedBy;

    @NotNull
    @Column(name = "date_of_issue", nullable = false)
    private LocalDate dateOfIssue;

    @NotNull
    @Column(name = "division_code", nullable = false)
    private Integer divisionCode;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registration_id", nullable = false)
    private Registration registration;
}