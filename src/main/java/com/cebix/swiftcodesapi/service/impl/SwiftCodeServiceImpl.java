package com.cebix.swiftcodesapi.service.impl;

import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import com.cebix.swiftcodesapi.mapper.SwiftCodeMapper;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.repository.SwiftCodeRepository;
import com.cebix.swiftcodesapi.service.SwiftCodeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwiftCodeServiceImpl implements SwiftCodeService {

    private final SwiftCodeRepository swiftCodeRepository;
    private final CountryRepository countryRepository;
    private final SwiftCodeMapper swiftCodeMapper;

    @Override
    public SwiftCodeDTO getSwiftCode(String swiftCode) {
        SwiftCode code = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found: " + swiftCode));

        SwiftCodeDTO dto = swiftCodeMapper.toDTO(code);

        if (code.isHeadquarter()) {
            List<SwiftCodeDTO> branches = swiftCodeRepository.findAllByHeadquarterEntity(code).stream()
                    .map(swiftCodeMapper::toDTO)
                    .collect(Collectors.toList());

            dto.setBranches(branches);
        }

        return dto;
    }

    @Override
    public List<SwiftCodeDTO> getSwiftCodesByCountryISO2(String countryISO2) {
        Country country = countryRepository.findByIsoCode(countryISO2)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + countryISO2));

        return swiftCodeRepository.findAllByCountry_Id(country.getId()).stream()
                .map(swiftCodeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void createSwiftCode(SwiftCodeCreateDTO dto) {
        if (swiftCodeRepository.existsBySwiftCode(dto.getSwiftCode())) {
            throw new IllegalArgumentException("SwiftCode already exists: " + dto.getSwiftCode());
        }

        Country country = countryRepository.findByIsoCode(dto.getCountryISO2())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + dto.getCountryISO2()));

        SwiftCode entity = swiftCodeMapper.toEntity(dto);
        entity.setCountry(country);

        swiftCodeRepository.save(entity);
    }

    @Override
    public void deleteSwiftCode(String swiftCode) {
        SwiftCode entity = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found: " + swiftCode));

        swiftCodeRepository.delete(entity);
    }
}