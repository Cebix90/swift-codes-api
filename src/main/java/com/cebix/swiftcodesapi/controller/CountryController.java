package com.cebix.swiftcodesapi.controller;

import com.cebix.swiftcodesapi.dto.CountryCreateDTO;
import com.cebix.swiftcodesapi.dto.CountryDTO;
import com.cebix.swiftcodesapi.dto.MessageResponseDTO;
import com.cebix.swiftcodesapi.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<List<CountryDTO>> getAllCountries() {
        List<CountryDTO> result = countryService.getAllCountries();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{isoCode}")
    public ResponseEntity<CountryDTO> getCountryByIsoCode(@PathVariable String isoCode) {
        CountryDTO result = countryService.getCountryByIsoCode(isoCode.toUpperCase());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<CountryDTO> createCountry(@Valid @RequestBody CountryCreateDTO countryCreateDTO) {
        CountryDTO result = countryService.createCountry(countryCreateDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{isoCode}")
    public ResponseEntity<CountryDTO> updateCountry(@PathVariable String isoCode, @Valid @RequestBody CountryCreateDTO countryCreateDTO) {
        CountryDTO result = countryService.updateCountry(isoCode.toUpperCase(), countryCreateDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{isoCode}")
    public ResponseEntity<MessageResponseDTO> deleteCountry(@PathVariable String isoCode) {
        countryService.deleteCountry(isoCode.toUpperCase());
        return ResponseEntity.ok(new MessageResponseDTO("Country successfully deleted"));
    }
}
