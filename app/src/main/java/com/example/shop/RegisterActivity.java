package com.example.shop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterUsername;
    private EditText editTextRegisterEmail;
    private EditText editTextRegisterPassword;
    private EditText editTextRegisterConfirmPassword; // ✅ Added Confirm Password Field
    private Button buttonRegister;

    private static final String REGISTER_URL = "http://10.0.2.2/API/register.php"; // Localhost for emulator

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextRegisterUsername = findViewById(R.id.editTextRegisterUsername);
        editTextRegisterEmail = findViewById(R.id.editTextRegisterEmail);
        editTextRegisterPassword = findViewById(R.id.editTextRegisterPassword);
        editTextRegisterConfirmPassword = findViewById(R.id.editTextRegisterConfirmPassword); // ✅ Initialize Confirm Password
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(view -> {
            String username = editTextRegisterUsername.getText().toString().trim();
            String email = editTextRegisterEmail.getText().toString().trim();
            String password = editTextRegisterPassword.getText().toString().trim();
            String confirmPassword = editTextRegisterConfirmPassword.getText().toString().trim(); // ✅ Get Confirm Password
            String userType = "User";

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Check if passwords match
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            new RegisterUserTask().execute(username, email, password, userType);
        });
    }

    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String username = params[0];
            String email = params[1];
            String password = params[2];
            String userType = params[3];

            try {
                URL url = new URL(REGISTER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject postData = new JSONObject();
                postData.put("username", username);
                postData.put("email", email);
                postData.put("password", password);
                postData.put("type", userType);

                DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
                outputStream.write(postData.toString().getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    return "{\"error\":true,\"message\":\"Server error: " + responseCode + "\"}";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (Exception e) {
                return "{\"error\":true,\"message\":\"Network error: " + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean error = jsonResponse.getBoolean("error");
                String message = jsonResponse.getString("message");

                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                if (!error) {
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                Toast.makeText(RegisterActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onRegisterClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
