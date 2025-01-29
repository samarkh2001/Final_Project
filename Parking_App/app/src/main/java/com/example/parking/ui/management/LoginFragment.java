package com.example.parking.ui.management;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.parking.R;
import com.example.parking.client.Client;
import commons.entities.User;
import commons.requests.Message;
import commons.requests.RequestType;

public class LoginFragment extends Fragment {

    private EditText emailField, passwordField;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize fields
        emailField = view.findViewById(R.id.login_email);
        passwordField = view.findViewById(R.id.login_password);

        // Login button
        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            if (validateInputs()) {  // Only navigate if login succeeds
                NavController navController = Navigation.findNavController(requireView());
                navController.navigate(R.id.from_login_to_management);
            }
        });

        return view;
    }

    private boolean validateInputs() {
        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Enter a valid email");
            return false;
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Enter a password");
            return false;
        }

        User u = new User(email, password);
        Client.forceWait = true;
        Client.getClient().sendMessageToServer(new Message(RequestType.LOGIN, u));

        while (Client.forceWait) {
            System.out.print("");
        }

        return Client.loggedInUser != null;  // Return true if login is successful
    }
}
