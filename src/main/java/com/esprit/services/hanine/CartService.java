package com.esprit.services.hanine;

import com.esprit.entities.hanine.Materiels;

import java.util.ArrayList;
import java.util.List;

public class CartService {
    private List<Materiels> cartItems;

    public CartService() {
        this.cartItems = new ArrayList<>();
    }

    public void addToCart(Materiels material) {
        cartItems.add(material);
    }

    public void removeFromCart(Materiels material) {
        cartItems.removeIf(item -> item.getId() == material.getId());
    }

    public List<Materiels> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public void clearCart() {
        cartItems.clear();
    }
}