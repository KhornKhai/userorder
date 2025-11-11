package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter {

    private Context context;
    private String[] menuItems;
    private int[] images;

    public MyGridAdapter(Context context, String[] menuItems, int[] images) {
        this.context = context;
        this.menuItems = menuItems;
        this.images = images;
    }

    @Override
    public int getCount() {
        return menuItems.length;
    }

    @Override
    public Object getItem(int position) {
        return menuItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imageView);
            holder.textView = convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the image and text for the grid item
        holder.imageView.setImageResource(images[position]);
        holder.textView.setText(menuItems[position]);

        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }
}