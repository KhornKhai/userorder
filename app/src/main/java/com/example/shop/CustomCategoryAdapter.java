package com.example.shop;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class CustomCategoryAdapter extends BaseAdapter {
    private Context context;
    private String[] categoryNames;
    private String[] categoryImages;

    public CustomCategoryAdapter(Context context, String[] categoryNames, String[] categoryImages) {
        this.context = context;
        this.categoryNames = categoryNames;
        this.categoryImages = categoryImages;
    }

    @Override
    public int getCount() {
        return categoryNames.length;
    }

    @Override
    public Object getItem(int position) {
        return categoryNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_category, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.category_name);
        ImageView imageView = convertView.findViewById(R.id.category_image);

        textView.setText(categoryNames[position]);

        // Constructing image URL (adjust if needed)
        String imageUrl = "http://10.0.2.2/API/admin/" + categoryImages[position];
        Log.d("Image URL", imageUrl);

        // Load image using Glide with error handling
        Glide.with(context)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .error(R.drawable.error_1) // Error image
                        .fitCenter())
                .into(imageView);

        return convertView;
    }
}
