package com.tiemtaphoa.warehouseservice.model;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class BranchInventory {
    private String branchId; 
    private int quantity;
    private double sellingPrice;

    public String getBranchId() {
        return branchId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }
}
