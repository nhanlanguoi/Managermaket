package com.tiemtaphoa.warehouseservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Data
@NoArgsConstructor
@Document(indexName = "warehouse_products", createIndex = true)
public class Product {

    @Id 
    private String id; 

    @Field(type = FieldType.Keyword) 
    private String productId;

    @Field(type = FieldType.Text, analyzer = "standard") 
    private String name;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;

    @Field(type = FieldType.Double)
    private double basePrice; 

    @Field(type = FieldType.Keyword)
    private String category;

    
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<BranchInventory> branchesInventory;

    public Product(String productId, String name, String description, double basePrice, String category, List<BranchInventory> branchesInventory) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.category = category;
        this.branchesInventory = branchesInventory;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getCategory() {
        return category;
    }

    public List<BranchInventory> getBranchesInventory() {
        return branchesInventory;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBranchesInventory(List<BranchInventory> branchesInventory) {
        this.branchesInventory = branchesInventory;
    }
}