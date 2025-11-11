package com.example.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private int[] images; // Array to hold image resource IDs

    // Constructor to initialize the images array
    public ImageSliderAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the slider_item layout for each image
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.slider_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        // Set the image resource for the current position
        holder.imageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        // Return the number of images
        return images.length;
    }

    // ViewHolder class to hold the views for each item in the slider
    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Initialize the ImageView
        }
    }
}