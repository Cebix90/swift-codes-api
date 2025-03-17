package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.dto.CountryCreateDTO;
import com.cebix.swiftcodesapi.dto.CountryDTO;
import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.mapper.CountryMapper;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.service.impl.CountryServiceImpl;
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

class CountryServiceImplTest {

    private static final String COUNTRY_NAME = "Poland";
    private static final String COUNTRY_NAME_UPDATED = "Polska";
    private static final String COUNTRY_ISO = "PL";

    private AutoCloseable closeable;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country country;
    private CountryDTO countryDTO;
    private CountryCreateDTO countryCreateDTO;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        country = Country.builder()
                .id(1L)
                .name(COUNTRY_NAME)
                .isoCode(COUNTRY_ISO)
                .build();

        countryDTO = new CountryDTO();
        countryDTO.setName(COUNTRY_NAME);
        countryDTO.setIsoCode(COUNTRY_ISO);

        countryCreateDTO = new CountryCreateDTO();
        countryCreateDTO.setName(COUNTRY_NAME);
        countryCreateDTO.setIsoCode(COUNTRY_ISO);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    @DisplayName("Should return all countries")
    void shouldReturnAllCountries() {
        Country germany = Country.builder().id(2L).name("Germany").isoCode("DE").build();

        CountryDTO germanyDTO = new CountryDTO();
        germanyDTO.setName("Germany");
        germanyDTO.setIsoCode("DE");

        when(countryRepository.findAll()).thenReturn(List.of(country, germany));
        when(countryMapper.toDTO(country)).thenReturn(countryDTO);
        when(countryMapper.toDTO(germany)).thenReturn(germanyDTO);

        List<CountryDTO> result = countryService.getAllCountries();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CountryDTO::getIsoCode).containsExactlyInAnyOrder("PL", "DE");

        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("Should return country by ISO code")
    void shouldReturnCountryByIsoCode() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(countryMapper.toDTO(country)).thenReturn(countryDTO);

        CountryDTO result = countryService.getCountryByIsoCode(COUNTRY_ISO);

        assertThat(result).isNotNull();
        assertThat(result.getIsoCode()).isEqualTo(COUNTRY_ISO);

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should throw exception when country not found by ISO code")
    void shouldThrowExceptionWhenCountryNotFoundByIsoCode() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryByIsoCode(COUNTRY_ISO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should create new country when ISO code is unique")
    void shouldCreateNewCountry() {
        when(countryRepository.existsByIsoCode(COUNTRY_ISO)).thenReturn(false);
        when(countryMapper.toEntity(countryCreateDTO)).thenReturn(country);
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDTO(country)).thenReturn(countryDTO);

        CountryDTO result = countryService.createCountry(countryCreateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getIsoCode()).isEqualTo(COUNTRY_ISO);

        verify(countryRepository).save(country);
    }

    @Test
    @DisplayName("Should throw exception when creating country with existing ISO code")
    void shouldThrowExceptionWhenIsoCodeAlreadyExists() {
        when(countryRepository.existsByIsoCode(COUNTRY_ISO)).thenReturn(true);

        assertThatThrownBy(() -> countryService.createCountry(countryCreateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Country with ISO code already exists");

        verify(countryRepository).existsByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should update existing country")
    void shouldUpdateCountry() {
        // Przygotowanie danych wejściowych do update
        CountryCreateDTO updateDTO = new CountryCreateDTO();
        updateDTO.setName(COUNTRY_NAME_UPDATED);
        updateDTO.setIsoCode(COUNTRY_ISO);

        // Przygotowanie danych zwracanych przez mocki
        CountryDTO updatedCountryDTO = new CountryDTO();
        updatedCountryDTO.setName(COUNTRY_NAME_UPDATED);
        updatedCountryDTO.setIsoCode(COUNTRY_ISO);

        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));
        when(countryRepository.save(any(Country.class))).thenAnswer(inv -> inv.getArgument(0));
        when(countryMapper.toDTO(any(Country.class))).thenReturn(updatedCountryDTO);

        CountryDTO result = countryService.updateCountry(COUNTRY_ISO, updateDTO);

        assertThat(result.getName()).isEqualTo(COUNTRY_NAME_UPDATED);

        verify(countryRepository).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing country")
    void shouldThrowExceptionWhenUpdatingNonExistingCountry() {
        CountryCreateDTO updateDTO = new CountryCreateDTO();
        updateDTO.setName(COUNTRY_NAME_UPDATED);
        updateDTO.setIsoCode(COUNTRY_ISO);

        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry(COUNTRY_ISO, updateDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }

    @Test
    @DisplayName("Should delete country when exists")
    void shouldDeleteCountry() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.of(country));

        countryService.deleteCountry(COUNTRY_ISO);

        verify(countryRepository).delete(country);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing country")
    void shouldThrowExceptionWhenDeletingNonExistingCountry() {
        when(countryRepository.findByIsoCode(COUNTRY_ISO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.deleteCountry(COUNTRY_ISO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with ISO2");

        verify(countryRepository).findByIsoCode(COUNTRY_ISO);
    }
}
