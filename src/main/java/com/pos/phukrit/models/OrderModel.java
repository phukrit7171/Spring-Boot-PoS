package com.pos.phukrit.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- NEW RELATIONSHIP 1 ---
    // The employee who created the order. This is a required field.
    // For self-checkout, this could be null or linked to a generic "System" user.
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user; // This is the EMPLOYEE

    // --- NEW RELATIONSHIP 2 ---
    // The customer associated with the order. This is optional.
    // 'nullable = true' allows for guest checkouts.
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private CustomerModel customer; // This is the CUSTOMER

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemModel> items;

    public enum OrderStatus {
        PENDING,
        COMPLETED,
        CANCELLED
    }
}