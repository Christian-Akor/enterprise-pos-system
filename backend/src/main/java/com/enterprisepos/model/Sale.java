package com.enterprisepos.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends BaseEntity {
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(unique = true, nullable = false)
    private String saleNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", nullable = false)
    private User cashier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount;
    
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal changeAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SaleStatus status = SaleStatus.COMPLETED;
    
    @Column(nullable = false)
    private LocalDateTime saleDate;
    
    private String notes;
    
    private String receiptUrl;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean receiptPrinted = false;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean receiptEmailed = false;
    
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, MOBILE_PAYMENT, BANK_TRANSFER, OTHER
    }
    
    public enum SaleStatus {
        PENDING, COMPLETED, REFUNDED, PARTIALLY_REFUNDED, CANCELLED
    }
    
    public void calculateTotals() {
        this.subtotal = items.stream()
            .map(SaleItem::getLineTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.taxAmount = items.stream()
            .map(SaleItem::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.totalAmount = subtotal.add(taxAmount).subtract(discountAmount);
        this.changeAmount = paidAmount.subtract(totalAmount);
    }
}