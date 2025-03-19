package com.cebix.swiftcodesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwiftCodeCreateDTO {
    @Size(max = 255)
    private String address;

    @NotBlank(message = "Bank name cannot be blank")
    @Size(max = 150)
    private String bankName;

    @NotBlank(message = "Country ISO2 cannot be blank")
    @Size(min = 2, max = 2)
    private String countryISO2;

    @NotBlank(message = "Country name cannot be blank")
    private String countryName;

    @NotNull(message = "isHeadquarter cannot be null")
    private Boolean isHeadquarter;

    @NotBlank(message = "SwiftCode cannot be blank")
    @Size(min = 8, max = 11)
    private String swiftCode;
}