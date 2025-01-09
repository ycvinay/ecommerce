package com.example.jwt.service;

import com.example.jwt.dto.RazorpayOrderResponse;
import com.example.jwt.model.*;
import com.example.jwt.repository.CartRepository;
import com.example.jwt.repository.OrderItemRepository;
import com.example.jwt.repository.OrderRepository;
import com.example.jwt.repository.ProductRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {


    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public Order placeOrder(String userEmail) {
        List<Cart> cartItems = cartRepository.findByUserEmail(userEmail);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot place an order.");
        }

        for (Cart cartItem: cartItems) {
            Product product = cartItem.getProduct();
            int cartQuantity = cartItem.getQuantity();

            if (cartQuantity > product.getStockQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
            }
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(cart -> cart.getProduct().getPrice() * cart.getQuantity())
                .sum();

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);

        order = orderRepository.save(order);

        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());

            orderItemRepository.save(orderItem);

            Product product = cartItem.getProduct();
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cartRepository.deleteAll(cartItems);

        return order;
    }

    @Transactional
    public RazorpayOrderResponse placeSingleItem(String userEmail, Long productId) throws RazorpayException {
        User user = userService.getUserByEmail(userEmail);
        Product p = productRepository.findById(productId).get();

        Cart cartItem = cartRepository.findByUserAndProduct(user,p).get();
        if (cartItem.getQuantity() < 0) {
            throw new IllegalStateException("Product is not in the cart or Insufficient quantity");
        }

        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new IllegalStateException("Product not found.");
        }

        Product product = productOptional.get();

        if (cartItem.getQuantity() > product.getStockQuantity()) {
            throw new IllegalStateException("Insufficient stock for the requested quantity.");
        }

        double totalAmount = product.getPrice() * cartItem.getQuantity();

        RazorpayClient razorpayClient = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (totalAmount * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + userEmail);

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);

        order = orderRepository.save(order);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProductId(cartItem.getProduct().getId());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPrice(product.getPrice());

        orderItemRepository.save(orderItem);

        product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
        productRepository.save(product);

        cartRepository.delete(cartItem);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setOrderId(razorpayOrder.get("id"));
        response.setAmount(totalAmount);
        response.setCurrency(razorpayOrder.get("currency"));
        response.setKey(razorpayKeyId);



        return response;
    }

    public List<Order> getUserOrders(String userEmail) {
        
        return orderRepository.findByUserEmail(userEmail);
    }
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).get();
        order.setStatus(status);
        orderRepository.save(order);
    }

    public String extractEmailFromToken(String token) {
        return cartService.extractEmailFromToken(token);
    }

    public Role extractRoleFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract the email (subject) from the token
        return jwtUtil.extractRole(token);
    }



}
