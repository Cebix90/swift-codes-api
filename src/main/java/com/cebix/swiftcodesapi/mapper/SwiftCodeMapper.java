package com.cebix.swiftcodesapi.mapper;

import com.cebix.swiftcodesapi.dto.SwiftCodeCreateDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeDTO;
import com.cebix.swiftcodesapi.dto.SwiftCodeSimpleDTO;
import com.cebix.swiftcodesapi.entity.SwiftCode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SwiftCodeMapper {

    @Mapping(source = "country.isoCode", target = "countryISO2")
    @Mapping(source = "country.name", target = "countryName")
    @Mapping(source = "headquarter", target = "isHeadquarter")
    SwiftCodeDTO toDTO(SwiftCode entity);

    SwiftCode toEntity(SwiftCodeCreateDTO dto);

    @Mapping(source = "country.isoCode", target = "countryISO2")
    @Mapping(source = "headquarter", target = "isHeadquarter")
    SwiftCodeSimpleDTO toSimpleDTO(SwiftCode swiftCode);
}
