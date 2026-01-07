package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends BaseEntity {
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String storeCode;
    
    private String description;
    
    @Column(nullable = false)
    private String address;
    
    private String city;
    
    private String state;
    
    private String country;
    
    private String postalCode;
    
    private String phone;
    
    private String email;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isMainStore = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    private String timezone;
}