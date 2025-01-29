package com.example.parking.ui.management;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.parking.R;
import com.example.parking.client.Client;
import commons.entities.User;
import commons.requests.Message;
import commons.requests.RequestType;
import java.util.concurrent.Executors;

public class SignupFragment extends Fragment {

    private EditText firstNameField, lastNameField, emailField, passwordField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Enable back button in ActionBar
        if (requireActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true); // Allow fragment to handle menu options

        // Initialize input fields
        firstNameField = view.findViewById(R.id.signup_first_name);
        lastNameField = view.findViewById(R.id.signup_last_name);
        emailField = view.findViewById(R.id.signup_email);
        passwordField = view.findViewById(R.id.signup_password);

        // Signup button
        Button signupButton = view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(v -> {
            if (validateInputs()) {
                performSignup();
            }
        });

        return view;
    }

    // Handle ActionBar Back Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Management layout
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_management);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean validateInputs() {
        // Validate first name
        String firstName = firstNameField.getText().toString();
        if (TextUtils.isEmpty(firstName)) {
            firstNameField.setError("First name is required");
            return false;
        }

        // Validate last name
        String lastName = lastNameField.getText().toString();
        if (TextUtils.isEmpty(lastName)) {
            lastNameField.setError("Last name is required");
            return false;
        }

        // Validate email
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email address");
            return false;
        }

        // Validate password
        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordField.setError("Password must be at least 6 characters");
            return false;
        }

        return true; // All inputs are valid
    }

    private void performSignup() {
        // Gather user input
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        // Create a new user object
        User newUser = new User(firstName, lastName, email, password);

        // Execute signup on a background thread to avoid blocking the UI
        Executors.newSingleThreadExecutor().execute(() -> {
            Client.forceWait = true;
            Client.getClient().sendMessageToServer(new Message(RequestType.REGISTER, newUser));

            // Wait for server response
            while (Client.forceWait) {
                // No blocking UI
            }

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                if (Client.loggedInUser != null) {
                    // Signup success
                    Toast.makeText(getContext(), "Signup successful!", Toast.LENGTH_SHORT).show();

                    // Navigate back to Management layout
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                    navController.navigate(R.id.navigation_management);
                } else {
                    // Signup failure
                    Toast.makeText(getContext(), "Signup failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
