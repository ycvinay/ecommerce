package com.example.jwt.controller;

import com.example.jwt.dto.RazorpayOrderResponse;
import com.example.jwt.model.Order;
import com.example.jwt.model.OrderStatus;
import com.example.jwt.model.Role;
import com.example.jwt.repository.CartRepository;
import com.example.jwt.repository.ProductRepository;
import com.example.jwt.service.OrderService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;


    @PostMapping("/place-order")
    public ResponseEntity<Order> placeOrder(@RequestHeader("Authorization") String token){
        String userEmail = orderService.extractEmailFromToken(token);
        Order order = orderService.placeOrder(userEmail);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/place-order/{productId}")
    public ResponseEntity<?> placeSingleItem(@PathVariable Long productId, @RequestHeader("Authorization") String token) {
        try {
            String userEmail = orderService.extractEmailFromToken(token);
            RazorpayOrderResponse order = orderService.placeSingleItem(userEmail, productId);
            return ResponseEntity.ok(order);
        } catch (IllegalStateException | RazorpayException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getUserOrders(@RequestHeader("Authorization") String token) {
        String userEmail = orderService.extractEmailFromToken(token);
        Role userRole = orderService.extractRoleFromToken(token);
        if (userRole == Role.USER) {
            List<Order> userOrders = orderService.getUserOrders(userEmail);
            return ResponseEntity.ok(userOrders);
        }  else if (userRole == Role.ADMIN) {
            List<Order> allOrders = orderService.getAllOrders();
            return ResponseEntity.ok(allOrders);
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status, @RequestHeader("Authorization") String token) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.noContent().build();
    }

}
