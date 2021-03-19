package com.zybooks.cs360appjefdewitt;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class GridFragment extends Fragment {

    private static String mAccountNumber;

    public GridFragment() {
        // Required empty public constructor
    }

    public GridFragment(String accountNumber) {
        Bundle args = new Bundle();
        args.putString("accountNumber", accountNumber);
        setArguments(args);
    }

    public static GridFragment newInstance(String accountNumber) {
        GridFragment fragment = new GridFragment(accountNumber);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_grid, container, false);

        if (getArguments() != null) {
            mAccountNumber = getArguments().getString("accountNumber");
        }

        GridLayout grid = (GridLayout) view.findViewById(R.id.grid_layout);
        List<WeightEntry> weightEntries = WeightEntryDatabase.getInstance(getContext())
                .getWeightEntries(mAccountNumber);
        Button removeButton = null;
        Button editButton = null;
        Button addButton = null;

        if (weightEntries != null && weightEntries.size() > 0) {

            // Loop over data to dynamically populate grid
            for (WeightEntry entry : weightEntries) {

                String tag = String.valueOf(entry.getDate());

                removeButton = new Button(getActivity());
                removeButton.setTag("rButton+" + tag);
                removeButton.setText("Remove");
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeRow(view, grid, tag, entry);
                    }
                });

                editButton = new Button(getActivity());
                editButton.setTag("eButton+" + tag);
                editButton.setText("Edit");
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showEditWeightEntryDialog(weightEntries, entry, view, tag);
                    }
                });

                //Add TextView cells
                TextView dateCell = new TextView(getActivity());
                dateCell.setText(entry.getDate());
                dateCell.setTag("dCell+" + tag);
                TextView weightCell = new TextView(getActivity());
                weightCell.setText(entry.getWeight());
                weightCell.setTag("wCell+" + tag);

                // Add new elements to grid
                grid.addView(removeButton);
                grid.addView(dateCell);
                grid.addView(weightCell);
                grid.addView(editButton);
            }
        } else {
            showEmptyGridMessage();
        }

        addButton = view.findViewById(R.id.add_new_row);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddWeightEntryDialog(grid, weightEntries, view);
            }
        });

        return view;
    }

    /**
     * If grid is empty, instruct user what to do
     */
    private void showEmptyGridMessage() {
        new AlertDialog.Builder(getContext())
                .setTitle("Uh oh!")
                .setMessage("The weight grid is empty. Please add an entry to start tracking progress.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    private void showEditWeightEntryDialog(List<WeightEntry> weightEntries, WeightEntry entry, View view, String tag) {

        /**
         *   Multi-EditText dialog layout aided by:
         *   https://stackoverflow.com/questions/12876624/multiple-edittext-objects-in-alertdialog
         */
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        Log.d("entry", "entry = " + entry);

        View rootView = (View) view.getParent();

        // Get Textviews for updating
        TextView dCell = rootView.findViewWithTag("dCell+" + tag);
        TextView wCell = rootView.findViewWithTag("wCell+" + tag);

        // Create an EditText for updating value
        final EditText dateBox = new EditText(getContext());
        // Show old date value
        dateBox.setText(dCell.getText());
        layout.addView(dateBox);

        // Create an EditText for updating value
        final EditText weightBox = new EditText(getContext());
        // Show old weight value
        weightBox.setText(wCell.getText());
        layout.addView(weightBox); // Another add method

        new AlertDialog.Builder(getContext())
                .setTitle("Edit")
                .setMessage("Click below to update:")
                .setView(layout) // Note the set method, not add
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Capture new text
                        String dateEditText = dateBox.getText().toString();
                        String weightEditText = weightBox.getText().toString();
                        // Replace old values with new
                        dCell.setText(dateEditText);
                        wCell.setText(weightEditText);

                        // Set new values
                        entry.setDate(dateEditText);
                        entry.setWeight(weightEditText);
                        entry.setAccount(mAccountNumber);

                        // Add new weightEntry object to db
                        WeightEntryDatabase.getInstance(getContext()).updateWeightEntry(entry);
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

    private void showAddWeightEntryDialog(GridLayout grid, List<WeightEntry> weightEntries, View view) {

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        View rootView = (View) view.getParent();

        // Create an EditText for adding a value
        final EditText dateBox = new EditText(getContext());
        dateBox.setHint("Enter date");
        layout.addView(dateBox);

        // Create an EditText for adding a value
        final EditText weightBox = new EditText(getContext());
        weightBox.setHint("Enter weight");
        layout.addView(weightBox); // Another add method

        new AlertDialog.Builder(getContext())
                .setTitle("Add")
                .setMessage("Click below to update:")
                .setView(layout) // Note the set method, not add
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Capture new text
                        String dateEditText = dateBox.getText().toString();
                        String weightEditText = weightBox.getText().toString();

                        // Set tag value
                        String tag = dateEditText;

                        if (!dateEditText.toString().equals("") && !weightEditText.toString().equals("")) {
                            Button removeButton = new Button(getActivity());
                            removeButton.setText("Remove");
                            removeButton.setTag("rButton+" + tag);

                            Button editButton = new Button(getActivity());
                            editButton.setText("Edit");
                            editButton.setTag("eButton+" + tag);

                            TextView dateCell = new TextView(getActivity());
                            dateCell.setText(dateEditText);
                            dateCell.setTag("dCell+" + tag);

                            TextView weightCell = new TextView(getActivity());
                            weightCell.setText(weightEditText);
                            weightCell.setTag("wCell+" + tag);

                            // Create new grid row
                            grid.addView(removeButton);
                            grid.addView(dateCell);
                            grid.addView(weightCell);
                            grid.addView(editButton);

                            // Create new weightEntry object
                            WeightEntry weightEntry = new WeightEntry(mAccountNumber, dateEditText, weightEditText, false);

                            // Add new weightEntry object to db
                            WeightEntryDatabase.getInstance(getContext()).addWeightEntry(weightEntry);

                            removeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    removeRow(view, grid, tag, weightEntry);
                                }
                            });

                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showEditWeightEntryDialog(weightEntries, weightEntry, view, tag);
                                }
                            });

                            WeightEntry goalWeightEntry = WeightEntryDatabase.getInstance(getContext()).getGoalWeightEntry(mAccountNumber);
                            int permissionSMS = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);

                            if (Float.parseFloat(weightEntry.getWeight()) <= Float.parseFloat(goalWeightEntry.getWeight())) {

                                if (permissionSMS == PackageManager.PERMISSION_GRANTED) {
                                    String messageToSend = "You did it! You reached your goal weight! Congratulations!";
                                    String number = "5555555555";

                                    SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
                                }
                            }

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

    private void removeRow(View view, GridLayout grid, String tag, WeightEntry entry) {
        View rootView = (View) view.getParent();
        grid.removeView(view.findViewWithTag("rButton+" + tag));
        grid.removeView(rootView.findViewWithTag("dCell+" + tag));
        grid.removeView(rootView.findViewWithTag("wCell+" + tag));
        grid.removeView(rootView.findViewWithTag("eButton+" + tag));

        // Remove weightEntry object from db
        WeightEntryDatabase.getInstance(getContext()).deleteWeightEntry(entry);
    }

}