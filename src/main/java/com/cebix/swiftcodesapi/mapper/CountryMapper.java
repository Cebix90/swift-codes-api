package com.cebix.swiftcodesapi.mapper;

import com.cebix.swiftcodesapi.dto.CountryDTO;
import com.cebix.swiftcodesapi.dto.CountryCreateDTO;
import com.cebix.swiftcodesapi.entity.Country;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDTO(Country country);

    Country toEntity(CountryCreateDTO dto);
}