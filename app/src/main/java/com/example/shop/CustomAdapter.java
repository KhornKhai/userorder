package com.example.shop;

import android.content.Context;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private String[] titles;
    private String[] priceViews;
    private String[] imageUrls;

    // Define the base URL for images
    private static final String BASE_IMAGE_URL = "http://10.0.2.2/API/admin/";

    public CustomAdapter(Context context, String[] titles, String[] priceViews, String[] imageUrls) {
        this.context = context;
        this.titles = (titles != null) ? titles : new String[0]; // Prevent null array crash
        this.priceViews = (priceViews != null) ? priceViews : new String[0];
        this.imageUrls = (imageUrls != null) ? imageUrls : new String[0];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return (titles.length > position) ? titles[position] : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shop_card, parent, false);

            // ViewHolder pattern for performance optimization
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.titleView = convertView.findViewById(R.id.titleView);
            holder.priceView = convertView.findViewById(R.id.priceView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set text values safely
        holder.titleView.setText(titles[position]);
        holder.priceView.setText("" + priceViews[position]);

        // Build the full image URL
        String fullImageUrl = BASE_IMAGE_URL + imageUrls[position];

        // Log the image URL for debugging
        Log.d("ImageURL", "Loading: " + fullImageUrl);

        // Load image safely
        if (imageUrls.length > position && imageUrls[position] != null && !imageUrls[position].isEmpty()) {
            Picasso.get()
                    .load(fullImageUrl)
                    .error(R.drawable.error_1)        // Ensure this exists
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            // Set default image if URL is invalid
            holder.imageView.setImageResource(R.drawable.error_1);
        }

        return convertView;
    }

    // ViewHolder pattern to optimize performance
    static class ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView priceView;
    }
}
