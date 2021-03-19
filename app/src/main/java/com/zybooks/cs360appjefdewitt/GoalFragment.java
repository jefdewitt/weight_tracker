package com.zybooks.cs360appjefdewitt;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class GoalFragment extends Fragment {

    private static String mAccountNumber;
    private static WeightEntry goalWeightEntry;

    public GoalFragment() {
        // Required empty public constructor
    }

    public GoalFragment(String accountNumber) {
        Bundle args = new Bundle();
        args.putString("accountNumber", accountNumber);
        setArguments(args);
    }

    public static GoalFragment newInstance(String accountNumber) {
        GoalFragment fragment = new GoalFragment(accountNumber);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        if (getArguments() != null) {
            mAccountNumber = getArguments().getString("accountNumber");
        }

        goalWeightEntry = WeightEntryDatabase.getInstance(getContext())
                .getGoalWeightEntry(mAccountNumber);

        TextView goalView = view.findViewById(R.id.goal_value);

        if (goalWeightEntry != null && goalWeightEntry.isGoal()) {
            goalView.setText(goalWeightEntry.getWeight());
        } else {
            goalWeightEntry = new WeightEntry();
            showUnsetGoalMessage(goalView);
        }

        Button goalButton = view.findViewById(R.id.buttonGoal);
        goalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                String goalWeight = goalWeightEntry.getWeight();

                // Create an EditText for updating value
                final EditText goalBox = new EditText(getContext());
                if (goalWeightEntry != null && goalWeightEntry.isGoal()) {
                    goalBox.setText(goalWeight);
                }
                layout.addView(goalBox);

                new AlertDialog.Builder(getContext())
                        .setTitle("Goal Weight")
                        .setMessage("Click below to update:")
                        .setView(layout)
                        .setPositiveButton("set", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                // Capture new dtext
                                String goalEditText = goalBox.getText().toString();

                                if (!goalEditText.toString().equals("")) {
                                    // Replace old value with new
                                    goalView.setText(goalEditText);

                                    goalWeightEntry = new WeightEntry(mAccountNumber, null, goalEditText, true);

                                    // Update goal weight in db
                                    WeightEntryDatabase.getInstance(getContext()).updateGoalWeight(goalWeightEntry, goalWeight);
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
        });

        return view;
    }

    /**
     * If goal isn't set, instruct user what to do
     */
    private void showUnsetGoalMessage(TextView goalView) {

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create an EditText for updating value
        final EditText goalBox = new EditText(getContext());
        layout.addView(goalBox);

        new AlertDialog.Builder(getContext())
                .setTitle("Uh oh!")
                .setMessage("Your goal weight isn't set. Please enter goal weight below.")
                .setView(layout)
                .setPositiveButton("set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Capture new dtext
                        String goalEditText = goalBox.getText().toString();

                        if (!goalEditText.toString().equals("")) {
                            // Replace old value with new
                            goalView.setText(goalEditText);

                            goalWeightEntry = new WeightEntry(mAccountNumber, null, goalEditText, true);

                            // Add goal weight in db
                            WeightEntryDatabase.getInstance(getContext()).addGoalWeight(goalWeightEntry);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}