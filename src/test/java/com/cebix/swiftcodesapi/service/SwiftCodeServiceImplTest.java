package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
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

    private static final Long SWIFT_ID = 1L;
    private static final String SWIFT_CODE = "CODE1";
    private static final String BANK_NAME = "Bank 1";

    private AutoCloseable closeable;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private SwiftCodeServiceImpl swiftCodeService;

    private Country country;
    private SwiftCode swiftCode;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        country = createCountry();
        swiftCode = createSwiftCode(SWIFT_ID, SWIFT_CODE, BANK_NAME, country);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private Country createCountry() {
        return Country.builder()
                .id(COUNTRY_ID)
                .name(COUNTRY_NAME)
                .isoCode(COUNTRY_ISO)
                .build();
    }

    private SwiftCode createSwiftCode(Long id, String code, String bankName, Country country) {
        return SwiftCode.builder()
                .id(id)
                .swiftCode(code)
                .bankName(bankName)
                .country(country)
                .build();
    }

    @Test
    @DisplayName("Should return all SwiftCodes")
    void shouldReturnAllSwiftCodes() {
        when(swiftCodeRepository.findAll()).thenReturn(List.of(swiftCode));

        List<SwiftCode> result = swiftCodeService.getAllSwiftCodes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSwiftCode()).isEqualTo(SWIFT_CODE);
        verify(swiftCodeRepository).findAll();
    }

    @Test
    @DisplayName("Should return SwiftCodes by country ID")
    void shouldReturnSwiftCodesByCountryId() {
        when(countryRepository.existsById(COUNTRY_ID)).thenReturn(true);
        when(swiftCodeRepository.findAllByCountry_Id(COUNTRY_ID)).thenReturn(List.of(swiftCode));

        List<SwiftCode> result = swiftCodeService.getSwiftCodesByCountryId(COUNTRY_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCountry().getId()).isEqualTo(COUNTRY_ID);
        verify(swiftCodeRepository).findAllByCountry_Id(COUNTRY_ID);
    }

    @Test
    @DisplayName("Should throw exception when country not found for SwiftCodesByCountryId")
    void shouldThrowWhenCountryNotFoundForSwiftCodesByCountryId() {
        when(countryRepository.existsById(COUNTRY_ID)).thenReturn(false);

        assertThatThrownBy(() -> swiftCodeService.getSwiftCodesByCountryId(COUNTRY_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with id");
    }

    @Test
    @DisplayName("Should return SwiftCode by ID")
    void shouldReturnSwiftCodeById() {
        when(swiftCodeRepository.findById(SWIFT_ID)).thenReturn(Optional.of(swiftCode));

        SwiftCode result = swiftCodeService.getSwiftCodeById(SWIFT_ID);

        assertThat(result).isNotNull();
        assertThat(result.getSwiftCode()).isEqualTo(SWIFT_CODE);
        verify(swiftCodeRepository).findById(SWIFT_ID);
    }

    @Test
    @DisplayName("Should throw exception when SwiftCode not found by ID")
    void shouldThrowWhenSwiftCodeNotFoundById() {
        when(swiftCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.getSwiftCodeById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found with id");
    }

    @Test
    @DisplayName("Should create SwiftCode when code is unique and country exists")
    void shouldCreateSwiftCode() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(false);
        when(countryRepository.findById(COUNTRY_ID)).thenReturn(Optional.of(country));
        when(swiftCodeRepository.save(swiftCode)).thenReturn(swiftCode);

        SwiftCode result = swiftCodeService.createSwiftCode(swiftCode);

        assertThat(result).isNotNull();
        assertThat(result.getSwiftCode()).isEqualTo(SWIFT_CODE);
        verify(swiftCodeRepository).save(swiftCode);
    }

    @Test
    @DisplayName("Should throw exception when creating SwiftCode with existing code")
    void shouldThrowWhenCreatingSwiftCodeWithExistingCode() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(true);

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(swiftCode))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SwiftCode already exists");
    }

    @Test
    @DisplayName("Should throw exception when creating SwiftCode with non-existing country")
    void shouldThrowWhenCreatingSwiftCodeWithNonExistingCountry() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(false);
        when(countryRepository.findById(COUNTRY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(swiftCode))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with id");
    }

    @Test
    @DisplayName("Should update existing SwiftCode")
    void shouldUpdateSwiftCode() {
        SwiftCode updatedSwift = createSwiftCode(null, "CODE2", "Bank 2", country);
        updatedSwift.setBranchName("New Branch");

        when(swiftCodeRepository.findById(SWIFT_ID)).thenReturn(Optional.of(swiftCode));
        when(countryRepository.findById(COUNTRY_ID)).thenReturn(Optional.of(country));
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SwiftCode result = swiftCodeService.updateSwiftCode(SWIFT_ID, updatedSwift);

        assertThat(result.getSwiftCode()).isEqualTo("CODE2");
        assertThat(result.getBankName()).isEqualTo("Bank 2");
        assertThat(result.getBranchName()).isEqualTo("New Branch");
        verify(swiftCodeRepository).save(any(SwiftCode.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing SwiftCode")
    void shouldThrowWhenUpdatingNonExistingSwiftCode() {
        SwiftCode updatedSwift = createSwiftCode(null, "CODE2", "Bank 2", country);

        when(swiftCodeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.updateSwiftCode(999L, updatedSwift))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found with id");
    }

    @Test
    @DisplayName("Should delete SwiftCode when exists")
    void shouldDeleteSwiftCode() {
        when(swiftCodeRepository.existsById(SWIFT_ID)).thenReturn(true);

        swiftCodeService.deleteSwiftCode(SWIFT_ID);

        verify(swiftCodeRepository).deleteById(SWIFT_ID);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing SwiftCode")
    void shouldThrowWhenDeletingNonExistingSwiftCode() {
        when(swiftCodeRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> swiftCodeService.deleteSwiftCode(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found with id");
    }

    @Test
    @DisplayName("Should check if SwiftCode exists by code")
    void shouldCheckIfSwiftCodeExistsByCode() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE)).thenReturn(true);

        boolean exists = swiftCodeService.existsBySwiftCode(SWIFT_CODE);

        assertThat(exists).isTrue();
        verify(swiftCodeRepository).existsBySwiftCode(SWIFT_CODE);
    }
}
