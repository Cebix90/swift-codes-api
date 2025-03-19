package com.cebix.swiftcodesapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Country name cannot be blank")
    @Size(max = 100, message = "Country name can't be longer than 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "ISO code cannot be blank")
    @Size(min = 2, max = 2, message = "ISO code must be exactly 2 characters")
    @Column(nullable = false, unique = true, length = 2)
    private String isoCode;

    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwiftCode> swiftCodes;
}
