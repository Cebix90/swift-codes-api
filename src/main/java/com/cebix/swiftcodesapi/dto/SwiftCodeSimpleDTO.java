package com.cebix.swiftcodesapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SwiftCodeSimpleDTO {
    private String address;
    private String bankName;
    private String countryISO2;
    private Boolean isHeadquarter;
    private String swiftCode;
}