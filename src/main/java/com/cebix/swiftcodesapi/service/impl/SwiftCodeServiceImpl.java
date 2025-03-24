package com.cebix.swiftcodesapi.service.impl;

import com.cebix.swiftcodesapi.dto.CountrySwiftCodesDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeSimpleDTO;
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

@Service
@RequiredArgsConstructor
public class SwiftCodeServiceImpl implements SwiftCodeService {

    private final SwiftCodeRepository swiftCodeRepository;
    private final CountryRepository countryRepository;
    private final SwiftCodeMapper swiftCodeMapper;

    @Override
    public SwiftCodeDTO getSwiftCode(String swiftCode) {
        SwiftCode entity = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found: " + swiftCode));

        SwiftCodeDTO dto = swiftCodeMapper.toDTO(entity);

        if (entity.isHeadquarter()) {
            List<SwiftCodeSimpleDTO> branchDTOs = swiftCodeRepository.findAllByHeadquarterEntity(entity)
                    .stream()
                    .map(swiftCodeMapper::toSimpleDTO)
                    .toList();
            dto.setBranches(branchDTOs);
        } else {
            dto.setBranches(null);
        }

        return dto;
    }

    @Override
    public CountrySwiftCodesDTO getSwiftCodesByCountryISO2(String countryISO2) {
        Country country = countryRepository.findByIsoCode(countryISO2)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + countryISO2));

        List<SwiftCodeSimpleDTO> swiftCodes = swiftCodeRepository.findAllByCountry_Id(country.getId())
                .stream()
                .map(swiftCodeMapper::toSimpleDTO)
                .toList();

        return CountrySwiftCodesDTO.builder()
                .countryISO2(country.getIsoCode())
                .countryName(country.getName())
                .swiftCodes(swiftCodes)
                .build();
    }

    @Override
    public void createSwiftCode(SwiftCodeCreateDTO dto) {
        if (swiftCodeRepository.existsBySwiftCode(dto.getSwiftCode())) {
            throw new IllegalArgumentException("SwiftCode already exists: " + dto.getSwiftCode());
        }

        Country country = countryRepository.findByIsoCode(dto.getCountryISO2().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with ISO2: " + dto.getCountryISO2()));

        SwiftCode entity = swiftCodeMapper.toEntity(dto);

        entity.setCountry(country);

        entity.setHeadquarter(Boolean.TRUE.equals(dto.getIsHeadquarter()));

        if (Boolean.FALSE.equals(dto.getIsHeadquarter())) {
            String hqSwiftCode = dto.getSwiftCode().substring(0, 8) + "XXX";
            SwiftCode headquarter = swiftCodeRepository.findBySwiftCode(hqSwiftCode)
                    .orElseThrow(() -> new EntityNotFoundException("Headquarter not found for branch: " + dto.getSwiftCode()));

            entity.setHeadquarterEntity(headquarter);
        }

        swiftCodeRepository.save(entity);
    }

    @Override
    public void deleteSwiftCode(String swiftCode) {
        SwiftCode entity = swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found: " + swiftCode));

        swiftCodeRepository.delete(entity);
    }
}