package com.example.jwt.repository;

import com.example.jwt.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends CrudRepository<Order, Long> {

    @Query("SELECT o FROM Order o")
    List<Order> findAllOrders();

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findTop5RecentOrders();
}
