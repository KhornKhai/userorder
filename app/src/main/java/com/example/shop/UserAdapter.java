package com.example.shop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final List<User> users;

    public UserAdapter(Context context, List<User> users) {
        super(context, R.layout.item_user, users); // Ensure you have a layout resource for each user
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_user, parent, false);
        }

        User currentUser = users.get(position);

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView userTypeTextView = convertView.findViewById(R.id.userTypeTextView);

        usernameTextView.setText(currentUser.getUsername());
        userTypeTextView.setText(currentUser.getUserType());

        return convertView;
    }
}