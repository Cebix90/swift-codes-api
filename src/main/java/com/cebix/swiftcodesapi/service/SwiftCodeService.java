package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.entity.SwiftCode;

import java.util.List;

public interface SwiftCodeService {
    List<SwiftCode> getAllSwiftCodes();

    List<SwiftCode> getSwiftCodesByCountryId(Long countryId);

    SwiftCode getSwiftCodeById(Long id);

    SwiftCode createSwiftCode(SwiftCode swiftCode);

    SwiftCode updateSwiftCode(Long id, SwiftCode swiftCode);

    void deleteSwiftCode(Long id);

    boolean existsBySwiftCode(String swiftCode);
}
