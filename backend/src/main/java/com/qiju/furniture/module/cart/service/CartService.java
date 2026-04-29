package com.qiju.furniture.module.cart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qiju.furniture.module.cart.entity.Cart;

import java.util.List;

/**
 * Cart Service Interface
 *
 * @author Qiju Team
 */
public interface CartService extends IService<Cart> {

    /**
     * Get cart items by session ID (anonymous)
     */
    List<Cart> getCartBySessionId(String sessionId);

    /**
     * Get cart items by user ID (logged-in)
     */
    List<Cart> getCartByUserId(Long userId);

    /**
     * Add item to cart
     */
    Cart addCartItem(Cart cart);

    /**
     * Update cart item quantity
     */
    Cart updateQuantity(Long id, Integer quantity);

    /**
     * Remove item from cart
     */
    void removeCartItem(Long id);

    /**
     * Clear all cart items for a session
     */
    void clearCart(String sessionId);

    /**
     * Clear all cart items for a user
     */
    void clearCartByUserId(Long userId);
}
