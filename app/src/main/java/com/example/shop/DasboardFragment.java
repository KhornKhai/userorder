package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DasboardFragment extends Fragment {

    private GridView gridView;
    private String[] titles;
    private String[] prices;
    private String[] imageUrls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dasboard, container, false);
        gridView = view.findViewById(R.id.gridView);

        // Fetch products from the server
        loadProducts();

        // Set item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("title", titles[position]);
                intent.putExtra("price", prices[position]);
                intent.putExtra("image", imageUrls[position]); // Adjust for URLs
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadProducts() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/API/admin/products.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // Timeout in case server is down
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
                    Log.d("API Response", jsonResponse); // Debug log

                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    if (jsonObject.has("data")) { // Check if "data" exists
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        // Initialize arrays
                        titles = new String[jsonArray.length()];
                        prices = new String[jsonArray.length()];
                        imageUrls = new String[jsonArray.length()];

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject product = jsonArray.getJSONObject(i);
                            titles[i] = product.getString("pro_name");
                            prices[i] = String.valueOf(product.getDouble("pro_price"));
                            imageUrls[i] = product.getString("pro_img"); // Store full image URL
                        }

                        // Update UI on the main thread safely
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                CustomAdapter adapter = new CustomAdapter(getActivity(), titles, prices, imageUrls);
                                gridView.setAdapter(adapter);
                            });
                        }
                    } else {
                        Log.e("API Error", "No 'data' field in response");
                    }
                } else {
                    Log.e("API Error", "Response Code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Load Products Error", "Error loading products", e);
            }
        }).start();
    }
}
