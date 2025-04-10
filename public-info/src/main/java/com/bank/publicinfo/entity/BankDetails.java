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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.Set;

@Getter
@Setter
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

    public static final int CITY_MAX_LENGTH = 180;
    public static final int JOINT_STOCK_COMPANY_MAX_LENGTH = 155;
    public static final int NAME_MAX_LENGTH = 80;

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
    private Long corAccount;

    @Size(max = CITY_MAX_LENGTH)
    @NotNull
    @Column(name = "city", nullable = false, length = CITY_MAX_LENGTH)
    private String city;

    @Size(max = JOINT_STOCK_COMPANY_MAX_LENGTH)
    @NotNull
    @Column(name = "joint_stock_company", nullable = false, length = JOINT_STOCK_COMPANY_MAX_LENGTH)
    private String jointStockCompany;

    @Size(max = NAME_MAX_LENGTH)
    @NotNull
    @Column(name = "name", nullable = false, length = NAME_MAX_LENGTH)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bankDetails",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<License> licenses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bankDetails",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<Certificate> certificates;

}
