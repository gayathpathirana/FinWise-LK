package com.finwise.lk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class FDCalculatorActivity extends AppCompatActivity {

    private EditText etDepositAmount;
    private EditText etInterestRate;
    private EditText etPeriod;

    private Button btnCalculateFD;

    private TextView tvInterestResult;
    private TextView tvMaturityResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fd_calculator);

        // Connect XML components
        etDepositAmount = findViewById(R.id.etDepositAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etPeriod = findViewById(R.id.etPeriod);

        btnCalculateFD = findViewById(R.id.btnCalculateFD);

        tvInterestResult = findViewById(R.id.tvInterestResult);
        tvMaturityResult = findViewById(R.id.tvMaturityResult);

        // Calculate button
        btnCalculateFD.setOnClickListener(v -> calculateFD());
    }

    private void calculateFD() {

        String amountText = etDepositAmount.getText().toString().trim();
        String rateText = etInterestRate.getText().toString().trim();
        String periodText = etPeriod.getText().toString().trim();

        if (amountText.isEmpty() ||
                rateText.isEmpty() ||
                periodText.isEmpty()) {

            Toast.makeText(this,
                    "Please enter all details",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {

            double principal = Double.parseDouble(amountText);
            double rate = Double.parseDouble(rateText);
            int months = Integer.parseInt(periodText);

            if (!Double.isFinite(principal) ||
                    !Double.isFinite(rate) ||
                    principal <= 0 ||
                    rate < 0 ||
                    rate > 100 ||
                    months <= 0 ||
                    months > 600) {

                Toast.makeText(this,
                        "Please enter valid values",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Simple interest calculation
            double interest =
                    (principal * rate * months) / (100 * 12);

            double maturityAmount = principal + interest;

            NumberFormat formatter =
                    NumberFormat.getNumberInstance(Locale.US);

            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            tvInterestResult.setText(
                    "LKR " + formatter.format(interest));

            tvMaturityResult.setText(
                    "LKR " + formatter.format(maturityAmount));

        } catch (NumberFormatException e) {

            Toast.makeText(this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
