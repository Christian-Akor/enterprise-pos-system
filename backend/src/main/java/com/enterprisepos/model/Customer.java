package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(unique = true)
    private String customerNumber;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true)
    private String email;
    
    private String phone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private String postalCode;
    
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal loyaltyPoints = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer totalPurchases = 0;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CustomerTier tier = CustomerTier.BRONZE;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    private String notes;
    
    public enum CustomerTier {
        BRONZE, SILVER, GOLD, PLATINUM
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
}