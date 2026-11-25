
package com.finwise.lk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnFD, btnLoan, btnSalary;
    Button btnInvestment, btnGoals;
    Button btnRegister, btnLogin, btnLogout;

    TextView tvUserWelcome;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        );

        btnFD = findViewById(R.id.btnFD);
        btnLoan = findViewById(R.id.btnLoan);
        btnSalary = findViewById(R.id.btnSalary);
        btnInvestment = findViewById(R.id.btnInvestment);
        btnGoals = findViewById(R.id.btnGoals);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        tvUserWelcome = findViewById(R.id.tvUserWelcome);

        updateSessionUI();

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        btnLogout.setOnClickListener(v -> logoutUser());

        btnFD.setOnClickListener(v ->
                startActivity(new Intent(this, FDComparisonActivity.class)));

        btnLoan.setOnClickListener(v ->
                startActivity(new Intent(this, LoanCalculatorActivity.class)));

        btnSalary.setOnClickListener(v ->
                startActivity(new Intent(this, SalaryCalculatorActivity.class)));

        btnInvestment.setOnClickListener(v ->
                startActivity(new Intent(this, InvestmentCalculatorActivity.class)));

        btnGoals.setOnClickListener(v ->
                startActivity(new Intent(this, GoalPlannerActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSessionUI();
    }

    private void updateSessionUI() {

        boolean loggedIn = preferences.getBoolean(
                "is_logged_in", false);

        if (loggedIn) {

            String fullName = preferences.getString(
                    "full_name", "User");

            tvUserWelcome.setText("Welcome, " + fullName + "!");

            tvUserWelcome.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);

            btnRegister.setVisibility(View.GONE);
            btnLogin.setVisibility(View.GONE);

        } else {

            tvUserWelcome.setVisibility(View.GONE);
            btnLogout.setVisibility(View.GONE);

            btnRegister.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void logoutUser() {

        preferences.edit().clear().apply();

        updateSessionUI();

        Toast.makeText(this,
                "You have logged out successfully.",
                Toast.LENGTH_SHORT).show();
    }
}
