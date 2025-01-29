package com.example.parking.ui.parking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.parking.R;
import com.example.parking.client.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import commons.entities.Park;
import commons.requests.Message;
import commons.requests.RequestType;


public class ParkSelectionFragment extends Fragment {
    private Spinner parksSpinner;

    private ParkSelectionView selectParkView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Client.forceWait = true;
        Client.getClient().sendMessageToServer(new Message(RequestType.GET_PARKS));
        while(Client.forceWait){
            System.out.print("");
        }

        Client.debug("ParkSelectionFragment@CreateFragment", "Fetched all parks");

        View view = inflater.inflate(R.layout.fragment_park_selection, container, false);
        selectParkView = new ViewModelProvider(this).get(ParkSelectionView.class);
        Spinner citySpinner = view.findViewById(R.id.cityList);
        parksSpinner = view.findViewById(R.id.parks_list);
        Button continueBtn = view.findViewById(R.id.continue_btn);


        ParkData.CITIES = new ArrayList<>();
        for (String city : ParkData.PARKS.keySet()){
            ParkData.CITIES.add(city);
            List<String> parkNames = new ArrayList<>();
            for (Park p : Objects.requireNonNull(ParkData.PARKS.get(city))){
                parkNames.add(p.getParkName());
            }
            ParkData.PARK_MAP.put(city, parkNames);
        }

        // End of initiating process
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ParkData.CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);


        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                selectParkView.setSelectedCity(selected);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ParkData.PARK_MAP.get(selected));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                parksSpinner.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectParkView.setSelectedCity(null);
            }
        });

        parksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                selectParkView.setSelectedPark(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectParkView.setSelectedPark(null);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "City: " + selectParkView.getSelectedCity() + ", Park: " + selectParkView.getSelectedPark(), Toast.LENGTH_SHORT).show();

                Bundle args = new Bundle(); // used to pass arguments to another fragment.
                args.putString("city", selectParkView.getSelectedCity());
                args.putString("park", selectParkView.getSelectedPark());

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                navController.navigate(R.id.from_park_selection_to_simulator, args);
            }
        });

        return view;
    }
}
