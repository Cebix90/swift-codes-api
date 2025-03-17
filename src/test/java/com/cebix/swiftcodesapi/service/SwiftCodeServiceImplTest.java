package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import com.cebix.swiftcodesapi.mapper.SwiftCodeMapper;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.repository.SwiftCodeRepository;
import com.cebix.swiftcodesapi.service.impl.SwiftCodeServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class SwiftCodeServiceImplTest {

    private static final Long COUNTRY_ID = 1L;
    private static final String COUNTRY_NAME = "Poland";
    private static final String COUNTRY_ISO = "PL";

    private static final String SWIFT_CODE = "CODE1";
    private static final String BANK_NAME = "Bank 1";
    private static final String ADDRESS = "Main St 1";
    private static final boolean IS_HEADQUARTER = true;

    private AutoCloseable closeable;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private SwiftCodeMapper swiftCodeMapper;

    @InjectMocks
    private SwiftCodeServiceImpl swiftCodeService;

    private Country country;
    private SwiftCode swiftCode;
    private SwiftCodeDTO swiftCodeDTO;
    private SwiftCodeCreateDTO swiftCodeCreateDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        country = Country.builder()
                .id(COUNTRY_ID)
                .name(COUNTRY_NAME)
                .isoCode(COUNTRY_ISO)
                .build();

        swiftCode = SwiftCode.builder()
                .id(1L)
                .swiftCode(SWIFT_CODE)
                .bankName(BANK_NAME)
                .address(ADDRESS)
                .isHeadquarter(IS_HEADQUARTER)
                .country(country)
                .build();

        swiftCodeDTO = new SwiftCodeDTO();
        swiftCodeDTO.setSwiftCode(SWIFT_CODE);
        swiftCodeDTO.setBankName(BANK_NAME);
        swiftCodeDTO.setAddress(ADDRESS);
        swiftCodeDTO.setCountryISO2(COUNTRY_ISO);
        swiftCodeDTO.setCountryName(COUNTRY_NAME);
        swiftCodeDTO.setHeadquarter(IS_HEADQUARTER);

        swiftCodeCreateDTO = new SwiftCodeCreateDTO();
        swiftCodeCreateDTO.setSwiftCode(SWIFT_CODE);
        swiftCodeCreateDTO.setBankName(BANK_NAME);
        swiftCodeCreateDTO.setAddress(ADDRESS);
        swiftCodeCreateDTO.setCountryISO2(COUNTRY_ISO);
        swiftCodeCreateDTO.setCountryName(COUNTRY_NAME);
        swiftCodeCreateDTO.setHeadquarter(IS_HEADQUARTER);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should return SwiftCode by code")
    void shouldReturnSwiftCodeByCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swiftCode));
        when(swiftCodeMapper.toDTO(swiftCode)).thenReturn(swiftCodeDTO);
        when(swiftCodeRepository.findAllByHeadquarterEntity(swiftCode)).thenReturn(List.of());

        SwiftCodeDTO result = swiftCodeService.getSwiftCode(SWIFT_CODE);

        assertThat(result).isNotNull();
        assertThat(result.getSwiftCode()).isEqualTo(SWIFT_CODE);

        verify(swiftCodeRepository).findBySwiftCode(SWIFT_CODE);
        verify(swiftCodeMapper).toDTO(swiftCode);
    }

    @Test
    @DisplayName("Should throw exception when SwiftCode not found by code")
    void shouldThrowWhenSwiftCodeNotFoundByCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.getSwiftCode(SWIFT_CODE))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found");

        verify(swiftCodeRepository).findBySwiftCode(SWIFT_CODE);
    }

    @Test
    @DisplayName("Should return SwiftCodes by country ISO2")
    void shouldReturnSwiftCodesByCountryISO2() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeRepository.findAllByCountry_Id(COUNTRY_ID)).thenReturn(List.of(swiftCode));
        when(swiftCodeMapper.toDTO(swiftCode)).thenReturn(swiftCodeDTO);

        List<SwiftCodeDTO> result = swiftCodeService.getSwiftCodesByCountryISO2(COUNTRY_ISO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSwiftCode()).isEqualTo(SWIFT_CODE);

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
        verify(swiftCodeRepository).findAllByCountry_Id(COUNTRY_ID);
    }

    @Test
    @DisplayName("Should throw exception when country not found by ISO2")
    void shouldThrowWhenCountryNotFoundByISO2() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.getSwiftCodesByCountryISO2(COUNTRY_ISO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should create SwiftCode when code is unique and country exists")
    void shouldCreateSwiftCode() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(false);
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeMapper.toEntity(swiftCodeCreateDTO)).thenReturn(swiftCode);

        swiftCodeService.createSwiftCode(swiftCodeCreateDTO);

        verify(swiftCodeRepository).save(swiftCode);
    }

    @Test
    @DisplayName("Should throw exception when creating SwiftCode with existing code")
    void shouldThrowWhenCreatingSwiftCodeWithExistingCode() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(true);

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(swiftCodeCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SwiftCode already exists");

        verify(swiftCodeRepository).existsBySwiftCode(SWIFT_CODE);
    }

    @Test
    @DisplayName("Should throw exception when creating SwiftCode with non-existing country")
    void shouldThrowWhenCreatingSwiftCodeWithNonExistingCountry() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(false);
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(swiftCodeCreateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");

        verify(swiftCodeRepository).existsBySwiftCode(SWIFT_CODE);
        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should delete SwiftCode when exists")
    void shouldDeleteSwiftCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.of(swiftCode));

        swiftCodeService.deleteSwiftCode(SWIFT_CODE);

        verify(swiftCodeRepository).delete(swiftCode);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing SwiftCode")
    void shouldThrowWhenDeletingNonExistingSwiftCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.deleteSwiftCode(SWIFT_CODE))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found");

        verify(swiftCodeRepository).findBySwiftCode(SWIFT_CODE);
    }
}