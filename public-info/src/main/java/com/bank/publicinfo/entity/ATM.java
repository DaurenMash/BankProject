package com.bank.publicinfo.entity;

import jakarta.persistence.*;
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
@Table(name = "atm", schema = "public_bank_information")
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 370)
    @NotNull
    @Column(name = "address", nullable = false, length = 370)
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