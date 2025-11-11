package com.example.shop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    private static final String API_URL = "http://10.0.2.2/API/get_user.php"; // Change to your actual PHP API URL
    private static final String IMAGE_BASE_URL = "http://10.0.2.2/API/admin/"; // Change to your server's image directory

    private ImageView profilePicture;
    private ImageView logoutButton;
    private TextView usernameTextView;
    private TextView statusTextView;
    private SharedPreferences sharedPreferences;
    private OkHttpClient client;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        profilePicture = view.findViewById(R.id.profile_picture);
        logoutButton = view.findViewById(R.id.logout);
        usernameTextView = view.findViewById(R.id.username);
        statusTextView = view.findViewById(R.id.status);
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE);

        // Initialize OkHttp client
        client = new OkHttpClient();

        // Load user data
        loadUserProfile();

        // Logout button action
        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private void loadUserProfile() {
        String storedUsername = sharedPreferences.getString("username", null);
        if (storedUsername == null) {
            Toast.makeText(getContext(), "No user session found!", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", storedUsername);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("ProfileFragment", "Network error: " + e.getMessage());
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Network error!", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("ProfileFragment", "Unexpected response: " + response);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Server error!", Toast.LENGTH_SHORT).show());
                    }
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (jsonResponse.getString("status").equals("success")) {
                        String username = jsonResponse.getString("username");
                        String imagePath = jsonResponse.getString("user_img");

                        // Construct full image URL
                        String imageUrl = imagePath.startsWith("http") ? imagePath : IMAGE_BASE_URL + imagePath;

                        // Save in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putString("profile_image", imageUrl);
                        editor.apply();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                usernameTextView.setText(username);
                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Glide.with(getContext())
                                            .load(imageUrl)
                                            .apply(new RequestOptions().placeholder(R.drawable.image_1).error(R.drawable.profile))
                                            .into(profilePicture);
                                }
                            });
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "JSON Parsing Error", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void logout() {
        // Clear user session data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Redirect to login screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
