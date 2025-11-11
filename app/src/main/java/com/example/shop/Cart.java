package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private static List<Item> cartItems = new ArrayList<>();

    public static void addItem(Item item) {
        for (Item cartItem : cartItems) {
            if (cartItem.getTitle().equals(item.getTitle())) {
                // Item exists, increment quantity
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        // Item does not exist, add it to the cart
        cartItems.add(item);
    }

    public static List<Item> getItems() {
        return new ArrayList<>(cartItems); // Return a copy of the cart items
    }

    public static void clearCart() {
        cartItems.clear();
    }
}