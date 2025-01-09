package com.example.jwt.service;

import com.example.jwt.dto.AnalyticsSummaryDTO;
import com.example.jwt.model.Order;
import com.example.jwt.model.User;
import com.example.jwt.repository.AnalyticsRepository;
import com.example.jwt.repository.OrderRepository;
import com.example.jwt.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final UserRepository userRepository;

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository, UserRepository userRepository) {
        this.analyticsRepository = analyticsRepository;
        this.userRepository = userRepository;
    }

    public AnalyticsSummaryDTO getAnalyticsSummary() {
        List<Order> orders = analyticsRepository.findAllOrders();

        // Total orders
        long totalOrders = orders.size();

        // Total revenue
        double totalRevenue = orders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        List<User> allUsers = userRepository.findAll();
        long totalUsers = allUsers.stream()
                .filter(user -> "USER".equals(user.getRole().toString()))
                .count();

        // Orders by status
        List<AnalyticsSummaryDTO.OrderStatusCount> ordersByStatus = orders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> new AnalyticsSummaryDTO.OrderStatusCount(entry.getKey().name(), entry.getValue()))
                .collect(Collectors.toList());

        // Recent orders (limit 5)
        List<AnalyticsSummaryDTO.RecentOrder> recentOrders = analyticsRepository.findTop5RecentOrders()
                .stream()
                .map(order -> new AnalyticsSummaryDTO.RecentOrder(
                        order.getId(),
                        order.getUserEmail(),
                        order.getStatus().name(),
                        order.getTotalAmount()
                ))
                .collect(Collectors.toList());

//        // Product category distribution (assume categories are derived from orders)
//        List<AnalyticsSummaryDTO.ProductCategoryDistribution> productCategoryDistribution = orders.stream()
//                .flatMap(order -> order.getOrderItems().stream()) // Assuming each order has `OrderItems`
//                .collect(Collectors.groupingBy(orderItem -> orderItem.getProduct().getCategory(), Collectors.counting()))
//                .entrySet()
//                .stream()
//                .map(entry -> new AnalyticsSummaryDTO.ProductCategoryDistribution(entry.getKey(), entry.getValue()))
//                .collect(Collectors.toList());

        return new AnalyticsSummaryDTO(
                totalOrders,
                totalRevenue,
                totalUsers,
                ordersByStatus,
                recentOrders
//                productCategoryDistribution
        );
    }
}
