package com.example.jwt.repository;

import com.example.jwt.model.Cart;
import com.example.jwt.model.Product;
import com.example.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    List<Cart> findByUserEmail(String email);
    Optional<Cart> findByUserAndProduct(User user, Product product);

    Optional<Cart> findByUserEmailAndProductId(String userEmail, Long productId);
}