package ru.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO для данных запчасти из CMS API
 */
public class SparePartDto {
    
    @JsonProperty("spareCode")
    private String spareCode;
    
    @JsonProperty("spareName")
    private String spareName;
    
    @JsonProperty("spareDescription")
    private String spareDescription;
    
    @JsonProperty("spareType")
    private String spareType;
    
    @JsonProperty("spareStatus")
    private String spareStatus;
    
    @JsonProperty("price")
    private String price;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    // Getters and Setters
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
    
    public String getPrice() {
        return price;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

