package com.celfocus.hiring.kickstarter.db.entity;

import com.celfocus.hiring.kickstarter.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
public class ProductEntity extends Product implements Serializable {


    public String getName() {
        return super.getName();
    }

    public void setName(String name) {
        super.setName(name);
    }

    @Id
    public String getSku() {
        return super.getSku();
    }

    public void setSku(String sku) {
        super.setSku(sku);
    }

    @Column(length = 1000)
    public String getDescription() {
        return super.getDescription();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    public BigDecimal getPrice() {
        return super.getPrice();
    }

    public void setPrice(BigDecimal price) {
        super.setPrice(price);
    }

    public String getimageUrl() {
        return super.getimageUrl();
    }

    public void setimageUrl(String imageUrl) {
        super.setimageUrl(imageUrl);
    }
}

