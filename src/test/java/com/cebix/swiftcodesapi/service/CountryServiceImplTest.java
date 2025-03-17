package com.cebix.swiftcodesapi.service;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.service.impl.CountryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CountryServiceImplTest {
    private static final Long COUNTRY_ID = 1L;
    private static final String COUNTRY_NAME = "Poland";
    private static final String COUNTRY_NAME_UPDATED = "Polska";
    private static final String COUNTRY_ISO = "PL";
    private static final String COUNTRY_ISO_DUPLICATE = "XX";

    private AutoCloseable closeable;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country country;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        country = createCountry(COUNTRY_ID, COUNTRY_NAME, COUNTRY_ISO);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private Country createCountry(Long id, String name, String isoCode) {
        return Country.builder()
                .id(id)
                .name(name)
                .isoCode(isoCode)
                .build();
    }

    @Test
    @DisplayName("Should return all countries")
    void shouldReturnAllCountries() {
        Country germany = createCountry(2L, "Germany", "DE");

        when(countryRepository.findAll()).thenReturn(List.of(country, germany));

        List<Country> result = countryService.getAllCountries();

        assertThat(result).hasSize(2);
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("Should return country by ID")
    void shouldReturnCountryById() {
        when(countryRepository.findById(COUNTRY_ID)).thenReturn(Optional.of(country));

        Country result = countryService.getCountryById(COUNTRY_ID);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(COUNTRY_NAME);
        verify(countryRepository).findById(COUNTRY_ID);
    }

    @Test
    @DisplayName("Should throw exception when country not found by ID")
    void shouldThrowExceptionWhenCountryNotFoundById() {
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountryById(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with id: 999");
    }

    @Test
    @DisplayName("Should create new country when ISO code is unique")
    void shouldCreateNewCountry() {
        when(countryRepository.existsByIsoCode(COUNTRY_ISO)).thenReturn(false);
        when(countryRepository.save(country)).thenReturn(country);

        Country saved = countryService.createCountry(country);

        assertThat(saved).isNotNull();
        verify(countryRepository).save(country);
    }

    @Test
    @DisplayName("Should throw exception when creating country with existing ISO code")
    void shouldThrowExceptionWhenIsoCodeAlreadyExists() {
        when(countryRepository.existsByIsoCode(COUNTRY_ISO)).thenReturn(true);

        assertThatThrownBy(() -> countryService.createCountry(country))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Country with ISO code already exists");
    }

    @Test
    @DisplayName("Should update existing country")
    void shouldUpdateCountry() {
        Country updated = createCountry(null, COUNTRY_NAME_UPDATED, COUNTRY_ISO);

        when(countryRepository.findById(COUNTRY_ID)).thenReturn(Optional.of(country));
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Country result = countryService.updateCountry(COUNTRY_ID, updated);

        assertThat(result.getName()).isEqualTo(COUNTRY_NAME_UPDATED);

        ArgumentCaptor<Country> captor = ArgumentCaptor.forClass(Country.class);
        verify(countryRepository).save(captor.capture());

        Country savedCountry = captor.getValue();
        assertThat(savedCountry.getName()).isEqualTo(COUNTRY_NAME_UPDATED);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing country")
    void shouldThrowExceptionWhenUpdatingNonExistingCountry() {
        Country updated = createCountry(null, "NonExistent", COUNTRY_ISO_DUPLICATE);

        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.updateCountry(999L, updated))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with id: 999");
    }

    @Test
    @DisplayName("Should delete country when exists")
    void shouldDeleteCountry() {
        when(countryRepository.existsById(COUNTRY_ID)).thenReturn(true);

        countryService.deleteCountry(COUNTRY_ID);

        verify(countryRepository).deleteById(COUNTRY_ID);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing country")
    void shouldThrowExceptionWhenDeletingNonExistingCountry() {
        when(countryRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> countryService.deleteCountry(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Country not found with id: 999");
    }

    @Test
    @DisplayName("Should check if country exists by ISO code")
    void shouldCheckIfCountryExistsByIsoCode() {
        when(countryRepository.existsByIsoCode(COUNTRY_ISO)).thenReturn(true);

        boolean exists = countryService.existsByIsoCode(COUNTRY_ISO);

        assertThat(exists).isTrue();
        verify(countryRepository).existsByIsoCode(COUNTRY_ISO);
    }
}
