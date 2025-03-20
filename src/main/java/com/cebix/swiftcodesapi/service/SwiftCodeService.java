package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.dto.CountrySwiftCodesDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;

public interface SwiftCodeService {

    SwiftCodeDTO getSwiftCode(String swiftCode);

    CountrySwiftCodesDTO getSwiftCodesByCountryISO2(String countryISO2);

    void createSwiftCode(SwiftCodeCreateDTO dto);

    void deleteSwiftCode(String swiftCode);
}