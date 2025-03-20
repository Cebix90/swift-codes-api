package com.cebix.swiftcodesapi.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CountrySwiftCodesDTO {
    private String countryISO2;
    private String countryName;
    private List<SwiftCodeSimpleDTO> swiftCodes;
}