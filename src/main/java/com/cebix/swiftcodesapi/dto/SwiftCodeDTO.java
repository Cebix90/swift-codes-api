package com.cebix.swiftcodesapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class SwiftCodeDTO {
    private String address;
    private String bankName;
    private String countryISO2;
    private String countryName;
    private boolean isHeadquarter;
    private String swiftCode;

    private List<SwiftCodeDTO> branches;
}
