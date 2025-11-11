package com.example.shop;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private RecyclerView categoryRecyclerView, productRecyclerView;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private ArrayList<Category> categoryList;
    private ArrayList<Product> productList;

    private static final String CATEGORY_API_URL = "http://10.0.2.2/API/admin/category.php";
    private static final String PRODUCT_API_URL = "http://10.0.2.2/API/admin/products.php";

    private OkHttpClient client;
    private Handler mainHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up ViewPager2 for image slider
        viewPager = view.findViewById(R.id.viewPager);
        int[] images = {R.drawable.banner1, R.drawable.banner2};
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(images);
        viewPager.setAdapter(sliderAdapter);

        // Initialize OkHttpClient
        client = new OkHttpClient();
        mainHandler = new Handler(Looper.getMainLooper());

        // Setup RecyclerView for categories
        categoryRecyclerView = view.findViewById(R.id.recyclerView);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        categoryRecyclerView.setAdapter(categoryAdapter);

        // Setup RecyclerView for products
        productRecyclerView = view.findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getContext(), productList);
        productRecyclerView.setAdapter(productAdapter);

        // Fetch Data
        fetchCategories();
        fetchProducts();

        return view;
    }

    private void fetchCategories() {
        Request request = new Request.Builder().url(CATEGORY_API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(() -> Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray categoriesArray = jsonObject.getJSONArray("data");

                    categoryList.clear();
                    for (int i = 0; i < categoriesArray.length(); i++) {
                        JSONObject categoryObject = categoriesArray.getJSONObject(i);
                        String name = categoryObject.optString("gate_name", "Unknown");
                        String imageUrl = categoryObject.optString("gate_img", "");

                        categoryList.add(new Category(name, imageUrl));
                    }

                    mainHandler.post(() -> categoryAdapter.notifyDataSetChanged());

                } catch (JSONException e) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Error parsing categories", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void fetchProducts() {
        Request request = new Request.Builder().url(PRODUCT_API_URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mainHandler.post(() -> Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray productsArray = jsonObject.getJSONArray("data");

                    productList.clear();
                    for (int i = 0; i < productsArray.length(); i++) {
                        JSONObject productObject = productsArray.getJSONObject(i);
                        productList.add(new Product(
                                productObject.getString("id"),
                                productObject.getString("pro_name"),
                                productObject.getString("pro_price"),
                                productObject.getString("pro_img")
                        ));
                    }

                    mainHandler.post(() -> productAdapter.notifyDataSetChanged());

                } catch (JSONException e) {
                    mainHandler.post(() -> Toast.makeText(getContext(), "Error parsing products", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
