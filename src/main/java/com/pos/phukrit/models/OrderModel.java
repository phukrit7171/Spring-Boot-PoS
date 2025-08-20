package com.pos.phukrit.models;

import jakarta.persistence.*;
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

    // This is the relationship you added - perfect!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Can be nullable if you allow guest checkouts
    private UserModel user;

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