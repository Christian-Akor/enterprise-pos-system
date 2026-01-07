package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {
    
    @Column(unique = true, nullable = false)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private String resource; // e.g., "SALES", "PRODUCTS", "USERS"
    
    @Column(nullable = false)
    private String action; // e.g., "CREATE", "READ", "UPDATE", "DELETE"
    
    public String getFullPermission() {
        return resource + ":" + action;
    }
}