package com.example.jwt.controller;


import com.example.jwt.model.Cart;
import com.example.jwt.model.User;
import com.example.jwt.repository.ProductRepository;
import com.example.jwt.service.CartService;
import com.example.jwt.service.JwtUtil;
import com.example.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(
            @RequestParam Long productId,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String email = cartService.extractEmailFromToken(token);
            cartService.addToCart(productId, email);
            return ResponseEntity.ok("Product Added to Cart Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/show")
    public ResponseEntity<List<Cart>> showCart(@RequestHeader("Authorization") String token) {
        try {
            String email = cartService.extractEmailFromToken(token);
            List<Cart> cartItems = cartService.getUserCart(email);
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/total-items")
    public ResponseEntity<Integer> getTotalItemsInCart(@RequestHeader("Authorization") String token) {
        try {
            String email = userService.extractEmailFromToken(token);
            if (email == null) {
                return ResponseEntity.status(401).body(null);
            }

            int totalItems = cartService.getTotalItemsInCart(email);
            return ResponseEntity.ok(totalItems);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromCart(@RequestParam Long productId, @RequestHeader("Authorization") String token) {
        try {
            String email = cartService.extractEmailFromToken(token);
            cartService.removeCartItem(productId, email);

            return ResponseEntity.ok("Product removed from cart successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update-quantity/{itemId}")
    public ResponseEntity<String> updateCartQuantity(@PathVariable Long itemId, @RequestParam int quantity, @RequestHeader("Authorization") String token) {
        try {
            String email = cartService.extractEmailFromToken(token);
            cartService.updateCartItemQuantity(itemId, quantity);
            return ResponseEntity.ok("Cart item quantity updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update cart item quantity: " + e.getMessage());
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("Authorization") String  token) {
        String email = cartService.extractEmailFromToken(token);
        User user = userService.getUserByEmail(email);
        cartService.clearCart(user);
        return ResponseEntity.ok("All items cleared from the cart.");
    }





}
