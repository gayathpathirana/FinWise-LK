
package com.finwise.lk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SalaryCalculatorActivity extends AppCompatActivity {

    EditText etGrossSalary;
    Button btnCalculateSalary;

    TextView tvGrossSalary;
    TextView tvEmployeeEPF;
    TextView tvAPIT;
    TextView tvNetSalary;
    TextView tvEmployerEPF;
    TextView tvETF;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_salary_calculator);

        etGrossSalary = findViewById(R.id.etGrossSalary);
        btnCalculateSalary = findViewById(R.id.btnCalculateSalary);

        tvGrossSalary = findViewById(R.id.tvGrossSalary);
        tvEmployeeEPF = findViewById(R.id.tvEmployeeEPF);
        tvAPIT = findViewById(R.id.tvAPIT);
        tvNetSalary = findViewById(R.id.tvNetSalary);
        tvEmployerEPF = findViewById(R.id.tvEmployerEPF);
        tvETF = findViewById(R.id.tvETF);

        btnCalculateSalary.setOnClickListener(v -> calculateSalary());
    }

    private void calculateSalary() {

        String salaryText =
                etGrossSalary.getText().toString().trim();

        if (salaryText.isEmpty()) {
            etGrossSalary.setError("Please enter your monthly salary");
            return;
        }

        double grossSalary;

        try {
            grossSalary = Double.parseDouble(salaryText);
        } catch (NumberFormatException e) {
            etGrossSalary.setError("Enter a valid salary");
            return;
        }

        if (!Double.isFinite(grossSalary) || grossSalary <= 0) {
            etGrossSalary.setError("Salary must be greater than zero");
            return;
        }

        if (grossSalary > 100000000) {
            etGrossSalary.setError("Please enter a realistic salary amount");
            return;
        }

        // Employee EPF deduction: 8%
        double employeeEPF = grossSalary * 0.08;

        // Estimated monthly APIT using existing formula
        double apit = calculateAPIT(grossSalary);

        // Take-home salary
        double netSalary = grossSalary - employeeEPF - apit;

        // Employer contributions
        double employerEPF = grossSalary * 0.12;
        double etf = grossSalary * 0.03;

        NumberFormat currency =
                NumberFormat.getNumberInstance(Locale.US);

        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);

        // Display results
        tvGrossSalary.setText(
                "Gross Salary: LKR " + currency.format(grossSalary));

        tvEmployeeEPF.setText(
                "Employee EPF (8%): LKR " + currency.format(employeeEPF));

        tvAPIT.setText(
                "Estimated APIT: LKR " + currency.format(apit));

        tvNetSalary.setText(
                "Take-home Salary: LKR " + currency.format(netSalary));

        tvEmployerEPF.setText(
                "Employer EPF (12%): LKR " + currency.format(employerEPF));

        tvETF.setText(
                "Employer ETF (3%): LKR " + currency.format(etf));

        // Save salary calculation to database
        saveSalaryCalculation(
                grossSalary,
                employeeEPF,
                apit,
                netSalary
        );
    }

    private void saveSalaryCalculation(
            double grossSalary,
            double employeeEPF,
            double apit,
            double netSalary) {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {
            Toast.makeText(
                    this,
                    "Please login to save your salary calculation",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        btnCalculateSalary.setEnabled(false);

        executor.execute(() -> {

            try {

                JSONObject data = new JSONObject();

                data.put("user_id", userId);
                data.put("type", "salary");
                data.put("gross_salary", grossSalary);
                data.put("employee_epf", employeeEPF);
                data.put("estimated_apit", apit);
                data.put("net_salary", netSalary);

                String response = ApiClient.post(
                        ApiConfig.SAVE_CALCULATION_URL,
                        data.toString()
                );

                JSONObject result = new JSONObject(response);

                runOnUiThread(() -> {

                    btnCalculateSalary.setEnabled(true);

                    if (result.optString("status").equals("success")) {

                        Toast.makeText(
                                this,
                                "Salary calculation saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                result.optString(
                                        "message",
                                        "Could not save salary calculation"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnCalculateSalary.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Save failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    private double calculateAPIT(double monthlyIncome) {

        if (monthlyIncome <= 150000) {
            return 0;
        } else if (monthlyIncome <= 233333.33) {
            return (monthlyIncome * 0.06) - 9000;
        } else if (monthlyIncome <= 275000) {
            return (monthlyIncome * 0.18) - 37000;
        } else if (monthlyIncome <= 316666.67) {
            return (monthlyIncome * 0.24) - 53500;
        } else if (monthlyIncome <= 358333.33) {
            return (monthlyIncome * 0.30) - 72500;
        } else {
            return (monthlyIncome * 0.36) - 94000;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
