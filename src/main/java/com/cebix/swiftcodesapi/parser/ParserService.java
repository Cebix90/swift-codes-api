package com.cebix.swiftcodesapi.parser;

import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import com.cebix.swiftcodesapi.repository.CountryRepository;
import com.cebix.swiftcodesapi.repository.SwiftCodeRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParserService {

    private final CountryRepository countryRepository;
    private final SwiftCodeRepository swiftCodeRepository;

    @PostConstruct
    public void init() {
        log.info("Starting CSV import/update...");
        importData();
    }

    public void importData() {
        try {
            ClassPathResource resource = new ClassPathResource("data/Interns_2025_SWIFT_CODES.csv");
            importDataFromStream(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("IO error loading CSV file", e);
        }
    }

    public void importDataFromStream(InputStreamReader reader) {
        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] fields;
            boolean isFirstLine = true;

            Set<String> importedSwiftCodes = new HashSet<>();

            while ((fields = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String countryISO2 = fields[0].trim().toUpperCase();
                String swiftCodeValue = fields[1].trim();
                String codeType = fields[2].trim();
                String bankName = fields[3].trim();
                String address = fields[4].trim();
                String townName = fields[5].trim();
                String countryName = fields[6].trim();
                String timeZone = fields[7].trim();

                if (countryISO2.length() != 2) {
                    log.warn("Invalid ISO code [{}] for country [{}]. Skipping...", countryISO2, countryName);
                    continue;
                }

                boolean isHeadquarter = swiftCodeValue.endsWith("XXX");
                importedSwiftCodes.add(swiftCodeValue);

                Country country = countryRepository.findByIsoCode(countryISO2)
                        .orElseGet(() -> {
                            Country newCountry = Country.builder()
                                    .name(countryName.toUpperCase())
                                    .isoCode(countryISO2.toUpperCase())
                                    .build();
                            return countryRepository.save(newCountry);
                        });

                SwiftCode swiftCode = swiftCodeRepository.findBySwiftCode(swiftCodeValue)
                        .orElse(null);

                if (swiftCode != null) {
                    swiftCode.setBankName(bankName);
                    swiftCode.setBranchName(townName);
                    swiftCode.setAddress(address);
                    swiftCode.setHeadquarter(isHeadquarter);
                    swiftCode.setCountry(country);

                    if (!isHeadquarter) {
                        String hqCode = swiftCodeValue.substring(0, 8) + "XXX";
                        SwiftCode hq = swiftCodeRepository.findBySwiftCode(hqCode).orElse(null);
                        swiftCode.setHeadquarterEntity(hq);
                    } else {
                        swiftCode.setHeadquarterEntity(null);
                    }

                    swiftCodeRepository.save(swiftCode);
                    log.info("Updated SwiftCode: {}", swiftCodeValue);
                } else {
                    SwiftCode newSwiftCode = SwiftCode.builder()
                            .swiftCode(swiftCodeValue)
                            .bankName(bankName)
                            .branchName(townName)
                            .address(address)
                            .isHeadquarter(isHeadquarter)
                            .country(country)
                            .build();

                    if (!isHeadquarter) {
                        String hqCode = swiftCodeValue.substring(0, 8) + "XXX";
                        SwiftCode hq = swiftCodeRepository.findBySwiftCode(hqCode).orElse(null);
                        newSwiftCode.setHeadquarterEntity(hq);
                    }

                    swiftCodeRepository.save(newSwiftCode);
                    log.info("Inserted new SwiftCode: {}", swiftCodeValue);
                }
            }

            log.info("CSV import/update completed!");

        } catch (CsvValidationException e) {
            log.error("CSV parsing error", e);
        } catch (IOException e) {
            log.error("IO error during import", e);
        }
    }
}