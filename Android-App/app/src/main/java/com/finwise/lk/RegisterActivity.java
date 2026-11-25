
package com.finwise.lk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "FINWISE_REGISTER";
    private static final int LOCAL_NETWORK_REQUEST = 1001;

    EditText etFullName, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (name.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= 37
                && checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK")
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{"android.permission.ACCESS_LOCAL_NETWORK"},
                    LOCAL_NETWORK_REQUEST
            );
            return;
        }

        sendRegistration(name, email, password);
    }

    private void sendRegistration(String name, String email, String password) {

        btnRegister.setEnabled(false);

        Toast.makeText(this, "Registering...", Toast.LENGTH_SHORT).show();

        executor.execute(() -> {

            String responseMessage;
            boolean success = false;

            try {
                JSONObject json = new JSONObject();
                json.put("full_name", name);
                json.put("email", email);
                json.put("password", password);

                Log.d(TAG, "Sending registration request");

                String response = ApiClient.post(
                        ApiConfig.REGISTER_URL,
                        json.toString()
                );

                Log.d(TAG, "Server response: " + response);

                JSONObject result = new JSONObject(response);

                responseMessage = result.optString(
                        "message", "Unknown response"
                );

                success = "success".equals(result.optString("status"));

            } catch (Exception e) {
                Log.e(TAG, "Registration failed", e);
                responseMessage = "Registration error: " + e.getMessage();
            }

            final String message = responseMessage;
            final boolean registered = success;

            runOnUiThread(() -> {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

                if (registered) {
                    finish();
                } else {
                    btnRegister.setEnabled(true);
                }
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCAL_NETWORK_REQUEST) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                registerUser();

            } else {
                Toast.makeText(this,
                        "Local network permission is required.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
