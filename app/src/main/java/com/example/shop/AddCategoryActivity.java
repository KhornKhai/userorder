package com.example.shop;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCategoryActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] categoryNames;
    private String[] categoryImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        gridView = findViewById(R.id.gridViewcategory);

        // Fetch and load categories
        loadCategories();
    }

    private void loadCategories() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/API/admin/category.php");
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
                        categoryNames = new String[jsonArray.length()];
                        categoryImages = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject category = jsonArray.getJSONObject(i);
                            categoryNames[i] = category.getString("gate_name");
                            categoryImages[i] = category.getString("gate_img");
                        }

                        // Update UI on the main thread
                        runOnUiThread(() -> {
                            CustomCategoryAdapter adapter = new CustomCategoryAdapter(AddCategoryActivity.this, categoryNames, categoryImages);
                            gridView.setAdapter(adapter);
                        });
                    } else {
                        Log.e("API Error", "No 'data' field in response");
                    }
                } else {
                    Log.e("API Error", "Response Code: " + responseCode);
                }
            } catch (Exception e) {
                Log.e("Load Categories Error", "Error loading categories", e);
            }
        }).start();
    }
}
