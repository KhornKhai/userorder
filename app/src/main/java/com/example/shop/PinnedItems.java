package com.example.shop;

import java.util.ArrayList;
import java.util.List;

public class PinnedItems {
    private static List<Item> pinnedItems = new ArrayList<>();

    public static void addItem(Item item) {
        if (!pinnedItems.contains(item)) {
            pinnedItems.add(item);
        }
    }

    public static void removeItem(Item item) {
        pinnedItems.remove(item);
    }

    public static List<Item> getPinnedItems() {
        return pinnedItems;
    }
}