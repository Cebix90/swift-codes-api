package com.cebix.swiftcodesapi.mapper;

import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeSimpleDTO;
import com.cebix.swiftcodesapi.entity.Country;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class SwiftCodeMapperTest {

    private SwiftCodeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(SwiftCodeMapper.class);
    }

    @Test
    @DisplayName("should map SwiftCode entity to SwiftCodeDTO")
    void should_MapToDTO_WhenEntityIsValid() {
        Country country = Country.builder()
                .isoCode("PL")
                .name("POLAND")
                .build();

        SwiftCode entity = SwiftCode.builder()
                .swiftCode("TESTPLPWXXX")
                .bankName("Bank Poland")
                .address("Warsaw")
                .isHeadquarter(true)
                .country(country)
                .build();

        SwiftCodeDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getSwiftCode()).isEqualTo("TESTPLPWXXX");
        assertThat(dto.getBankName()).isEqualTo("Bank Poland");
        assertThat(dto.getAddress()).isEqualTo("Warsaw");
        assertThat(dto.getIsHeadquarter()).isTrue();
        assertThat(dto.getCountryISO2()).isEqualTo("PL");
        assertThat(dto.getCountryName()).isEqualTo("POLAND");
    }

    @Test
    @DisplayName("should map SwiftCodeCreateDTO to SwiftCode entity")
    void should_MapToEntity_WhenDTOIsValid() {
        SwiftCodeCreateDTO dto = SwiftCodeCreateDTO.builder()
                .swiftCode("TESTPLPWXXX")
                .bankName("Bank Poland")
                .address("Warsaw")
                .countryISO2("PL")
                .countryName("POLAND")
                .isHeadquarter(true)
                .build();

        SwiftCode entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getSwiftCode()).isEqualTo("TESTPLPWXXX");
        assertThat(entity.getBankName()).isEqualTo("Bank Poland");
        assertThat(entity.getAddress()).isEqualTo("Warsaw");
        assertThat(entity.isHeadquarter()).isTrue();
        assertThat(entity.getCountry()).isNull();
        assertThat(entity.getHeadquarterEntity()).isNull();
    }

    @Test
    @DisplayName("should map SwiftCode entity to SwiftCodeSimpleDTO")
    void should_MapToSimpleDTO_WhenEntityIsValid() {
        Country country = Country.builder()
                .isoCode("PL")
                .name("POLAND")
                .build();

        SwiftCode entity = SwiftCode.builder()
                .swiftCode("TESTPLPWXXX")
                .bankName("Bank Poland")
                .address("Warsaw")
                .isHeadquarter(false)
                .country(country)
                .build();

        SwiftCodeSimpleDTO dto = mapper.toSimpleDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getSwiftCode()).isEqualTo("TESTPLPWXXX");
        assertThat(dto.getBankName()).isEqualTo("Bank Poland");
        assertThat(dto.getAddress()).isEqualTo("Warsaw");
        assertThat(dto.getIsHeadquarter()).isFalse();
        assertThat(dto.getCountryISO2()).isEqualTo("PL");
    }

    @Test
    @DisplayName("should return null when mapping null SwiftCode to DTO")
    void should_ReturnNull_WhenMappingNullToDTO() {
        SwiftCodeDTO dto = mapper.toDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("should return null when mapping null SwiftCode to SimpleDTO")
    void should_ReturnNull_WhenMappingNullToSimpleDTO() {
        SwiftCodeSimpleDTO dto = mapper.toSimpleDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("should return null when mapping null DTO to entity")
    void should_ReturnNull_WhenMappingNullToEntity() {
        SwiftCode entity = mapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("should map toDTO with null Country")
    void should_MapToDTO_WhenCountryIsNull() {
        SwiftCode entity = SwiftCode.builder()
                .swiftCode("NULL1")
                .bankName("Null Bank")
                .address("Null City")
                .isHeadquarter(false)
                .country(null)
                .build();

        SwiftCodeDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCountryISO2()).isNull();
        assertThat(dto.getCountryName()).isNull();
    }

    @Test
    @DisplayName("should map toDTO when Country.isoCode is null")
    void should_MapToDTO_WhenCountryIsoCodeIsNull() {
        Country country = Country.builder()
                .isoCode(null)
                .name("POLAND")
                .build();

        SwiftCode entity = SwiftCode.builder()
                .swiftCode("NULL2")
                .bankName("Bank")
                .address("City")
                .isHeadquarter(true)
                .country(country)
                .build();

        SwiftCodeDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCountryISO2()).isNull();
        assertThat(dto.getCountryName()).isEqualTo("POLAND");
    }

    @Test
    @DisplayName("should map toDTO when Country.name is null")
    void should_MapToDTO_WhenCountryNameIsNull() {
        Country country = Country.builder()
                .isoCode("PL")
                .name(null)
                .build();

        SwiftCode entity = SwiftCode.builder()
                .swiftCode("NULL3")
                .bankName("Bank")
                .address("City")
                .isHeadquarter(true)
                .country(country)
                .build();

        SwiftCodeDTO dto = mapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getCountryISO2()).isEqualTo("PL");
        assertThat(dto.getCountryName()).isNull();
    }
}