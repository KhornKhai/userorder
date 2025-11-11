package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

public class AdminDashboardFragment extends Fragment {

    private GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dasboard, container, false); // Ensure your layout file name is correct

        // Initialize the GridView
        gridView = view.findViewById(R.id.gridView);
        String[] menuItems = {"Add Category", "Add Products", "User Orders", "Users MS"};
        int[] images = {R.drawable.ic_add, R.drawable.add_list, R.drawable.shoporder, R.drawable.user_ic};

        MyGridAdapter adapter = new MyGridAdapter(getActivity(), menuItems, images);
        gridView.setAdapter(adapter);

        // Set OnItemClickListener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // Navigate to Add Category activity
                        startActivity(new Intent(getActivity(), AddCategoryActivity.class));
                        break;
                    case 1:
                        // Navigate to Add Products activity
                        startActivity(new Intent(getActivity(), AddProductsActivity.class));
                        break;
                    case 2:
                        // Navigate to User Orders activity
                        startActivity(new Intent(getActivity(), UserOrdersActivity.class));
                        break;
                    case 3:
                        // Navigate to Users Management activity
                        startActivity(new Intent(getActivity(), UsersManagementActivity.class));
                        break;
                }
            }
        });

        // Show the GridView if there's data (for demonstration, always showing it)
        gridView.setVisibility(View.VISIBLE); // Change this logic as needed

        return view;
    }
}