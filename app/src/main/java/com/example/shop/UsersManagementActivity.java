package com.example.shop;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

public class UsersManagementActivity extends AppCompatActivity {
    private ListView userListView;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        userListView.setAdapter(userAdapter);

        Button addUserButton = findViewById(R.id.addUserButton);
        addUserButton.setOnClickListener(v -> showAddUserDialog());

        loadUsers(); // Load users initially

        // Click listener for user update
        userListView.setOnItemClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            showUpdateUserDialog(selectedUser);
        });

        // Long click listener for user deletion
        userListView.setOnItemLongClickListener((parent, view, position, id) -> {
            User selectedUser = userList.get(position);
            new AlertDialog.Builder(this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteUser(selectedUser.getId()))
                    .setNegativeButton("No", null)
                    .show();
            return true; // Indicate that the long click was handled
        });
    }

    private void handleErrorResponse(HttpURLConnection conn) throws IOException {
        StringBuilder responseMessage = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            responseMessage.append(line);
        }
        reader.close();
        runOnUiThread(() -> showToast("Error: " + responseMessage.toString()));
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void loadUsers() {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.110/API/connect/cn.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONArray dataArray = new JSONObject(sb.toString()).getJSONArray("data");
                    userList.clear();
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject userJson = dataArray.getJSONObject(i);
                        userList.add(new User(userJson.getString("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("type")));
                    }

                    runOnUiThread(userAdapter::notifyDataSetChanged);
                } else {
                    showToast("Failed to load users");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error: " + e.getMessage());
            }
        }).start();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add User");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Spinner userTypeSpinner = dialogView.findViewById(R.id.userTypeSpinner);

        // Populate the Spinner with user types
        String[] userTypes = {"Admin", "User"}; // Example user types
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String userType = userTypeSpinner.getSelectedItem().toString(); // Get selected user type

            if (username.isEmpty() || userType.isEmpty()) {
                showToast("Username and User Type cannot be empty!");
            } else {
                addUser(username, email, password, userType);
            }
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
    }

    private void addUser(String username, String email, String password, String userType) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.110/API/add_user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("type", userType);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input);
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String newUserId = ""; // Update based on your server response
                    runOnUiThread(() -> {
                        userList.add(new User(newUserId, username, email, userType));
                        userAdapter.notifyDataSetChanged();
                        showToast("User added successfully");
                    });
                } else {
                    handleErrorResponse(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error: " + e.getMessage());
            }
        }).start();
    }

    private void showUpdateUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update User");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        Spinner userTypeSpinner = dialogView.findViewById(R.id.userTypeSpinner);

        // Pre-fill the fields with existing user data
        usernameEditText.setText(user.getUsername());
        emailEditText.setText(user.getEmail());

        // Populate the Spinner with user types
        String[] userTypes = {"Admin", "User"}; // Example user types
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        // Set the selected user type
        int spinnerPosition = adapter.getPosition(user.getUserType());
        userTypeSpinner.setSelection(spinnerPosition);

        // Add a "Delete" button
        builder.setPositiveButton("Update", (dialog, which) -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String userType = userTypeSpinner.getSelectedItem().toString(); // Get selected user type

            if (username.isEmpty() || userType.isEmpty()) {
                showToast("Username and User Type cannot be empty!");
            } else {
                updateUser(user.getId(), username, email, password, userType);
            }
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Add a "Delete" button to the dialog
        dialog.setOnShowListener(dialogInterface -> {
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete User");
            deleteButton.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", (dialog1, which1) -> deleteUser(user.getId()))
                        .setNegativeButton("No", null)
                        .show();
                dialog.dismiss(); // Close the update dialog
            });

            // Add the delete button to the dialog layout
            ((ViewGroup) dialogView).addView(deleteButton);
        });

        dialog.show();
    }

    private void updateUser(String userId, String username, String email, String password, String userType) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.45/API/update_user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userId);
                jsonObject.put("username", username);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("type", userType);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input);
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        loadUsers(); // Refresh the user list
                        showToast("User updated successfully");
                    });
                } else {
                    handleErrorResponse(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error: " + e.getMessage());
            }
        }).start();
    }

    private void deleteUser(String userId) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.1.110/API/delete_user.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON object for the request
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", userId);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonObject.toString().getBytes("utf-8");
                    os.write(input);
                }

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> {
                        loadUsers(); // Refresh the user list
                        showToast("User deleted successfully");
                    });
                } else {
                    handleErrorResponse(conn);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error: " + e.getMessage());
            }
        }).start();
    }
}