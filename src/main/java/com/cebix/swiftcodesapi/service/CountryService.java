package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.entity.Country;

import java.util.List;

public interface CountryService {
    List<Country> getAllCountries();

    Country getCountryById(Long id);

    Country createCountry(Country country);

    Country updateCountry(Long id, Country country);

    void deleteCountry(Long id);

    boolean existsByIsoCode(String isoCode);
}
