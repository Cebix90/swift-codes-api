package com.cebix.swiftcodesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SwiftCodeCreateDTO {
    @NotBlank
    private String address;

    @NotBlank
    private String bankName;

    @NotBlank
    @Size(min = 2, max = 2)
    private String countryISO2;

    @NotBlank
    private String countryName;

    private boolean isHeadquarter;

    @NotBlank
    private String swiftCode;
}
