package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String BASE_IMAGE_URL = "http://10.0.2.2/API/admin/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the data from the intent
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        String imageUrl = intent.getStringExtra("image"); // Now expecting a String URL

        // Find views
        TextView titleView = findViewById(R.id.titleView);
        TextView priceView = findViewById(R.id.priceView);
        ImageView imageView = findViewById(R.id.imageView);
        Button addToOrderButton = findViewById(R.id.add_to_order_button);
        ImageView backButton = findViewById(R.id.backButton);

        // Set text views
        titleView.setText(title);
        priceView.setText("" + price);

        // Load image from URL using Picasso
        String fullImageUrl = imageUrl.startsWith("http") ? imageUrl : BASE_IMAGE_URL + imageUrl;
        Picasso.get()
                .load(fullImageUrl)
                .error(R.drawable.error_1)        // Ensure this drawable exists
                .into(imageView);

        // Set up the click listener for the add to cart button
        addToOrderButton.setOnClickListener(v -> {
            addToCart(title, price, fullImageUrl);
        });

        // Set up the click listener for the back button
        backButton.setOnClickListener(v -> finish());
    }

    private void addToCart(String title, String price, String imageUrl) {
        // Logic to add the item to the cart
        Item item = new Item(title, price, imageUrl); // Updated to use a String URL
        Cart.addItem(item); // Add item to cart

        // Feedback to user
        Toast.makeText(this, "Item added to cart!", Toast.LENGTH_SHORT).show();

        // Optionally finish the activity to go back to the previous fragment/activity
        finish();
    }
}
