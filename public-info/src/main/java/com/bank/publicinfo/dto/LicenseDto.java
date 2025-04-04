package com.bank.publicinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LicenseDto implements Serializable {

    private Long id;
    private byte[] photo;
    private Long bankDetailsId;

}
