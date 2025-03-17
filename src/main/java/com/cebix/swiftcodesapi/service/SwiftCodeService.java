package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;

import java.util.List;

public interface SwiftCodeService {

    SwiftCodeDTO getSwiftCode(String swiftCode);

    List<SwiftCodeDTO> getSwiftCodesByCountryISO2(String countryISO2);

    void createSwiftCode(SwiftCodeCreateDTO dto);

    void deleteSwiftCode(String swiftCode);
}