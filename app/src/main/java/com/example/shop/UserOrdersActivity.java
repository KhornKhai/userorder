package com.example.shop;

import android.os.Bundle;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;

public class UserOrdersActivity extends AppCompatActivity {

    private GridView ordersGridView;
    private static final String URL = "http://10.0.2.2/API/admin/get_orders.php"; // Replace with your local server URL
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_orders);

        ordersGridView = findViewById(R.id.ordersGridView);
        client = new OkHttpClient();

        fetchOrders();
    }

    private void fetchOrders() {
        Request request = new Request.Builder().url(URL).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> System.out.println("Error fetching data: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> System.out.println("Server Error: " + response.code()));
                    return;
                }

                final String responseData = response.body().string();

                runOnUiThread(() -> parseJSON(responseData));
            }
        });
    }

    private void parseJSON(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            OrdersAdapter adapter = new OrdersAdapter(this, jsonArray);
            ordersGridView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}