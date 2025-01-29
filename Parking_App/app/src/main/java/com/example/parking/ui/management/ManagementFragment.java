package com.example.parking.ui.management;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.parking.R;
import com.example.parking.client.Client;
import com.example.parking.ui.parking.ParkData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import commons.entities.FullPark;
import commons.entities.Park;
import commons.entities.Ticket;
import commons.requests.Message;
import commons.requests.RequestType;

public class ManagementFragment extends Fragment {

    public static Ticket[] tickets;
    public static FullPark[] fullParkReports;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Check if the admin is logged in; if not, navigate to login
        if (Client.loggedInUser == null) {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.from_management_to_login);
            return null;
        }

        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_management, container, false);

        Client.forceWait = true;
        Client.getClient().sendMessageToServer(new Message(RequestType.GET_PARKS));
        while(Client.forceWait){
            System.out.print("");
        }

        // Set the dynamic welcome message
        TextView welcomeText = view.findViewById(R.id.welcome_user_text);
        String firstName = Client.loggedInUser.getFirstName();
        String lastName = Client.loggedInUser.getLastName();
        String s = "Welcome " + firstName + " " + lastName;
        welcomeText.setText(s);

        // Handle "Register New Admin" button click
        Button registerAdminButton = view.findViewById(R.id.register_new_admin_button);
        registerAdminButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.from_management_to_signup);
        });

        // Handle "Fetch Payment Report" button click
        Button fetchPaymentReportButton = view.findViewById(R.id.fetch_payment_report_button);
        fetchPaymentReportButton.setOnClickListener(v -> {

            Bundle args = new Bundle();
            args.putString("title", "Payment Report");
            args.putInt("op", 1);
            openPopup(args);
        });

        // Handle "Fetch Full Park Report" button click
        Button fetchFullParkReportButton = view.findViewById(R.id.fetch_full_park_button);
        fetchFullParkReportButton.setOnClickListener(v -> {
            Client.forceWait = true;
            Client.getClient().sendMessageToServer(new Message(RequestType.GET_FULL_PARKS));
            while(Client.forceWait)
                System.out.print("");

            Bundle args = new Bundle();
            args.putString("title", "Full Park Reports");
            args.putInt("op", 2);

            openPopup(args);
        });

        // Handle "Fetch Avg Time Report" button click
        Button fetchAvgTimeReportButton = view.findViewById(R.id.fetch_avg_time_report_button);
        fetchAvgTimeReportButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("title", "Average Parking Time");
            args.putInt("op", 3);

            openPopup(args);
        });

        // Handle Logout functionality
        ImageView logoutLayout = view.findViewById(R.id.logout_icon); // Use the layout containing the logout icon and text
        logoutLayout.setClickable(true);
        logoutLayout.setOnClickListener(v -> {
            // Clear the logged-in user
            Client.loggedInUser = null;

            // Navigate back to the login page
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.from_management_to_login);
        });

        return view;
    }

    private void openPopup(Bundle args){
        int op = args.getInt("op");

        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_layout);

        String title = args.getString("title");
        dialog.setTitle(title);

        TextView popupTitle = dialog.findViewById(R.id.popup_title);
        TextView selectedDate = dialog.findViewById(R.id.selected_date);
        ImageView calendar = dialog.findViewById(R.id.calendar_img);
        Button submit = dialog.findViewById(R.id.submit);
        LinearLayout output = dialog.findViewById(R.id.output_layout);

        Spinner cities = dialog.findViewById(R.id.cities);
        Spinner parks = dialog.findViewById(R.id.parks);

        initSpinners(cities, parks);

        popupTitle.setText(title);

        calendar.setClickable(true);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(dialog.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                selectedDate.setText(date);
                                selectedDate.setVisibility(View.VISIBLE);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.removeAllViews();

                String date = (String) selectedDate.getText();

                if (date == null || date.length() <= 3){
                    Toast.makeText(getContext(), "Select a date to continue." , Toast.LENGTH_SHORT).show();
                }else{
                    TextView t;
                    int op = args.getInt("op");

                    String selectedCity = cities.getSelectedItem().toString();
                    String selectedPark = parks.getSelectedItem().toString();
                    if (tickets == null && op != 2){
                        Client.forceWait = true;
                        Client.getClient().sendMessageToServer(new Message(RequestType.GET_TICKETS));
                        while(Client.forceWait)
                            System.out.print("");
                    }
                    List<String> data = handleRequest(op, formateDate(date), new Park(selectedCity, selectedPark));
                    for (String s : data){
                        t = new TextView(dialog.getContext());
                        t.setText(s);
                        output.addView(t);
                    }
                    output.setVisibility(View.VISIBLE);
                }
            }
        });
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    private List<String> handleRequest(int type, String date, Park p){
        List<String> res = new ArrayList<>();
        switch(type){
            case 1: // payment report
                if (tickets != null){
                    double highest = -1;
                    double lowest = 9999999;
                    double total = 0;
                    boolean foundAny = false;
                    for (Ticket t : tickets){
                        if (t != null && t.getSlot().getEnterDate().equals(date)
                                && t.getSlot().getPark().getParkName().equals(p.getParkName())
                                && t.getSlot().getPark().getCity().equals(p.getCity())){
                            foundAny = true;
                            if (t.getPayment() > highest)
                                highest = t.getPayment();
                            if (t.getPayment() < lowest)
                                lowest = t.getPayment();
                            total += t.getPayment();
                        }
                    }
                    if (foundAny){
                        res.add(String.format("Total income: %.2f$.", total));
                        res.add(String.format("Highest bill: %.2f", highest));
                        res.add(String.format("Lowest bill: %.2f", lowest));
                    }else{
                        res.add("No tickets were found in the specified date.");
                    }
                }
                break;
            case 2:// full park report
                if (fullParkReports != null){
                    int highestHrRequests = -1;
                    int highestHrCnt = -1;
                    int cnt = 0;

                    for (FullPark fp : fullParkReports){
                        if (fp.getCity().equals(p.getCity()) && fp.getParkName().equals(p.getParkName()) && fp.getDate().equals(date)){

                            cnt++;
                            if (fp.getCnt() > highestHrCnt){
                                highestHrCnt = fp.getCnt();
                                highestHrRequests = fp.getHour();
                            }
                        }
                    }
                    if (cnt > 0){
                        res.add("There has been " + cnt + " reports.");
                        res.add("Hour with highest requests on full: " + highestHrRequests);
                        res.add("There has been " + highestHrCnt + " occasions.");
                    }else
                        res.add("No reports were found");
                }else{
                    res.add("No reports were found");
                }
                break;
            default: // fetch avg time
                if (tickets != null){
                    int ticketCnt = 0;
                    double overAllTime = 0;
                    for (Ticket t : tickets){
                        if (t.getSlot().getEnterDate().equals(date) &&
                        t.getSlot().getPark().getCity().equals(p.getCity()) &&
                        t.getSlot().getPark().getParkName().equals(p.getParkName())){
                            ticketCnt++;
                            overAllTime += (t.getLeaveHour() - t.getSlot().getHour()) * 60;
                            overAllTime += t.getLeaveMin() - t.getSlot().getMin();
                        }
                    }
                    if (overAllTime == 0 && ticketCnt > 0){
                        res.add("Average parking time is less than a minute.");
                        res.add("Total parked cars: " + ticketCnt);
                    }else{
                        res.add(String.format("Average parking time %.2f minutes", (overAllTime/ticketCnt)));
                        res.add("Total parked cars: " + ticketCnt);
                    }

                }else{
                    res.add("No tickets were found in the specified date.");
                }
                break;
        }
        return res;
    }

    private String formateDate(String date){
        String[] parts = date.split("/");
        String editedDate = parts[2] + "-";
        if (parts[1].length() < 2)
            editedDate += "0"+parts[1];
        else
            editedDate += parts[1];
        editedDate += "-";
        if (parts[0].length() < 2)
            editedDate += "0"+parts[0];
        else
            editedDate += parts[0];
        return editedDate;
    }

    private void initSpinners(Spinner cities, Spinner parks){
        ParkData.CITIES = new ArrayList<>();
        for (String city : ParkData.PARKS.keySet()){
            ParkData.CITIES.add(city);
            List<String> parkNames = new ArrayList<>();
            for (Park p : Objects.requireNonNull(ParkData.PARKS.get(city))){
                parkNames.add(p.getParkName());
            }
            ParkData.PARK_MAP.put(city, parkNames);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ParkData.CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cities.setAdapter(adapter);

        cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ParkData.PARK_MAP.get(selected));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                parks.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //selectParkView.setSelectedCity(null);
            }
        });
    }

}
