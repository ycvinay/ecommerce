package com.example.jwt.repository;

import com.example.jwt.dto.AnalyticsSummaryDTO;
import com.example.jwt.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserEmail(String email);

    long count();

    // Sum total revenue (assuming Order has a totalAmount field)
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    double sumTotalRevenue();

    // Count orders by status
//    @Query("SELECT new com.example.jwt.dto.AnalyticsDTO$OrderStatusCount(o.status.name, COUNT(o)) FROM Order o GROUP BY o.status")
//    List<AnalyticsDTO.OrderStatusCount> countOrdersByStatus();
    // Find recent orders (limit 5)
//    @Query("SELECT new com.example.jwt.dto.AnalyticsDTO$RecentOrder(o.id, o.userEmail, o.status, o.totalAmount) FROM Order o ORDER BY o.createdAt DESC")
//    List<AnalyticsDTO.RecentOrder> findRecentOrders();


}
