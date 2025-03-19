package com.cebix.swiftcodesapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "swift_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwiftCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SWIFT code cannot be blank")
    @Size(min = 8, max = 11, message = "SWIFT code must be between 8 and 11 characters")
    @Column(name = "swift_code", nullable = false, unique = true, length = 11)
    private String swiftCode;

    @NotBlank(message = "Bank name cannot be blank")
    @Size(max = 150, message = "Bank name can't be longer than 150 characters")
    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Size(max = 150, message = "Branch name can't be longer than 150 characters")
    @Column(name = "branch_name", length = 150)
    private String branchName;

    @Size(max = 255, message = "Address can't be longer than 255 characters")
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "is_headquarter", nullable = false)
    private boolean isHeadquarter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "headquarter_id")
    private SwiftCode headquarterEntity;
}
