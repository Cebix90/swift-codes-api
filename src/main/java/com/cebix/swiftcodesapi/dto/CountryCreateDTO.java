package com.cebix.swiftcodesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CountryCreateDTO {
    @NotBlank(message = "Country name cannot be blank")
    @Size(max = 100, message = "Country name can't be longer than 100 characters")
    private String name;

    @NotBlank(message = "ISO code cannot be blank")
    @Size(min = 2, max = 2, message = "ISO code must be exactly 2 characters")
    private String isoCode;
}