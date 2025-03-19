package com.cebix.swiftcodesapi.repository;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SwiftCodeRepositoryTest {

    private static final String ADDRESS = "Main Street 1";

    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country createAndSaveCountry(String name, String isoCode) {
        return countryRepository.save(
                Country.builder()
                        .name(name)
                        .isoCode(isoCode)
                        .build()
        );
    }

    private SwiftCode createSwiftCode(String swiftCode, String bankName, Country country, boolean isHeadquarter) {
        return SwiftCode.builder()
                .swiftCode(swiftCode)
                .bankName(bankName)
                .address(ADDRESS)
                .isHeadquarter(isHeadquarter)
                .country(country)
                .build();
    }

    @Test
    @DisplayName("Should create and retrieve SwiftCode by ID")
    void shouldCreateAndRetrieveSwiftCodeById() {
        Country country = createAndSaveCountry("Poland", "PL");
        SwiftCode swiftCode = createSwiftCode("POLAPLPR", "Polish Bank", country, true);

        SwiftCode saved = swiftCodeRepository.save(swiftCode);
        Optional<SwiftCode> found = swiftCodeRepository.findById(saved.getId());

        assertThat(found)
                .isPresent()
                .get()
                .satisfies(sc -> {
                    assertThat(sc.getSwiftCode()).isEqualTo("POLAPLPR");
                    assertThat(sc.getBankName()).isEqualTo("Polish Bank");
                });
    }

    @Test
    @DisplayName("Should find SwiftCode by swiftCode")
    void shouldFindBySwiftCode() {
        Country country = createAndSaveCountry("Germany", "DE");
        SwiftCode swiftCode = createSwiftCode("DEUTDEFF", "Deutsche Bank", country, true);

        swiftCodeRepository.save(swiftCode);

        Optional<SwiftCode> found = swiftCodeRepository.findBySwiftCode("DEUTDEFF");

        assertThat(found)
                .isPresent()
                .get()
                .satisfies(sc -> {
                    assertThat(sc.getBankName()).isEqualTo("Deutsche Bank");
                    assertThat(sc.getSwiftCode()).isEqualTo("DEUTDEFF");
                });
    }

    @Test
    @DisplayName("Should return true if SwiftCode exists")
    void shouldCheckExistenceBySwiftCode() {
        Country country = createAndSaveCountry("France", "FR");
        SwiftCode swiftCode = createSwiftCode("BNPAFRPP", "BNP Paribas", country, true);

        swiftCodeRepository.save(swiftCode);

        boolean exists = swiftCodeRepository.existsBySwiftCode("BNPAFRPP");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find all SwiftCodes by country ID")
    void shouldFindAllByCountryId() {
        Country country = createAndSaveCountry("Spain", "ES");

        SwiftCode swiftCode1 = createSwiftCode("BBVAESMM", "BBVA", country, true);
        SwiftCode swiftCode2 = createSwiftCode("CAIXESBB", "CaixaBank", country, false);

        swiftCodeRepository.saveAll(List.of(swiftCode1, swiftCode2));

        List<SwiftCode> results = swiftCodeRepository.findAllByCountry_Id(country.getId());

        assertThat(results)
                .hasSize(2)
                .extracting(SwiftCode::getSwiftCode)
                .containsExactlyInAnyOrder("BBVAESMM", "CAIXESBB");
    }

    @Test
    @DisplayName("Should update an existing SwiftCode")
    void shouldUpdateSwiftCode() {
        Country country = createAndSaveCountry("Italy", "IT");
        SwiftCode swiftCode = createSwiftCode("BCITITMM", "Intesa Sanpaolo", country, true);

        SwiftCode saved = swiftCodeRepository.save(swiftCode);

        saved.setBankName("Intesa Updated");

        SwiftCode updated = swiftCodeRepository.save(saved);

        assertThat(updated.getBankName()).isEqualTo("Intesa Updated");
    }

    @Test
    @DisplayName("Should delete SwiftCode")
    void shouldDeleteSwiftCode() {
        Country country = createAndSaveCountry("Netherlands", "NL");
        SwiftCode swiftCode = createSwiftCode("INGBNL2A", "ING Bank", country, true);

        SwiftCode saved = swiftCodeRepository.save(swiftCode);

        swiftCodeRepository.delete(saved);

        Optional<SwiftCode> found = swiftCodeRepository.findById(saved.getId());

        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should find all SwiftCodes")
    void shouldFindAllSwiftCodes() {
        Country country = createAndSaveCountry("Belgium", "BE");

        SwiftCode swiftCode1 = createSwiftCode("KREDBEBB", "KBC Bank", country, true);
        SwiftCode swiftCode2 = createSwiftCode("BNAGBEBB", "BNP Paribas Fortis", country, false);

        swiftCodeRepository.saveAll(List.of(swiftCode1, swiftCode2));

        List<SwiftCode> results = swiftCodeRepository.findAll();

        assertThat(results)
                .hasSize(2)
                .extracting(SwiftCode::getSwiftCode)
                .containsExactlyInAnyOrder("KREDBEBB", "BNAGBEBB");
    }
}