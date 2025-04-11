package com.bank.publicinfo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDto implements Serializable {

    private Long id;

    @NotNull
    private byte[] photo;

    @NotNull
    private Long bankDetailsId;

}
