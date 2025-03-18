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
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SwiftCodeServiceImplTest {

    private AutoCloseable closeable;

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private SwiftCodeMapper swiftCodeMapper;

    @InjectMocks
    private SwiftCodeServiceImpl swiftCodeService;

    private final Long COUNTRY_ID = 1L;
    private final String COUNTRY_NAME = "Poland";
    private final String COUNTRY_ISO = "PL";

    private final String SWIFT_CODE_HQ = "TESTPLPXXXX";
    private final String SWIFT_CODE_BRANCH = "TESTPLP123";

    private Country country;
    private SwiftCode hqSwiftCode;
    private SwiftCode branchSwiftCode;
    private SwiftCodeDTO hqSwiftCodeDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        country = Country.builder()
                .id(COUNTRY_ID)
                .isoCode(COUNTRY_ISO)
                .name(COUNTRY_NAME)
                .build();

        hqSwiftCode = SwiftCode.builder()
                .id(1L)
                .swiftCode(SWIFT_CODE_HQ)
                .bankName("Bank HQ")
                .address("Main HQ Address")
                .isHeadquarter(true)
                .country(country)
                .build();

        branchSwiftCode = SwiftCode.builder()
                .id(2L)
                .swiftCode(SWIFT_CODE_BRANCH)
                .bankName("Branch Bank")
                .address("Branch Address")
                .isHeadquarter(false)
                .country(country)
                .headquarterEntity(hqSwiftCode)
                .build();

        hqSwiftCodeDTO = SwiftCodeDTO.builder()
                .swiftCode(SWIFT_CODE_HQ)
                .bankName("Bank HQ")
                .address("Main HQ Address")
                .countryISO2(COUNTRY_ISO)
                .countryName(COUNTRY_NAME)
                .isHeadquarter(true)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should return SwiftCode DTO for HQ with branches")
    void shouldReturnSwiftCodeHQWithBranches() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE_HQ)).thenReturn(Optional.of(hqSwiftCode));
        when(swiftCodeMapper.toDTO(hqSwiftCode)).thenReturn(hqSwiftCodeDTO);
        when(swiftCodeRepository.findAllByHeadquarterEntity(hqSwiftCode)).thenReturn(List.of(branchSwiftCode));
        when(swiftCodeMapper.toDTO(branchSwiftCode)).thenReturn(
                SwiftCodeDTO.builder().swiftCode(SWIFT_CODE_BRANCH).bankName("Branch Bank").isHeadquarter(false).build()
        );

        SwiftCodeDTO result = swiftCodeService.getSwiftCode(SWIFT_CODE_HQ);

        assertThat(result.getSwiftCode()).isEqualTo(SWIFT_CODE_HQ);
        assertThat(result.getBranches()).hasSize(1);
        assertThat(result.getBranches().get(0).getSwiftCode()).isEqualTo(SWIFT_CODE_BRANCH);
    }

    @Test
    @DisplayName("Should return SwiftCode DTO for branch without branches")
    void shouldReturnSwiftCodeBranchWithoutBranches() {
        SwiftCodeDTO branchDTO = SwiftCodeDTO.builder()
                .swiftCode(SWIFT_CODE_BRANCH)
                .isHeadquarter(false)
                .build();

        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE_BRANCH)).thenReturn(Optional.of(branchSwiftCode));
        when(swiftCodeMapper.toDTO(branchSwiftCode)).thenReturn(branchDTO);

        SwiftCodeDTO result = swiftCodeService.getSwiftCode(SWIFT_CODE_BRANCH);

        assertThat(result.getSwiftCode()).isEqualTo(SWIFT_CODE_BRANCH);
        assertThat(result.getBranches()).isNull();
    }

    @Test
    @DisplayName("Should throw EntityNotFound when SwiftCode not found")
    void shouldThrowWhenSwiftCodeNotFound() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE_HQ)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.getSwiftCode(SWIFT_CODE_HQ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found");
    }

    @Test
    @DisplayName("Should return list of SwiftCodes by country ISO2")
    void shouldReturnSwiftCodesByCountryISO2() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeRepository.findAllByCountry_Id(COUNTRY_ID)).thenReturn(List.of(hqSwiftCode));
        when(swiftCodeMapper.toDTO(hqSwiftCode)).thenReturn(hqSwiftCodeDTO);

        List<SwiftCodeDTO> result = swiftCodeService.getSwiftCodesByCountryISO2(COUNTRY_ISO);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSwiftCode()).isEqualTo(SWIFT_CODE_HQ);
    }

    @Test
    @DisplayName("Should throw EntityNotFound when country ISO2 not found")
    void shouldThrowWhenCountryISO2NotFound() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.getSwiftCodesByCountryISO2(COUNTRY_ISO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");
    }

    @Test
    @DisplayName("Should create HQ SwiftCode")
    void shouldCreateHeadquarterSwiftCode() {
        SwiftCodeCreateDTO dto = SwiftCodeCreateDTO.builder()
                .swiftCode(SWIFT_CODE_HQ)
                .bankName("Bank HQ")
                .address("HQ Address")
                .countryISO2(COUNTRY_ISO)
                .countryName(COUNTRY_NAME)
                .isHeadquarter(true)
                .build();

        SwiftCode entity = SwiftCode.builder()
                .swiftCode(dto.getSwiftCode())
                .build();

        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE_HQ)).thenReturn(false);
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeMapper.toEntity(dto)).thenReturn(entity);

        swiftCodeService.createSwiftCode(dto);

        verify(swiftCodeRepository).save(entity);
        assertThat(entity.isHeadquarter()).isTrue();
        assertThat(entity.getCountry()).isEqualTo(country);
    }

    @Test
    @DisplayName("Should create branch SwiftCode with headquarter found")
    void shouldCreateBranchSwiftCodeWithHeadquarter() {
        String branchSwiftCode = "TESTPLP123";
        String expectedHQSwiftCode = branchSwiftCode.substring(0, 8) + "XXX";

        SwiftCodeCreateDTO dto = SwiftCodeCreateDTO.builder()
                .swiftCode(branchSwiftCode)
                .bankName("Branch Bank")
                .address("Branch Address")
                .countryISO2(COUNTRY_ISO)
                .countryName(COUNTRY_NAME)
                .isHeadquarter(false)
                .build();

        SwiftCode entity = SwiftCode.builder().swiftCode(dto.getSwiftCode()).build();

        when(swiftCodeRepository.existsBySwiftCode(branchSwiftCode)).thenReturn(false);
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeMapper.toEntity(dto)).thenReturn(entity);
        when(swiftCodeRepository.findBySwiftCode(expectedHQSwiftCode)).thenReturn(Optional.of(hqSwiftCode));

        swiftCodeService.createSwiftCode(dto);

        verify(swiftCodeRepository).save(entity);
        assertThat(entity.isHeadquarter()).isFalse();
        assertThat(entity.getHeadquarterEntity()).isEqualTo(hqSwiftCode);
    }

    @Test
    @DisplayName("Should throw EntityNotFound when headquarter for branch not found")
    void shouldThrowWhenBranchHeadquarterNotFound() {
        SwiftCodeCreateDTO dto = SwiftCodeCreateDTO.builder()
                .swiftCode(SWIFT_CODE_BRANCH)
                .bankName("Branch Bank")
                .address("Branch Address")
                .countryISO2(COUNTRY_ISO)
                .countryName(COUNTRY_NAME)
                .isHeadquarter(false)
                .build();

        SwiftCode entity = SwiftCode.builder().swiftCode(dto.getSwiftCode()).build();

        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE_BRANCH)).thenReturn(false);
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(swiftCodeMapper.toEntity(dto)).thenReturn(entity);
        when(swiftCodeRepository.findBySwiftCode("TESTPLPXXX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Headquarter not found for branch");

        verify(swiftCodeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgument when SwiftCode already exists")
    void shouldThrowWhenSwiftCodeAlreadyExists() {
        when(swiftCodeRepository.existsBySwiftCode(SWIFT_CODE_HQ)).thenReturn(true);

        SwiftCodeCreateDTO dto = SwiftCodeCreateDTO.builder()
                .swiftCode(SWIFT_CODE_HQ)
                .isHeadquarter(true)
                .countryISO2(COUNTRY_ISO)
                .countryName(COUNTRY_NAME)
                .build();

        assertThatThrownBy(() -> swiftCodeService.createSwiftCode(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SwiftCode already exists");
    }

    @Test
    @DisplayName("Should delete SwiftCode when exists")
    void shouldDeleteSwiftCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE_HQ)).thenReturn(Optional.of(hqSwiftCode));

        swiftCodeService.deleteSwiftCode(SWIFT_CODE_HQ);

        verify(swiftCodeRepository).delete(hqSwiftCode);
    }

    @Test
    @DisplayName("Should throw EntityNotFound when deleting non-existing SwiftCode")
    void shouldThrowWhenDeletingNonExistingSwiftCode() {
        when(swiftCodeRepository.findBySwiftCode(SWIFT_CODE_HQ)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> swiftCodeService.deleteSwiftCode(SWIFT_CODE_HQ))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("SwiftCode not found");

        verify(swiftCodeRepository, never()).delete(any());
    }
}