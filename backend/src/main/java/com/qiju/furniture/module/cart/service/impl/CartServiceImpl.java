package com.qiju.furniture.module.cart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiju.furniture.common.exception.BusinessException;
import com.qiju.furniture.module.cart.entity.Cart;
import com.qiju.furniture.module.cart.mapper.CartMapper;
import com.qiju.furniture.module.cart.service.CartService;
import com.qiju.furniture.module.product.entity.Product;
import com.qiju.furniture.module.product.mapper.ProductMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cart Service Implementation
 *
 * @author Qiju Team
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final ProductMapper productMapper;

    public CartServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    @Override
    public List<Cart> getCartBySessionId(String sessionId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getSessionId, sessionId)
                .orderByDesc(Cart::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Cart> getCartByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Cart addCartItem(Cart cart) {
        // Auto-fetch product info if not provided
        if (cart.getProductName() == null && cart.getProductId() != null) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product != null) {
                cart.setProductName(product.getName());
                cart.setProductImage(product.getImage());
                cart.setPrice(product.getPrice());
            }
        }

        // Determine query field based on whether userId or sessionId is set
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        if (cart.getUserId() != null) {
            wrapper.eq(Cart::getUserId, cart.getUserId());
        } else {
            wrapper.eq(Cart::getSessionId, cart.getSessionId());
        }
        wrapper.eq(Cart::getProductId, cart.getProductId());
        Cart existing = this.getOne(wrapper);

        if (existing != null) {
            // Update quantity
            existing.setQuantity(existing.getQuantity() + (cart.getQuantity() != null ? cart.getQuantity() : 1));
            this.updateById(existing);
            return existing;
        }

        // Default quantity to 1
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            cart.setQuantity(1);
        }

        this.save(cart);
        return cart;
    }

    @Override
    public Cart updateQuantity(Long id, Integer quantity) {
        Cart cart = this.getById(id);
        if (cart == null) {
            throw new BusinessException(404, "Cart item not found");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(400, "Quantity must be greater than 0");
        }
        cart.setQuantity(quantity);
        this.updateById(cart);
        return cart;
    }

    @Override
    public void removeCartItem(Long id) {
        Cart cart = this.getById(id);
        if (cart == null) {
            throw new BusinessException(404, "Cart item not found");
        }
        this.removeById(id);
    }

    @Override
    public void clearCart(String sessionId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getSessionId, sessionId);
        this.remove(wrapper);
    }

    @Override
    public void clearCartByUserId(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }
}
