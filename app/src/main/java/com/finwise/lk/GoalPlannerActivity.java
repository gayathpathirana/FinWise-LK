
package com.finwise.lk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoalPlannerActivity extends AppCompatActivity {

    private EditText etGoalName;
    private EditText etTargetAmount;
    private EditText etCurrentSavings;
    private EditText etGoalMonths;

    private Button btnCalculateGoal;

    private TextView tvGoalResultName;
    private TextView tvRemainingAmount;
    private TextView tvMonthlySavings;
    private TextView tvGoalProgress;

    private ProgressBar pbGoalProgress;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_goal_planner);

        etGoalName = findViewById(R.id.etGoalName);
        etTargetAmount = findViewById(R.id.etTargetAmount);
        etCurrentSavings = findViewById(R.id.etCurrentSavings);
        etGoalMonths = findViewById(R.id.etGoalMonths);

        btnCalculateGoal = findViewById(R.id.btnCalculateGoal);

        tvGoalResultName = findViewById(R.id.tvGoalResultName);
        tvRemainingAmount = findViewById(R.id.tvRemainingAmount);
        tvMonthlySavings = findViewById(R.id.tvMonthlySavings);
        tvGoalProgress = findViewById(R.id.tvGoalProgress);

        pbGoalProgress = findViewById(R.id.pbGoalProgress);

        btnCalculateGoal.setOnClickListener(v -> calculateGoal());

        // Retrieve previously saved goals
        loadSavedGoals();
    }

    private void calculateGoal() {

        String goalName =
                etGoalName.getText().toString().trim();

        String targetText =
                etTargetAmount.getText().toString().trim();

        String savingsText =
                etCurrentSavings.getText().toString().trim();

        String monthsText =
                etGoalMonths.getText().toString().trim();

        if (goalName.isEmpty()) {
            etGoalName.setError("Enter your goal name");
            return;
        }

        if (targetText.isEmpty()) {
            etTargetAmount.setError("Enter target amount");
            return;
        }

        if (savingsText.isEmpty()) {
            etCurrentSavings.setError(
                    "Enter current savings (enter 0 if none)"
            );
            return;
        }

        if (monthsText.isEmpty()) {
            etGoalMonths.setError("Enter target period");
            return;
        }

        double targetAmount;
        double currentSavings;
        int months;

        try {

            targetAmount = Double.parseDouble(targetText);
            currentSavings = Double.parseDouble(savingsText);
            months = Integer.parseInt(monthsText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Please enter valid numbers",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!Double.isFinite(targetAmount)
                || targetAmount <= 0
                || targetAmount > 1000000000000.0) {

            etTargetAmount.setError("Enter a valid target amount");
            return;
        }

        if (!Double.isFinite(currentSavings)
                || currentSavings < 0
                || currentSavings > targetAmount) {

            etCurrentSavings.setError(
                    "Savings must be between 0 and the target amount"
            );
            return;
        }

        if (months < 1 || months > 600) {

            etGoalMonths.setError(
                    "Enter a period between 1 and 600 months"
            );

            return;
        }

        double remainingAmount =
                Math.max(0, targetAmount - currentSavings);

        double monthlySavings =
                remainingAmount / months;

        double progressPercentage =
                Math.min(100, (currentSavings / targetAmount) * 100);

        // Display calculated goal
        displayGoal(
                goalName,
                targetAmount,
                currentSavings,
                months
        );

        // Save goal to MySQL
        saveGoal(
                goalName,
                targetAmount,
                currentSavings,
                months
        );
    }

    // Display goal details using the existing UI
    private void displayGoal(
            String goalName,
            double targetAmount,
            double currentSavings,
            int months) {

        double remainingAmount =
                Math.max(0, targetAmount - currentSavings);

        double monthlySavings =
                remainingAmount / months;

        double progressPercentage =
                Math.min(100, (currentSavings / targetAmount) * 100);

        NumberFormat currency =
                NumberFormat.getNumberInstance(Locale.US);

        currency.setMinimumFractionDigits(2);
        currency.setMaximumFractionDigits(2);

        tvGoalResultName.setText("Goal: " + goalName);

        tvRemainingAmount.setText(
                "Remaining Amount: LKR "
                        + currency.format(remainingAmount)
        );

        if (remainingAmount == 0) {

            tvMonthlySavings.setText(
                    "Congratulations! Your goal is already achieved."
            );

        } else {

            tvMonthlySavings.setText(
                    "Required Monthly Savings: LKR "
                            + currency.format(monthlySavings)
            );
        }

        tvGoalProgress.setText(
                "Savings Progress: "
                        + String.format(
                        Locale.US,
                        "%.1f",
                        progressPercentage
                )
                        + "%"
        );

        pbGoalProgress.setProgress(
                (int) Math.round(progressPercentage)
        );
    }

    // Save goal through PHP API
    private void saveGoal(
            String goalName,
            double targetAmount,
            double currentSavings,
            int months) {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {

            Toast.makeText(
                    this,
                    "Please login to save your financial goal",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnCalculateGoal.setEnabled(false);

        executor.execute(() -> {

            try {

                JSONObject data = new JSONObject();

                data.put("user_id", userId);
                data.put("goal_name", goalName);
                data.put("target_amount", targetAmount);
                data.put("current_savings", currentSavings);
                data.put("target_months", months);

                String response = ApiClient.post(
                        ApiConfig.SAVE_GOAL_URL,
                        data.toString()
                );

                JSONObject result = new JSONObject(response);

                if (result.optString("status").equals("success")) {

                    runOnUiThread(() -> {

                        btnCalculateGoal.setEnabled(true);

                        Toast.makeText(
                                this,
                                "Financial goal saved successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

                    // Refresh saved goals after successful insertion
                    retrieveLatestGoal(userId);

                } else {

                    runOnUiThread(() -> {

                        btnCalculateGoal.setEnabled(true);

                        Toast.makeText(
                                this,
                                result.optString(
                                        "message",
                                        "Could not save financial goal"
                                ),
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {

                    btnCalculateGoal.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Save failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    // Retrieve saved goals when the activity opens
    private void loadSavedGoals() {

        int userId = getSharedPreferences(
                "FinWiseSession",
                MODE_PRIVATE
        ).getInt("user_id", 0);

        if (userId <= 0) {
            return;
        }

        retrieveLatestGoal(userId);
    }

    // Fetch goals from MySQL and display the latest one
    private void retrieveLatestGoal(int userId) {

        executor.execute(() -> {

            try {

                String url = ApiConfig.GET_GOALS_URL
                        + "?user_id=" + userId;

                String response = ApiClient.get(url);

                JSONObject result = new JSONObject(response);

                if (!result.optString("status").equals("success")) {
                    return;
                }

                JSONArray goals = result.optJSONArray("goals");

                if (goals == null || goals.length() == 0) {
                    return;
                }

                // PHP returns newest goals first
                JSONObject latestGoal = goals.getJSONObject(0);

                String goalName =
                        latestGoal.getString("goal_name");

                double targetAmount =
                        latestGoal.getDouble("target_amount");

                double currentSavings =
                        latestGoal.getDouble("current_savings");

                int months =
                        latestGoal.getInt("target_months");

                runOnUiThread(() -> {

                    displayGoal(
                            goalName,
                            targetAmount,
                            currentSavings,
                            months
                    );
                });

            } catch (Exception e) {

                runOnUiThread(() ->

                        Toast.makeText(
                                this,
                                "Could not load saved goals: "
                                        + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
