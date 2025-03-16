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
public class SwiftCodeRepositoryTest {
    @Autowired
    private SwiftCodeRepository swiftCodeRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country createAndSaveCountry(String name, String isoCode) {
        Country country = Country.builder()
                .name(name)
                .isoCode(isoCode)
                .build();
        return countryRepository.save(country);
    }

    @Test
    @DisplayName("Should create and retrieve SwiftCode by ID")
    void shouldCreateAndRetrieveSwiftCodeById() {
        Country poland = createAndSaveCountry("Poland", "PL");

        SwiftCode swift = SwiftCode.builder()
                .swiftCode("POLAPLPR")
                .bankName("Polish Bank")
                .branchName("Main Branch")
                .country(poland)
                .build();

        SwiftCode saved = swiftCodeRepository.save(swift);
        Optional<SwiftCode> found = swiftCodeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getSwiftCode()).isEqualTo("POLAPLPR");
        assertThat(found.get().getBankName()).isEqualTo("Polish Bank");
    }

    @Test
    @DisplayName("Should find SwiftCode by swiftCode")
    void shouldFindBySwiftCode() {
        Country germany = createAndSaveCountry("Germany", "DE");

        SwiftCode swift = SwiftCode.builder()
                .swiftCode("DEUTDEFF")
                .bankName("Deutsche Bank")
                .country(germany)
                .build();

        swiftCodeRepository.save(swift);

        Optional<SwiftCode> found = swiftCodeRepository.findBySwiftCode("DEUTDEFF");

        assertThat(found).isPresent();
        assertThat(found.get().getBankName()).isEqualTo("Deutsche Bank");
    }

    @Test
    @DisplayName("Should return true if SwiftCode exists")
    void shouldCheckExistenceBySwiftCode() {
        Country france = createAndSaveCountry("France", "FR");

        SwiftCode swift = SwiftCode.builder()
                .swiftCode("BNPAFRPP")
                .bankName("BNP Paribas")
                .country(france)
                .build();

        swiftCodeRepository.save(swift);

        boolean exists = swiftCodeRepository.existsBySwiftCode("BNPAFRPP");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find all SwiftCodes by country ID")
    void shouldFindAllByCountryId() {
        Country spain = createAndSaveCountry("Spain", "ES");

        SwiftCode swift1 = SwiftCode.builder()
                .swiftCode("BBVAESMM")
                .bankName("BBVA")
                .country(spain)
                .build();

        SwiftCode swift2 = SwiftCode.builder()
                .swiftCode("CAIXESBB")
                .bankName("CaixaBank")
                .country(spain)
                .build();

        swiftCodeRepository.saveAll(List.of(swift1, swift2));

        List<SwiftCode> results = swiftCodeRepository.findAllByCountry_Id(spain.getId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(SwiftCode::getSwiftCode).containsExactlyInAnyOrder("BBVAESMM", "CAIXESBB");
    }

    @Test
    @DisplayName("Should update an existing SwiftCode")
    void shouldUpdateSwiftCode() {
        Country italy = createAndSaveCountry("Italy", "IT");

        SwiftCode swift = SwiftCode.builder()
                .swiftCode("BCITITMM")
                .bankName("Intesa Sanpaolo")
                .country(italy)
                .build();

        SwiftCode saved = swiftCodeRepository.save(swift);

        // Update bank name
        saved.setBankName("Intesa Updated");
        SwiftCode updated = swiftCodeRepository.save(saved);

        assertThat(updated.getBankName()).isEqualTo("Intesa Updated");
    }

    @Test
    @DisplayName("Should delete SwiftCode")
    void shouldDeleteSwiftCode() {
        Country netherlands = createAndSaveCountry("Netherlands", "NL");

        SwiftCode swift = SwiftCode.builder()
                .swiftCode("INGBNL2A")
                .bankName("ING Bank")
                .country(netherlands)
                .build();

        SwiftCode saved = swiftCodeRepository.save(swift);

        swiftCodeRepository.delete(saved);

        Optional<SwiftCode> found = swiftCodeRepository.findById(saved.getId());

        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Should find all SwiftCodes")
    void shouldFindAllSwiftCodes() {
        Country belgium = createAndSaveCountry("Belgium", "BE");

        SwiftCode swift1 = SwiftCode.builder()
                .swiftCode("KREDBEBB")
                .bankName("KBC Bank")
                .country(belgium)
                .build();

        SwiftCode swift2 = SwiftCode.builder()
                .swiftCode("BNAGBEBB")
                .bankName("BNP Paribas Fortis")
                .country(belgium)
                .build();

        swiftCodeRepository.saveAll(List.of(swift1, swift2));

        List<SwiftCode> results = swiftCodeRepository.findAll();

        assertThat(results).hasSize(2);
        assertThat(results).extracting(SwiftCode::getSwiftCode).containsExactlyInAnyOrder("KREDBEBB", "BNAGBEBB");
    }
}
