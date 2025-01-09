package com.example.jwt.dto;

import java.util.List;

public class AnalyticsSummaryDTO {
    private Long totalOrders;
    private Double totalRevenue;
    private Long totalUsers;
    private List<OrderStatusCount> ordersByStatus;
    private List<RecentOrder> recentOrders;
//    private List<ProductCategoryDistribution> productCategoryDistribution;

    // Constructors, Getters, and Setters
    public AnalyticsSummaryDTO(Long totalOrders, Double totalRevenue, Long totalUsers,
                               List<OrderStatusCount> ordersByStatus,
                               List<RecentOrder> recentOrders) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalUsers = totalUsers;
        this.ordersByStatus = ordersByStatus;
        this.recentOrders = recentOrders;
//        this.productCategoryDistribution = productCategoryDistribution;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public List<OrderStatusCount> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void setOrdersByStatus(List<OrderStatusCount> ordersByStatus) {
        this.ordersByStatus = ordersByStatus;
    }

    public List<RecentOrder> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<RecentOrder> recentOrders) {
        this.recentOrders = recentOrders;
    }

//    public List<ProductCategoryDistribution> getProductCategoryDistribution() {
//        return productCategoryDistribution;
//    }
//
//    public void setProductCategoryDistribution(List<ProductCategoryDistribution> productCategoryDistribution) {
//        this.productCategoryDistribution = productCategoryDistribution;
//    }

    // Nested DTO classes
    public static class OrderStatusCount {
        private String status;
        private long count;

        public OrderStatusCount(String status, long count) {
            this.status = status;
            this.count = count;
        }

        // Add getters
        public String getStatus() {
            return status;
        }

        public long getCount() {
            return count;
        }
    }


    public static class RecentOrder {
        private Long orderId;
        private String customerName;
        private String status;
        private Double totalAmount;

        public RecentOrder(Long orderId, String customerName, String status, Double totalAmount) {
            this.orderId = orderId;
            this.customerName = customerName;
            this.status = status;
            this.totalAmount = totalAmount;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }
    }

    public static class ProductCategoryDistribution {
        private String category;
        private Long count;

        public ProductCategoryDistribution(String category, Long count) {
            this.category = category;
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
