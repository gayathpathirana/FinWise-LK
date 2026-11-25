
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

public class InvestmentCalculatorActivity extends AppCompatActivity {

    private EditText etInitialInvestment;
    private EditText etMonthlyInvestment;
    private EditText etAnnualReturn;
    private EditText etInvestmentYears;

    private Button btnCalculateInvestment;

    private TextView tvTotalInvested;
    private TextView tvInvestmentProfit;
    private TextView tvFinalValue;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_investment_calculator);

        etInitialInvestment = findViewById(R.id.etInitialInvestment);
        etMonthlyInvestment = findViewById(R.id.etMonthlyInvestment);
        etAnnualReturn = findViewById(R.id.etAnnualReturn);
        etInvestmentYears = findViewById(R.id.etInvestmentYears);

        btnCalculateInvestment =
                findViewById(R.id.btnCalculateInvestment);

        tvTotalInvested = findViewById(R.id.tvTotalInvested);
        tvInvestmentProfit = findViewById(R.id.tvInvestmentProfit);
        tvFinalValue = findViewById(R.id.tvFinalValue);

        btnCalculateInvestment.setOnClickListener(
                v -> calculateInvestment()
        );
    }

    private void calculateInvestment() {

        String initialText =
                etInitialInvestment.getText().toString().trim();

        String monthlyText =
                etMonthlyInvestment.getText().toString().trim();

        String rateText =
                etAnnualReturn.getText().toString().trim();

        String yearsText =
                etInvestmentYears.getText().toString().trim();

        if (initialText.isEmpty()) {
            etInitialInvestment.setError("Enter initial investment");
            return;
        }

        if (monthlyText.isEmpty()) {
            etMonthlyInvestment.setError(
                    "Enter monthly investment (enter 0 if none)"
            );
            return;
        }

        if (rateText.isEmpty()) {
            etAnnualReturn.setError("Enter expected annual return");
            return;
        }

        if (yearsText.isEmpty()) {
            etInvestmentYears.setError("Enter investment period");
            return;
        }

        double initialInvestment;
        double monthlyInvestment;
        double annualRate;
        int years;

        try {

            initialInvestment = Double.parseDouble(initialText);
            monthlyInvestment = Double.parseDouble(monthlyText);
            annualRate = Double.parseDouble(rateText);
            years = Integer.parseInt(yearsText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!Double.isFinite(initialInvestment)
                || !Double.isFinite(monthlyInvestment)
                || !Double.isFinite(annualRate)) {

            Toast.makeText(
                    this,
                    "Please enter valid investment values",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (initialInvestment < 0 || monthlyInvestment < 0) {
            etInitialInvestment.setError(
                    "Investment amounts cannot be negative"
            );
            return;
        }

        if (initialInvestment == 0 && monthlyInvestment == 0) {
            etInitialInvestment.setError(
                    "Enter at least one investment amount"
            );
            return;
        }

        if (annualRate < 0 || annualRate > 100) {
            etAnnualReturn.setError(
                    "Enter a rate between 0 and 100"
            );
            return;
        }

        if (years < 1 || years > 50) {
            etInvestmentYears.setError(
                    "Enter a period between 1 and 50 years"
            );
            return;
        }

        int months = years * 12;

        // Convert annual percentage into monthly decimal rate
        double monthlyRate = annualRate / 1200.0;

        // Future value of initial investment
        double initialFutureValue =
                initialInvestment * Math.pow(1 + monthlyRate, months);

        // Future value of monthly contributions
        // Monthly contributions are made at the end of each month.
        double monthlyFutureValue;

        if (monthlyRate == 0) {

            monthlyFutureValue = monthlyInvestment * months;

        } else {

            monthlyFutureValue = monthlyInvestment *
                    ((Math.pow(1 + monthlyRate, months) - 1)
                            / monthlyRate);
        }

        double finalValue =
                initialFutureValue + monthlyFutureValue;

        double totalInvested =
                initialInvestment + (monthlyInvestment * months);

        double estimatedProfit =
                finalValue - totalInvested;

        if (!Double.isFinite(finalValue)
                || !Double.isFinite(totalInvested)
                || !Double.isFinite(estimatedProfit)) {

            Toast.makeText(
                    this,
                    "Calculated value is too large",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Display results
        NumberFormat currency =
                NumberFormat.getNumberInstance(Locale.US);

        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);

        tvTotalInvested.setText(
                "Total Amount Invested: LKR "
                        + currency.format(totalInvested)
        );

        tvInvestmentProfit.setText(
                "Estimated Profit: LKR "
                        + currency.format(estimatedProfit)
        );

        tvFinalValue.setText(
                "Final Investment Value: LKR "
                        + currency.format(finalValue)
        );

        // Save calculation to MySQL
        saveInvestmentCalculation(
                initialInvestment,
                monthlyInvestment,
                annualRate,
                years,
                totalInvested,
                estimatedProfit,
                finalValue
        );
    }

    // Save investment result through PHP API
    private void saveInvestmentCalculation(
            double initialInvestment,
            double monthlyInvestment,
            double annualRate,
            int years,
            double totalInvested,
            double estimatedProfit,
            double finalValue) {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login to save your investment calculation",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnCalculateInvestment.setEnabled(false);

        executor.execute(() -> {

            try {

                JSONObject data = new JSONObject();

                data.put("user_id", userId);
                data.put("type", "investment");
                data.put("initial_amount", initialInvestment);
                data.put("monthly_amount", monthlyInvestment);
                data.put("annual_return", annualRate);
                data.put("period_years", years);
                data.put("total_invested", totalInvested);
                data.put("estimated_profit", estimatedProfit);
                data.put("final_value", finalValue);

                String response = ApiClient.post(
                        ApiConfig.SAVE_CALCULATION_URL,
                        data.toString()
                );

                JSONObject result = new JSONObject(response);

                runOnUiThread(() -> {

                    btnCalculateInvestment.setEnabled(true);

                    if (result.optString("status")
                            .equals("success")) {

                        Toast.makeText(
                                this,
                                "Investment calculation saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                result.optString(
                                        "message",
                                        "Could not save investment calculation"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnCalculateInvestment.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Save failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
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
