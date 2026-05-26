package com.example.demo.application.dto;

public class OrderItemDTO {

    private int sku;
    private int amount;

    public int getSku()             { return sku; }
    public void setSku(int sku)     { this.sku = sku; }
    public int getAmount()          { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}