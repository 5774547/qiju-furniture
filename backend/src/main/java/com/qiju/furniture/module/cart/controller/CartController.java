package com.qiju.furniture.module.cart.controller;

import com.qiju.furniture.common.result.Result;
import com.qiju.furniture.module.cart.dto.CartAddDTO;
import com.qiju.furniture.module.cart.dto.CartUpdateDTO;
import com.qiju.furniture.module.cart.entity.Cart;
import com.qiju.furniture.module.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cart Controller - supports both sessionId (anonymous) and userId (logged-in)
 *
 * @author Qiju Team
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Get cart items - by userId (authenticated) or sessionId (anonymous)
     *
     * @param sessionId Session identifier (optional if authenticated)
     * @param authentication Spring Security authentication
     * @return List of cart items
     */
    @GetMapping
    public Result<List<Cart>> getCart(@RequestParam(required = false) String sessionId,
                                       Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<Cart> cartItems;
        if (userId != null) {
            cartItems = cartService.getCartByUserId(userId);
        } else {
            cartItems = cartService.getCartBySessionId(sessionId);
        }
        return Result.ok(cartItems);
    }

    /**
     * Add item to cart - uses userId if authenticated, otherwise sessionId
     *
     * @param cartAddDTO Cart add data
     * @param authentication Spring Security authentication
     * @return Added/updated cart item
     */
    @PostMapping
    public Result<Cart> addCartItem(@Valid @RequestBody CartAddDTO cartAddDTO,
                                     Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        Cart cart = new Cart();
        BeanUtils.copyProperties(cartAddDTO, cart);
        if (userId != null) {
            cart.setUserId(userId);
        }
        Cart created = cartService.addCartItem(cart);
        return Result.ok(created);
    }

    /**
     * Update cart item quantity
     *
     * @param id           Cart item ID
     * @param cartUpdateDTO Update data with quantity
     * @return Updated cart item
     */
    @PutMapping("/{id}")
    public Result<Cart> updateQuantity(@PathVariable Long id,
                                       @Valid @RequestBody CartUpdateDTO cartUpdateDTO) {
        Cart updated = cartService.updateQuantity(id, cartUpdateDTO.getQuantity());
        return Result.ok(updated);
    }

    /**
     * Remove item from cart
     *
     * @param id Cart item ID
     * @return Success result
     */
    @DeleteMapping("/{id}")
    public Result<Void> removeCartItem(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return Result.ok();
    }

    /**
     * Clear all cart items - by userId (authenticated) or sessionId (anonymous)
     *
     * @param sessionId Session identifier (optional if authenticated)
     * @param authentication Spring Security authentication
     * @return Success result
     */
    @DeleteMapping("/clear")
    public Result<Void> clearCart(@RequestParam(required = false) String sessionId,
                                   Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        if (userId != null) {
            cartService.clearCartByUserId(userId);
        } else {
            cartService.clearCart(sessionId);
        }
        return Result.ok();
    }

    /**
     * Extract current user ID from SecurityContext
     *
     * @param authentication Spring Security authentication
     * @return User ID or null if not authenticated
     */
    private Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        return null;
    }
}
