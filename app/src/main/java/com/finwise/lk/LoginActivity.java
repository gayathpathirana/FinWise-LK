
package com.finwise.lk;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private static final int LOCAL_NETWORK_REQUEST = 101;

    private static final String LOCAL_NETWORK_PERMISSION =
            "android.permission.ACCESS_LOCAL_NETWORK";

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvCreateAccount;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        btnLogin.setOnClickListener(v -> loginUser());

        tvCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(
                        LoginActivity.this,
                        RegisterActivity.class
                ))
        );
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    "Please enter email and password.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Request local network permission on Android 17+
        if (Build.VERSION.SDK_INT >= 37 &&
                checkSelfPermission(LOCAL_NETWORK_PERMISSION)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{LOCAL_NETWORK_PERMISSION},
                    LOCAL_NETWORK_REQUEST
            );
            return;
        }

        performLogin(email, password);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);

        if (requestCode == LOCAL_NETWORK_REQUEST) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                loginUser();

            } else {
                Toast.makeText(this,
                        "Local network permission is required to connect to FinWise server.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void performLogin(String email, String password) {

        btnLogin.setEnabled(false);
        btnLogin.setText("Logging in...");

        executor.execute(() -> {

            try {
                JSONObject request = new JSONObject();
                request.put("email", email);
                request.put("password", password);

                String response = ApiClient.post(
                        ApiConfig.LOGIN_URL,
                        request.toString()
                );

                Log.d("FINWISE_LOGIN", "Response: " + response);

                JSONObject result = new JSONObject(response);

                runOnUiThread(() -> {

                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");

                    if ("success".equals(
                            result.optString("status"))) {

                        JSONObject user = result.optJSONObject("user");

                        if (user == null) {
                            Toast.makeText(this,
                                    "Invalid server response.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int userId = user.optInt("user_id");
                        String fullName = user.optString("full_name");
                        String userEmail = user.optString("email");

                        SharedPreferences preferences =
                                getSharedPreferences(
                                        "FinWiseSession",
                                        MODE_PRIVATE
                                );

                        preferences.edit()
                                .putInt("user_id", userId)
                                .putString("full_name", fullName)
                                .putString("email", userEmail)
                                .putBoolean("is_logged_in", true)
                                .apply();

                        Toast.makeText(this,
                                "Welcome, " + fullName + "!",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                MainActivity.class
                        );

                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this,
                                result.optString(
                                        "message",
                                        "Login failed."
                                ),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {

                Log.e("FINWISE_LOGIN", "Login error", e);

                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("LOGIN");

                    Toast.makeText(this,
                            "Connection error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
