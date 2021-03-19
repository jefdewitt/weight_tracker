package com.zybooks.cs360appjefdewitt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class NotificationsFragment extends Fragment {

    private final int SMS_PERMISSION_CODE = 1;
    String sendPermission = Manifest.permission.SEND_SMS;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        Button buttonRequest = view.findViewById(R.id.buttonSMS);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hasFilePermissions();
            }
        });

        return view;
    }

    private void hasFilePermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED) {
            // When we've already been granted permission
            Toast.makeText(getActivity(), "You have already granted this permission.", Toast.LENGTH_SHORT).show();
        } else {
            showPermissionRationaleDialog();
        }
    }

    private void showPermissionRationaleDialog() {
        // Is user has already denied permission but is viewing request again, so we give more info
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to allow SMS notifications to alert you " +
                            "when you reach your goal.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            // To identify our request
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted!
                Toast.makeText(getActivity(), "Permission GRANTED", Toast.LENGTH_SHORT).show();
            }
            else {
                // Permission denied!
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
            return;

        }
    }
}