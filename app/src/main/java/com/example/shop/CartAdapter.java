package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> cartItems;

    public CartAdapter(Context context, ArrayList<Item> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        }

        Item item = cartItems.get(position);

        TextView title = convertView.findViewById(R.id.itemTitle);
        TextView price = convertView.findViewById(R.id.itemPrice);
        TextView quantity = convertView.findViewById(R.id.itemQuantity);
        ImageView image = convertView.findViewById(R.id.itemImage);
        ImageView subtractButton = convertView.findViewById(R.id.subtractButton);
        ImageView addButton = convertView.findViewById(R.id.addButton);

        title.setText(item.getTitle());
        price.setText("" + item.getPrice());
        quantity.setText(String.valueOf(item.getQuantity()));

        // Load image from URL using Picasso
        Picasso.get()
                .load(item.getImageUrl()) // Now using URL instead of drawable resource
                .error(R.drawable.error_1)        // Ensure this drawable exists
                .into(image);

        // Set up click listener for the subtract button
        subtractButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1); // Decrease quantity
            } else {
                // Remove only the selected item, not the whole cart
                cartItems.remove(position);
                Toast.makeText(context, item.getTitle() + " removed from cart.", Toast.LENGTH_SHORT).show();
            }
            notifyDataSetChanged(); // Refresh UI
        });

        // Set up click listener for the add button
        addButton.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1); // Increase quantity
            notifyDataSetChanged(); // Refresh UI
        });

        return convertView;
    }
}
