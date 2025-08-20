package com.pos.phukrit.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "products") // Assuming a table named 'products'
public class ProductModel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Assuming an ID field
    @Column(nullable = false)
    private String name; // Product name
    private String description; // Product description
    @Column(nullable = false)
    private double price; // Product price
    @Column(nullable = false)
    private int stock; // Stock quantity
}