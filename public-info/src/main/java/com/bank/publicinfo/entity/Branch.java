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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "branch", schema = "public_info", uniqueConstraints = {
        @UniqueConstraint(name = "branch_pk_2", columnNames = {"phone_number"})
})
public class Branch {

    public static final int ADDRESS_MAX_LENGTH = 370;
    public static final int CITY_MAX_LENGTH = 250;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = ADDRESS_MAX_LENGTH)
    @NotNull
    @Column(name = "address", nullable = false, length = ADDRESS_MAX_LENGTH)
    private String address;

    @NotNull
    @Column(name = "phone_number", nullable = false)
    private Long phoneNumber;

    @Size(max = CITY_MAX_LENGTH)
    @NotNull
    @Column(name = "city", nullable = false, length = CITY_MAX_LENGTH)
    private String city;

    @NotNull
    @Column(name = "start_of_work", nullable = false)
    private LocalTime startOfWork;

    @NotNull
    @Column(name = "end_of_work", nullable = false)
    private LocalTime endOfWork;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "branch",
            cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<ATM> atms;

}
