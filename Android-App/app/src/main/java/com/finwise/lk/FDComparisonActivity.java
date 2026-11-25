
package com.finwise.lk;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FDComparisonActivity extends AppCompatActivity {

    private EditText etCompareAmount, etCompareMonths;
    private Button btnCompareBanks;
    private LinearLayout llBankResults;

    private final List<BankRate> banks = new ArrayList<>();

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    private boolean ratesLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fd_comparison);

        etCompareAmount = findViewById(R.id.etCompareAmount);
        etCompareMonths = findViewById(R.id.etCompareMonths);
        btnCompareBanks = findViewById(R.id.btnCompareBanks);
        llBankResults = findViewById(R.id.llBankResults);

        btnCompareBanks.setOnClickListener(v -> compareBanks());

        loadBankRates();
    }

    // Load real FD rates from MySQL through PHP API
    private void loadBankRates() {

        btnCompareBanks.setEnabled(false);

        Toast.makeText(
                this,
                "Loading bank interest rates...",
                Toast.LENGTH_SHORT
        ).show();

        executor.execute(() -> {

            try {

                String response =
                        ApiClient.get(ApiConfig.BANK_RATES_URL);

                JSONObject json = new JSONObject(response);

                if (!json.optString("status").equals("success")) {
                    throw new Exception(
                            json.optString(
                                    "message",
                                    "Unable to load bank rates"
                            )
                    );
                }

                JSONArray ratesArray = json.getJSONArray("rates");

                List<BankRate> loadedRates = new ArrayList<>();

                for (int i = 0; i < ratesArray.length(); i++) {

                    JSONObject item = ratesArray.getJSONObject(i);

                    String productType =
                            item.optString("product_type", "");

                    if (!productType.equalsIgnoreCase("FD")) {
                        continue;
                    }

                    String bankName =
                            item.optString("bank_name", "");

                    double rate =
                            item.optDouble("interest_rate", 0);

                    int months =
                            item.optInt("period_months", 0);

                    if (!bankName.isEmpty()
                            && rate > 0
                            && months > 0) {

                        loadedRates.add(
                                new BankRate(
                                        bankName,
                                        rate,
                                        months
                                )
                        );
                    }
                }

                mainHandler.post(() -> {

                    banks.clear();
                    banks.addAll(loadedRates);

                    ratesLoaded = !banks.isEmpty();

                    btnCompareBanks.setEnabled(true);

                    if (ratesLoaded) {

                        Toast.makeText(
                                this,
                                banks.size() + " FD rates loaded successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "No FD rates found in database",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                mainHandler.post(() -> {

                    btnCompareBanks.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Failed to load rates: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    // Compare banks using the entered amount and period
    private void compareBanks() {

        String amountText =
                etCompareAmount.getText().toString().trim();

        String monthsText =
                etCompareMonths.getText().toString().trim();

        if (amountText.isEmpty() || monthsText.isEmpty()) {

            Toast.makeText(
                    this,
                    "Please enter all details",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!ratesLoaded) {

            Toast.makeText(
                    this,
                    "Bank rates are not loaded yet",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            double principal = Double.parseDouble(amountText);
            int months = Integer.parseInt(monthsText);

            if (Double.isNaN(principal)
                    || Double.isInfinite(principal)
                    || principal <= 0
                    || months <= 0
                    || months > 600) {

                Toast.makeText(
                        this,
                        "Please enter valid values",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Select only banks matching the exact deposit period
            List<BankRate> matchingBanks = new ArrayList<>();

            for (BankRate bank : banks) {

                if (bank.months == months) {
                    matchingBanks.add(bank);
                }
            }

            if (matchingBanks.isEmpty()) {

                llBankResults.removeAllViews();

                TextView message = createText(
                        "No bank rates available for "
                                + months + " months.",
                        16,
                        Color.WHITE,
                        true
                );

                llBankResults.addView(message);

                return;
            }

            // Highest interest rate first
            matchingBanks.sort(
                    Comparator.comparingDouble(
                            (BankRate bank) -> bank.rate
                    ).reversed()
            );

            llBankResults.removeAllViews();

            BankRate bestBank = matchingBanks.get(0);

            double bestInterest = 0;
            double bestMaturity = 0;

            // Display matching banks
            for (int i = 0; i < matchingBanks.size(); i++) {

                BankRate bank = matchingBanks.get(i);

                // Simple interest calculation
                double interest =
                        (principal * bank.rate * months)
                                / (100 * 12);

                double maturity = principal + interest;

                addBankCard(
                        i + 1,
                        bank.name,
                        bank.rate,
                        interest,
                        maturity
                );

                if (i == 0) {
                    bestInterest = interest;
                    bestMaturity = maturity;
                }
            }

            // Save highest-return comparison
            saveBestComparison(
                    principal,
                    bestBank.rate,
                    months,
                    bestInterest,
                    bestMaturity
            );

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // Save the highest-return result for the logged-in user
    private void saveBestComparison(
            double principal,
            double rate,
            int months,
            double interest,
            double maturity) {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Login to save your FD comparison",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        executor.execute(() -> {

            try {

                JSONObject data = new JSONObject();

                data.put("user_id", userId);
                data.put("type", "fd");
                data.put("deposit_amount", principal);
                data.put("interest_rate", rate);
                data.put("period_months", months);
                data.put("interest_amount", interest);
                data.put("maturity_amount", maturity);

                String response = ApiClient.post(
                        ApiConfig.SAVE_CALCULATION_URL,
                        data.toString()
                );

                JSONObject result = new JSONObject(response);

                mainHandler.post(() -> {

                    if (result.optString("status")
                            .equals("success")) {

                        Toast.makeText(
                                this,
                                "Best FD comparison saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                result.optString(
                                        "message",
                                        "Could not save comparison"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {

                mainHandler.post(() ->

                        Toast.makeText(
                                this,
                                "Save failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }

    // Create bank result card
    private void addBankCard(
            int rank,
            String bankName,
            double rate,
            double interest,
            double maturity) {

        LinearLayout card = new LinearLayout(this);

        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);

        GradientDrawable background = new GradientDrawable();

        background.setColor(Color.rgb(24, 38, 59));
        background.setCornerRadius(18);

        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 16);

        card.setLayoutParams(cardParams);

        TextView name = createText(
                "#" + rank + "  " + bankName,
                18,
                Color.WHITE,
                true
        );

        card.addView(name);

        TextView rateText = createText(
                "Annual Interest Rate: " + rate + "%",
                15,
                Color.rgb(244, 197, 66),
                true
        );

        addTopMargin(rateText, 12);
        card.addView(rateText);

        TextView interestText = createText(
                "Estimated Interest: LKR " + formatMoney(interest),
                14,
                Color.LTGRAY,
                false
        );

        addTopMargin(interestText, 12);
        card.addView(interestText);

        TextView maturityText = createText(
                "Maturity Amount: LKR " + formatMoney(maturity),
                15,
                Color.WHITE,
                true
        );

        addTopMargin(maturityText, 8);
        card.addView(maturityText);

        if (rank == 1) {

            TextView best = createText(
                    "BEST RETURN",
                    12,
                    Color.rgb(244, 197, 66),
                    true
            );

            addTopMargin(best, 12);
            card.addView(best);
        }

        llBankResults.addView(card);
    }

    private TextView createText(
            String value,
            int size,
            int color,
            boolean bold) {

        TextView text = new TextView(this);

        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);

        if (bold) {
            text.setTypeface(null, Typeface.BOLD);
        }

        return text;
    }

    private void addTopMargin(TextView view, int margin) {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.topMargin = margin;

        view.setLayoutParams(params);
    }

    private String formatMoney(double amount) {

        NumberFormat formatter =
                NumberFormat.getNumberInstance(Locale.US);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount);
    }

    private static class BankRate {

        String name;
        double rate;
        int months;

        BankRate(String name, double rate, int months) {

            this.name = name;
            this.rate = rate;
            this.months = months;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
