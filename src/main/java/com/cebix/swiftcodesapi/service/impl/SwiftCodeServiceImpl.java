package com.cebix.swiftcodesapi.service.impl;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
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

    @Override
    public List<SwiftCode> getAllSwiftCodes() {
        return swiftCodeRepository.findAll();
    }

    @Override
    public List<SwiftCode> getSwiftCodesByCountryId(Long countryId) {
        if (!countryRepository.existsById(countryId)) {
            throw new EntityNotFoundException("Country not found with id: " + countryId);
        }
        return swiftCodeRepository.findAllByCountry_Id(countryId);
    }

    @Override
    public SwiftCode getSwiftCodeById(Long id) {
        return swiftCodeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found with id: " + id));
    }

    @Override
    public SwiftCode createSwiftCode(SwiftCode swiftCode) {
        if (swiftCodeRepository.existsBySwiftCode(swiftCode.getSwiftCode())) {
            throw new IllegalArgumentException("SwiftCode already exists: " + swiftCode.getSwiftCode());
        }

        Long countryId = swiftCode.getCountry().getId();
        Country country = countryRepository.findById(countryId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + countryId));

        swiftCode.setCountry(country);
        return swiftCodeRepository.save(swiftCode);
    }

    @Override
    public SwiftCode updateSwiftCode(Long id, SwiftCode updatedSwiftCode) {
        SwiftCode existing = swiftCodeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SwiftCode not found with id: " + id));

        existing.setSwiftCode(updatedSwiftCode.getSwiftCode());
        existing.setBankName(updatedSwiftCode.getBankName());
        existing.setBranchName(updatedSwiftCode.getBranchName());

        Long newCountryId = updatedSwiftCode.getCountry().getId();
        Country newCountry = countryRepository.findById(newCountryId)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with id: " + newCountryId));

        existing.setCountry(newCountry);

        return swiftCodeRepository.save(existing);
    }

    @Override
    public void deleteSwiftCode(Long id) {
        if (!swiftCodeRepository.existsById(id)) {
            throw new EntityNotFoundException("SwiftCode not found with id: " + id);
        }
        swiftCodeRepository.deleteById(id);
    }

    @Override
    public boolean existsBySwiftCode(String swiftCode) {
        return swiftCodeRepository.existsBySwiftCode(swiftCode);
    }
}
