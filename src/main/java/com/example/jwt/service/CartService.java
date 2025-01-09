package com.example.jwt.service;

import com.example.jwt.model.Cart;
import com.example.jwt.model.Product;
import com.example.jwt.model.User;
import com.example.jwt.repository.CartRepository;
import com.example.jwt.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    public void addToCart(Long productId, String email) {
        User user = userService.getUserByEmail(email);
        Product product = productRepository.findById(productId).get();

        Optional<Cart> existingCartItem = cartRepository.findByUserAndProduct(user, product);
        if (existingCartItem.isEmpty()) {
            Cart cartItem = new Cart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cartRepository.save(cartItem);
        }
    }

    public List<Cart> getUserCart(String email) {
        User user = userService.getUserByEmail(email);
        return cartRepository.findByUser(user);
    }

    public int getTotalItemsInCart(String email) {
        List<Cart> cartItems = getUserCart(email);
        return (int) cartItems.stream().map(cart -> cart.getProduct().getId())
                .distinct()
                .count();
    }

    public void removeCartItem(Long productId, String email) {
        User user = userService.getUserByEmail(email);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException("Product not found with ID: " + productId)
        );
        Optional<Cart> existingProd = cartRepository.findByUserAndProduct(user, product);
        if (existingProd.isPresent()) {
            cartRepository.delete(existingProd.get());
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        Cart cartItem = cartRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartRepository.save(cartItem);
    }

    public void clearCart(User user) {
        List<Cart> cartItems = cartRepository.findByUser(user);
        cartRepository.deleteAll(cartItems);
    }

    public String extractEmailFromToken(String token) {
        // Remove "Bearer " prefix from the token if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract the email (subject) from the token
        return jwtUtil.extractUsername(token);
    }



}
