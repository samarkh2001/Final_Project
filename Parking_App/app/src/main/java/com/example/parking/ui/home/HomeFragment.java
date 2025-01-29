package com.example.parking.ui.home;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.parking.R;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set up instruction icon click listener
        ImageButton instructionIcon = view.findViewById(R.id.instruction_icon);
        instructionIcon.setOnClickListener(v -> showInstructionsDialog());

        return view;
    }

    private void showInstructionsDialog() {
        // Create and customize the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("How to Use the Application")
                .setMessage(
                        "Welcome to the Parking Management Application! Here’s how to use it:\n\n" +
                                "1. Select Parking Location:\n" +
                                "   - In the main page, choose a country and then select a parking lot from the list.\n\n" +
                                "2. Parking Simulator:\n" +
                                "   - Observe the parking lot in real-time.\n" +
                                "   - Cars enter, park in unavailable spots (marked red), and exit after payment.\n\n" +
                                "3. Management (Admin Only):\n" +
                                "   - Click on 'Management' to log in as an admin.\n" +
                                "   - Admins can generate reports and add new managers.\n\n" +
                                "4. Payment and Profile:\n" +
                                "   - Pay for parking before leaving.\n" +
                                "   - Access the profile page to view your parking details, payment history, and remaining time.\n\n" +
                                "We hope you enjoy using the application!"
                )
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss())
                .create();

        // Customize dialog colors
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getWindow().setBackgroundDrawableResource(R.color.lavender);

            // Set text color for Title and Message
            TextView titleView = dialog.findViewById(android.R.id.title);
            if (titleView != null) {
                titleView.setTextColor(Color.WHITE);
            }
            TextView messageView = dialog.findViewById(android.R.id.message);
            if (messageView != null) {
                messageView.setTextColor(Color.WHITE);
            }
        });

        dialog.show();
    }
}
