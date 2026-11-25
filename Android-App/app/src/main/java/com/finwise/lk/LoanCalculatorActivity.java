
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

public class LoanCalculatorActivity extends AppCompatActivity {

    private EditText etLoanAmount;
    private EditText etLoanRate;
    private EditText etLoanYears;

    private Button btnCalculateLoan;

    private TextView tvMonthlyEMI;
    private TextView tvTotalInterest;
    private TextView tvTotalPayment;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loan_calculator);

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etLoanRate = findViewById(R.id.etLoanRate);
        etLoanYears = findViewById(R.id.etLoanYears);

        btnCalculateLoan = findViewById(R.id.btnCalculateLoan);

        tvMonthlyEMI = findViewById(R.id.tvMonthlyEMI);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        btnCalculateLoan.setOnClickListener(v -> calculateLoan());
    }

    private void calculateLoan() {

        String amountText =
                etLoanAmount.getText().toString().trim();

        String rateText =
                etLoanRate.getText().toString().trim();

        String yearsText =
                etLoanYears.getText().toString().trim();

        if (amountText.isEmpty()
                || rateText.isEmpty()
                || yearsText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter all loan details",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            double principal = Double.parseDouble(amountText);
            double annualRate = Double.parseDouble(rateText);
            int years = Integer.parseInt(yearsText);

            if (!Double.isFinite(principal)
                    || !Double.isFinite(annualRate)
                    || principal <= 0
                    || annualRate < 0
                    || annualRate > 100
                    || years <= 0
                    || years > 50) {

                Toast.makeText(
                        this,
                        "Please enter valid loan details",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            int numberOfMonths = years * 12;

            double monthlyRate = annualRate / 1200.0;

            double monthlyEMI;

            if (monthlyRate == 0) {

                monthlyEMI = principal / numberOfMonths;

            } else {

                double factor = Math.pow(
                        1 + monthlyRate,
                        numberOfMonths
                );

                monthlyEMI =
                        principal * monthlyRate * factor
                                / (factor - 1);
            }

            double totalPayment =
                    monthlyEMI * numberOfMonths;

            double totalInterest =
                    totalPayment - principal;

            // Display calculated results
            tvMonthlyEMI.setText(
                    "LKR " + formatMoney(monthlyEMI)
            );

            tvTotalInterest.setText(
                    "LKR " + formatMoney(totalInterest)
            );

            tvTotalPayment.setText(
                    "LKR " + formatMoney(totalPayment)
            );

            // Save calculation to MySQL
            saveLoanCalculation(
                    principal,
                    annualRate,
                    years,
                    monthlyEMI,
                    totalInterest,
                    totalPayment
            );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Save loan calculation through PHP API
    private void saveLoanCalculation(
            double principal,
            double annualRate,
            int years,
            double monthlyEMI,
            double totalInterest,
            double totalPayment) {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login to save your calculation",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnCalculateLoan.setEnabled(false);

        executor.execute(() -> {

            try {

                JSONObject data = new JSONObject();

                data.put("user_id", userId);
                data.put("type", "loan");
                data.put("loan_amount", principal);
                data.put("interest_rate", annualRate);
                data.put("period_years", years);
                data.put("monthly_emi", monthlyEMI);
                data.put("total_interest", totalInterest);
                data.put("total_payment", totalPayment);

                String response = ApiClient.post(
                        ApiConfig.SAVE_CALCULATION_URL,
                        data.toString()
                );

                JSONObject result = new JSONObject(response);

                runOnUiThread(() -> {

                    btnCalculateLoan.setEnabled(true);

                    if (result.optString("status")
                            .equals("success")) {

                        Toast.makeText(
                                this,
                                "Loan calculation saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                result.optString(
                                        "message",
                                        "Could not save loan calculation"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnCalculateLoan.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Save failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    private String formatMoney(double amount) {

        NumberFormat formatter =
                NumberFormat.getNumberInstance(Locale.US);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
