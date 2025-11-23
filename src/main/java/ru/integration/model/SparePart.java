package ru.integration.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Модель для хранения исторических данных о запчастях
 */
@Entity
@Table(name = "spare_parts", uniqueConstraints = @UniqueConstraint(columnNames = "spare_code"))
public class SparePart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "spare_code", nullable = false, unique = true, length = 100)
    private String spareCode;
    
    @Column(name = "spare_name", nullable = false, length = 255)
    private String spareName;
    
    @Column(name = "spare_description", length = 1000)
    private String spareDescription;
    
    @Column(name = "spare_type", nullable = false, length = 50)
    private String spareType;
    
    @Column(name = "spare_status", nullable = false, length = 50)
    private String spareStatus;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;
    
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastSyncedAt = LocalDateTime.now();
    }
    
    // Конструкторы
    public SparePart() {
    }
    
    public SparePart(String spareCode, String spareName, String spareDescription,
                    String spareType, String spareStatus, BigDecimal price,
                    Integer quantity, LocalDate updatedAt) {
        this.spareCode = spareCode;
        this.spareName = spareName;
        this.spareDescription = spareDescription;
        this.spareType = spareType;
        this.spareStatus = spareStatus;
        this.price = price;
        this.quantity = quantity;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSpareCode() {
        return spareCode;
    }
    
    public void setSpareCode(String spareCode) {
        this.spareCode = spareCode;
    }
    
    public String getSpareName() {
        return spareName;
    }
    
    public void setSpareName(String spareName) {
        this.spareName = spareName;
    }
    
    public String getSpareDescription() {
        return spareDescription;
    }
    
    public void setSpareDescription(String spareDescription) {
        this.spareDescription = spareDescription;
    }
    
    public String getSpareType() {
        return spareType;
    }
    
    public void setSpareType(String spareType) {
        this.spareType = spareType;
    }
    
    public String getSpareStatus() {
        return spareStatus;
    }
    
    public void setSpareStatus(String spareStatus) {
        this.spareStatus = spareStatus;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LocalDate getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }
    
    public void setLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }
    
    @Override
    public String toString() {
        return "SparePart{" +
                "id=" + id +
                ", spareCode='" + spareCode + '\'' +
                ", spareName='" + spareName + '\'' +
                ", spareType='" + spareType + '\'' +
                ", spareStatus='" + spareStatus + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}

