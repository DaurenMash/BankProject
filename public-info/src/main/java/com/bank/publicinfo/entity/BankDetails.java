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

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_details", schema = "public_info", uniqueConstraints = {
        @UniqueConstraint(name = "bank_details_pk_2", columnNames = {"bik"}),
        @UniqueConstraint(name = "bank_details_pk_3", columnNames = {"inn"}),
        @UniqueConstraint(name = "bank_details_pk_4", columnNames = {"kpp"}),
        @UniqueConstraint(name = "bank_details_pk_5", columnNames = {"cor_account"})
})
public class BankDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "bik", nullable = false)
    private Long bik;

    @NotNull
    @Column(name = "inn", nullable = false)
    private Long inn;

    @NotNull
    @Column(name = "kpp", nullable = false)
    private Long kpp;

    @NotNull
    @Column(name = "cor_account", nullable = false)
    private Long cor_account;

    @Size(max = 180)
    @NotNull
    @Column(name = "city", nullable = false, length = 180)
    private String city;

    @Size(max = 155)
    @NotNull
    @Column(name = "joint_stock_company", nullable = false, length = 155)
    private String joint_stock_company;

    @Size(max = 80)
    @NotNull
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bankDetails",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<License> licenses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bankDetails",
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Certificate> certificates;


}