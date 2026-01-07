package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String businessName;
    
    @Column(unique = true, nullable = false)
    private String businessEmail;
    
    private String businessPhone;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private String postalCode;
    
    private String taxId;
    
    private String currency;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPlan subscriptionPlan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TenantStatus status;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    public enum SubscriptionPlan {
        FREE, BASIC, PROFESSIONAL, ENTERPRISE
    }
    
    public enum TenantStatus {
        TRIAL, ACTIVE, SUSPENDED, CANCELLED
    }
}