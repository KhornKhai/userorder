package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrdersAdapter extends BaseAdapter {
    private Context context;
    private JSONArray orders;

    public OrdersAdapter(Context context, JSONArray orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public int getCount() {
        return orders.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return orders.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_grid_item, parent, false);
        }

        // Find the TextViews in the layout
        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView orderDetailsTextView = convertView.findViewById(R.id.orderDetailsTextView);
        TextView totalPriceTextView = convertView.findViewById(R.id.totalPriceTextView);
        TextView createdAtTextView = convertView.findViewById(R.id.createdAtTextView);

        try {
            // Get the order JSON object for this position
            JSONObject order = orders.getJSONObject(position);
            // Set the TextViews with the order data
            usernameTextView.setText(order.getString("username"));
            orderDetailsTextView.setText(order.getString("order_details"));
            totalPriceTextView.setText("$" + order.getDouble("total_price"));
            createdAtTextView.setText(order.getString("created_at"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}