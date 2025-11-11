package com.example.shop;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ShopFragment extends Fragment {

    private ListView cartListView;
    private CartAdapter cartAdapter;
    private ArrayList<Item> cartItems;
    private final OkHttpClient client = new OkHttpClient();
    private SharedPreferences sharedPreferences;
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        cartListView = view.findViewById(R.id.cartListView);
        Button orderNowButton = view.findViewById(R.id.customdialog);

        // Load username from SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("username", "Guest");

        cartItems = new ArrayList<>(Cart.getItems() != null ? Cart.getItems() : new ArrayList<>());

        cartAdapter = new CartAdapter(getContext(), cartItems);
        cartListView.setAdapter(cartAdapter);

        orderNowButton.setOnClickListener(v -> {
            if (username.equals("Guest")) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getContext(), "Please log in to place an order.", Toast.LENGTH_SHORT).show();
            } else {
                if (cartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showOrderDialog();
            }
        });

        return view;
    }

    private void showOrderDialog() {
        Dialog dialog = new Dialog(getContext(), R.style.BottomSheetDialog);
        dialog.setContentView(R.layout.dialog_order_confirmation);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        TextView orderSummary = dialog.findViewById(R.id.orderSummary);
        TextView orderTotal = dialog.findViewById(R.id.orderTotal);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        confirmButton.setBackgroundColor(Color.parseColor("#5c31b1"));
        cancelButton.setBackgroundColor(Color.parseColor("#5c31b1"));

        StringBuilder summary = new StringBuilder();
        double total = 0.0;

        for (Item item : cartItems) {
            double itemTotal = Double.parseDouble(item.getPrice().replace("$", "")) * item.getQuantity();
            total += itemTotal;
            summary.append(item.getName()).append(" :qty (").append(item.getQuantity()).append("): ")
                    .append("$").append(String.format("%.2f", itemTotal)).append("\n");
        }

        orderSummary.setText(summary.toString());
        orderTotal.setText("Total: $" + String.format("%.2f", total));

        confirmButton.setOnClickListener(v -> sendOrderToAPI(dialog));
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void sendOrderToAPI(Dialog dialog) {
        try {
            JSONArray itemsArray = new JSONArray();
            double total = 0.0;

            for (Item item : cartItems) {
                if (item == null || item.getName() == null || item.getPrice() == null) {
                    Log.e("ShopFragment", "Item data is null! Check cartItems list.");
                    continue;
                }

                // Create a JSON object for each item
                JSONObject itemObject = new JSONObject();
                String itemName = item.getName() != null ? Arrays.toString(item.getName()) : "Unknown Item"; // Ensure the name is a string
                itemObject.put("name", String.valueOf(item.getName())); //
                itemObject.put("price", item.getPrice().replace("$", "").trim());
                itemObject.put("quantity", item.getQuantity());
                itemsArray.put(itemObject); // Add itemObject to the itemsArray

                double itemPrice = Double.parseDouble(item.getPrice().replace("$", ""));
                total += itemPrice * item.getQuantity();
            }

            JSONObject orderData = new JSONObject();
            orderData.put("username", username);
            orderData.put("items", itemsArray); // This will now contain proper item names
            orderData.put("total_price", total);
            orderData.put("action", "insert");

            String url = "http://10.0.2.2/API/admin/orders.php";
            RequestBody body = RequestBody.create(orderData.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to place order", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (jsonResponse.getString("status").equals("success")) {
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(getContext(), "Order confirmed!", Toast.LENGTH_SHORT).show();
                                    cartItems.clear();
                                    cartAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                });
                            } else {
                                requireActivity().runOnUiThread(() ->
                                        {
                                            try {
                                                Toast.makeText(getContext(), "Error: " + jsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Error processing order!", Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}