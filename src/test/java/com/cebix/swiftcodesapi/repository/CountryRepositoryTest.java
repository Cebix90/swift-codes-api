package com.cebix.swiftcodesapi.repository;

import com.cebix.swiftcodesapi.entity.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CountryRepositoryTest {
    @Autowired
    private CountryRepository countryRepository;

    @Test
    @DisplayName("Should create and retrieve a country by ID")
    void shouldCreateAndRetrieveCountryById() {
        Country poland = Country.builder().name("Poland").isoCode("PL").build();

        Country saved = countryRepository.save(poland);
        Optional<Country> found = countryRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Poland");
    }

    @Test
    @DisplayName("Should find country by ISO code")
    void shouldFindCountryByIsoCode() {
        Country germany = Country.builder().name("Germany").isoCode("DE").build();

        countryRepository.save(germany);

        Optional<Country> found = countryRepository.findByIsoCode("DE");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Germany");
    }

    @Test
    @DisplayName("Should return true if country exists by ISO code")
    void shouldCheckIfCountryExistsByIsoCode() {
        Country france = Country.builder().name("France").isoCode("FR").build();

        countryRepository.save(france);

        boolean exists = countryRepository.existsByIsoCode("FR");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should update an existing country")
    void shouldUpdateCountry() {
        Country italy = Country.builder().name("Italy").isoCode("IT").build();

        Country saved = countryRepository.save(italy);

        saved.setName("Italia");
        Country updated = countryRepository.save(saved);

        assertThat(updated.getName()).isEqualTo("Italia");
    }

    @Test
    @DisplayName("Should delete a country by entity")
    void shouldDeleteCountry() {
        Country spain = Country.builder().name("Spain").isoCode("ES").build();

        Country saved = countryRepository.save(spain);

        countryRepository.delete(saved);

        Optional<Country> found = countryRepository.findById(saved.getId());

        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should find all countries")
    void shouldFindAllCountries() {
        Country poland = Country.builder().name("Poland").isoCode("PL").build();
        Country germany = Country.builder().name("Germany").isoCode("DE").build();
        Country france = Country.builder().name("France").isoCode("FR").build();

        countryRepository.saveAll(List.of(poland, germany, france));

        List<Country> countries = countryRepository.findAll();

        assertThat(countries).hasSize(3);
        assertThat(countries).extracting(Country::getIsoCode).containsExactlyInAnyOrder("PL", "DE", "FR");
    }
}
