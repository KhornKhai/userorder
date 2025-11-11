package com.example.shop;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddProductsActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] titles;
    private String[] prices;
    private String[] imageUrls;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        gridView = findViewById(R.id.gridView);

        // Fetch and load products
        loadProducts();
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/API/admin/products.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    String jsonResponse = sb.toString();
                    Log.d("API Response", jsonResponse);

                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    if (jsonObject.has("data")) { // Ensure "data" exists in the response
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        // Initialize arrays
                        titles = new String[jsonArray.length()];
                        prices = new String[jsonArray.length()];
                        imageUrls = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            titles[i] = product.getString("pro_name");
                            prices[i] = String.valueOf(product.getDouble("pro_price"));
                            imageUrls[i] = product.getString("pro_img");
                        }

                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            CustomAdapter adapter = new CustomAdapter(AddProductsActivity.this, titles, prices, imageUrls);
                            gridView.setAdapter(adapter);
                        });
                    } else {
                        Log.e("API Error", "No 'data' field in response");
                    }
                } else {
                    Log.e("API Error", "Response Code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("Load Products Error", "Error loading products", e);
            }
        }).start();
    }
}
