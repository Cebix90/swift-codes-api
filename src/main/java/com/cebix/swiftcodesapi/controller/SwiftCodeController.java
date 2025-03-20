package com.cebix.swiftcodesapi.controller;

import com.cebix.swiftcodesapi.dto.CountrySwiftCodesDTO;
import com.cebix.swiftcodesapi.dto.MessageResponseDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.service.SwiftCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/swift-codes")
@RequiredArgsConstructor
public class SwiftCodeController {
    private final SwiftCodeService swiftCodeService;

    @GetMapping("/{swiftCode}")
    public ResponseEntity<SwiftCodeDTO> getSwiftCode(@PathVariable String swiftCode) {
        SwiftCodeDTO result = swiftCodeService.getSwiftCode(swiftCode);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/country/{countryISO2}")
    public ResponseEntity<CountrySwiftCodesDTO> getSwiftCodesByCountry(@PathVariable String countryISO2) {
        CountrySwiftCodesDTO result = swiftCodeService.getSwiftCodesByCountryISO2(countryISO2.toUpperCase());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createSwiftCode(@Valid @RequestBody SwiftCodeCreateDTO dto) {
        swiftCodeService.createSwiftCode(dto);
        return ResponseEntity.ok(new MessageResponseDTO("SwiftCode " + dto.getSwiftCode() + " successfully created"));
    }

    @DeleteMapping("/{swiftCode}")
    public ResponseEntity<MessageResponseDTO> deleteSwiftCode(@PathVariable String swiftCode) {
        swiftCodeService.deleteSwiftCode(swiftCode);
        return ResponseEntity.ok(new MessageResponseDTO("SwiftCode " + swiftCode + " successfully deleted"));
    }
}
