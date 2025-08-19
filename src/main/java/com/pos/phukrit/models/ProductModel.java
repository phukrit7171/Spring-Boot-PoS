package com.pos.phukrit.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "products") // Assuming a table named 'products'
public class ProductModel{
    private Long id; // Assuming an ID field
    private String name; // Product name
    private String description; // Product description
    private double price; // Product price
    private int stock; // Stock quantity
}