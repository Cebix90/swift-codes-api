package com.cebix.swiftcodesapi.service.impl;

import com.cebix.swiftcodesapi.dto.CountryDTO;
import com.cebix.swiftcodesapi.dto.CountryCreateDTO;
import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.mapper.CountryMapper;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.service.CountryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CountryDTO getCountryByIsoCode(String isoCode) {
        Country country = countryRepository.findByIsoCode(isoCode)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + isoCode));

        return countryMapper.toDTO(country);
    }

    @Override
    public CountryDTO createCountry(CountryCreateDTO dto) {
        if (countryRepository.existsByIsoCode(dto.getIsoCode())) {
            throw new IllegalArgumentException("Country with ISO code already exists: " + dto.getIsoCode());
        }

        Country country = countryMapper.toEntity(dto);
        Country savedCountry = countryRepository.save(country);

        return countryMapper.toDTO(savedCountry);
    }

    @Override
    public CountryDTO updateCountry(String isoCode, CountryCreateDTO dto) {
        Country existingCountry = countryRepository.findByIsoCode(isoCode)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + isoCode));

        existingCountry.setName(dto.getName());
        existingCountry.setIsoCode(dto.getIsoCode());

        Country updatedCountry = countryRepository.save(existingCountry);

        return countryMapper.toDTO(updatedCountry);
    }

    @Override
    public void deleteCountry(String isoCode) {
        Country country = countryRepository.findByIsoCode(isoCode)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + isoCode));

        countryRepository.delete(country);
    }
}