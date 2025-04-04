package com.bank.publicinfo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "branch", schema = "public_bank_information", uniqueConstraints = {
        @UniqueConstraint(name = "branch_pk_2", columnNames = {"phone_number"})
})
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 370)
    @NotNull
    @Column(name = "address", nullable = false, length = 370)
    private String address;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @Size(max = 250)
    @NotNull
    @Column(name = "city", nullable = false, length = 250)
    private String city;

    @NotNull
    @Column(name = "start_of_work", nullable = false)
    private LocalTime startOfWork;

    @NotNull
    @Column(name = "end_of_work", nullable = false)
    private LocalTime endOfWork;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ATM> atms;

}