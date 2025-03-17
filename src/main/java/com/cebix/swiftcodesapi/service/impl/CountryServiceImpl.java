package com.cebix.swiftcodesapi.service.impl;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;

    @Override
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @Override
    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));
    }

    @Override
    public Country createCountry(Country country) {
        if (countryRepository.existsByIsoCode(country.getIsoCode())) {
            throw new IllegalArgumentException("Country with ISO code already exists: " + country.getIsoCode());
        }
        return countryRepository.save(country);
    }

    @Override
    public Country updateCountry(Long id, Country updatedCountry) {
        Country existing = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + id));

        existing.setName(updatedCountry.getName());
        existing.setIsoCode(updatedCountry.getIsoCode());

        return countryRepository.save(existing);
    }

    @Override
    public void deleteCountry(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new EntityNotFoundException("Country not found with id: " + id);
        }
        countryRepository.deleteById(id);
    }

    @Override
    public boolean existsByIsoCode(String isoCode) {
        return countryRepository.existsByIsoCode(isoCode);
    }
}
