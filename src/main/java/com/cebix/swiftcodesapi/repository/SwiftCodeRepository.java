package com.cebix.swiftcodesapi.repository;

import com.cebix.swiftcodesapi.entity.SwiftCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Long> {
    Optional<SwiftCode> findBySwiftCode(String swiftCode);

    List<SwiftCode> findAllByCountry_Id(Long countryId);

    boolean existsBySwiftCode(String swiftCode);
}
