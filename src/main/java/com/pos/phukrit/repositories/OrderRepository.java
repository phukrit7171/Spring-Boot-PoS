package com.pos.phukrit.repositories;

import com.pos.phukrit.models.OrderModel;
import com.pos.phukrit.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    // Find all orders placed by a specific user
    List<OrderModel> findByUser(UserModel user);

    // Find all orders with a specific status
    List<OrderModel> findByStatus(OrderModel.OrderStatus status);
}