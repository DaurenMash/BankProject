package com.bank.publicinfo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "atm", schema = "public_info")
public class ATM {
    private static final int MAX_ADDRESS_LENGTH = 370;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = MAX_ADDRESS_LENGTH)
    @NotNull
    @Column(name = "address", nullable = false, length = MAX_ADDRESS_LENGTH)
    private String address;

    @Column(name = "start_of_work")
    @Nullable
    private LocalTime startOfWork;

    @Column(name = "end_of_work")
    @Nullable
    private LocalTime endOfWork;

    @NotNull
    @Column(name = "all_hours", nullable = false)
    private Boolean allHours;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotNull
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

}
