package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.dto.CountryCreateDTO;
import com.cebix.swiftcodesapi.dto.CountryDTO;
import com.cebix.swiftcodesapi.entity.Country;

import java.util.List;

public interface CountryService {
//    List<Country> getAllCountries();
//
//    Country getCountryById(Long id);
//
//    Country createCountry(Country country);
//
//    Country updateCountry(Long id, Country country);
//
//    void deleteCountry(Long id);
//
//    boolean existsByIsoCode(String isoCode);

    List<CountryDTO> getAllCountries();
    CountryDTO getCountryByIsoCode(String isoCode);
    CountryDTO createCountry(CountryCreateDTO dto);
    CountryDTO updateCountry(String isoCode, CountryCreateDTO dto);
    void deleteCountry(String isoCode);
}
