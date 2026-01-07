package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false, unique = true)
    private String sku;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 2000)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @Column(nullable = false, precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = BigDecimal.ZERO;
    
    private String barcode;
    
    private String imageUrl;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 10;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StockStatus stockStatus = StockStatus.IN_STOCK;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean trackInventory = true;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    private String unit; // e.g., "piece", "kg", "liter"
    
    public enum StockStatus {
        IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    }
    
    public void updateStockStatus() {
        if (stockQuantity <= 0) {
            this.stockStatus = StockStatus.OUT_OF_STOCK;
        } else if (stockQuantity <= lowStockThreshold) {
            this.stockStatus = StockStatus.LOW_STOCK;
        } else {
            this.stockStatus = StockStatus.IN_STOCK;
        }
    }
}