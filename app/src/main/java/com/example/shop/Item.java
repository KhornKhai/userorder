package com.example.shop;

public class Item {
    private String title;
    private String price; // Consider using a double for prices
    private String imageUrl; // Changed from int to String
    private int quantity;

    public Item(String title, String price, String imageUrl) { // Change parameter type
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl; // Store image as a URL
        this.quantity = 1; // Default quantity
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() { // Updated method name
        return imageUrl; // Return image URL
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public char[] getName() {
        return title.toCharArray();
    }
}
