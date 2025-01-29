package com.example.parking.ui.parking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.parking.R;
import com.example.parking.client.Client;
import java.util.Random;
import commons.entities.Park;
import commons.requests.Message;
import commons.requests.RequestType;


public class ParkingSimulatorFragment extends Fragment {

    public static Park park;
    public static boolean reserveSuccess = false;
    public static ImageView[][] slots;

    private ViewGroup parentLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private BlockHandler blockHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parking_simulator, container, false);



        // Enable back button in ActionBar
        if (requireActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true); // Allow fragment to handle menu options


        TextView cityView = view.findViewById(R.id.cityText);
        TextView parkView = view.findViewById(R.id.parkText);

       if (getArguments() == null){
           Toast.makeText(getContext(), "Error passing arguments..." , Toast.LENGTH_SHORT).show();

       }else{
           park = null;
           reserveSuccess = false;

           String city = getArguments().getString("city");
           String parkName = getArguments().getString("park");

           Client.forceWait = true;
           Client.getClient().sendMessageToServer(new Message(RequestType.GET_PARK_SLOTS, new Park(city, parkName)));
           while(Client.forceWait){
               System.out.print("");
           }

           if (park == null){
               Toast.makeText(getContext(), "Error loading park slots" , Toast.LENGTH_SHORT).show();

               NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
               navController.navigate(R.id.from_simulator_to_selection);
               return null;
           }

           initVariables(view, getArguments());
           blockHandler = new BlockHandler(getActivity(), view, park);

           blockHandler.initVariables();

           handler.post(carArrivalRunner);
           handler.post(carExitRunner);
       }

        return view;
    }

    // Handle ActionBar Back Button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to Management layout
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_park_selection);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove all callbacks when the Fragment is destroyed to avoid memory leaks
        handler.removeCallbacks(carArrivalRunner);
        handler.removeCallbacks(carExitRunner);
    }

    private final Runnable carArrivalRunner = new Runnable() {
        @Override
        public void run() {
            if (blockHandler.canAcceptNewCar())
                handleCarArrival();
            else{
                System.out.println("Can't accept car");
                //
            }
            int delay = random.nextInt(5000) + 20000; // Random delay between 20-25 seconds (3000-7000ms)
            handler.postDelayed(this, delay);
        }
    };

    private final Runnable carExitRunner = new Runnable() {
        @Override
        public void run() {
            blockHandler.handleCarExit();

            int delay = random.nextInt(3000) + 18000; // Random delay between 3-7 seconds (3000-7000ms)
            handler.postDelayed(this, delay);
        }
    };
    private void initVariables(View view, Bundle args){
        if (park == null || view == null)
            return;
        parentLayout = view.findViewById(R.id.mainFrame);
        slots = new ImageView[][]{
                {view.findViewById(R.id.park00), view.findViewById(R.id.park01), view.findViewById(R.id.park02), view.findViewById(R.id.park03), view.findViewById(R.id.park04), view.findViewById(R.id.park05), view.findViewById(R.id.park06)},
                {view.findViewById(R.id.park10), view.findViewById(R.id.park11), view.findViewById(R.id.park12), view.findViewById(R.id.park13), view.findViewById(R.id.park14), view.findViewById(R.id.park15), view.findViewById(R.id.park16)},
                {view.findViewById(R.id.park20), view.findViewById(R.id.park21), view.findViewById(R.id.park22), view.findViewById(R.id.park23), view.findViewById(R.id.park24), view.findViewById(R.id.park25), view.findViewById(R.id.park26)},
                {view.findViewById(R.id.park30), view.findViewById(R.id.park31), view.findViewById(R.id.park32), view.findViewById(R.id.park33), view.findViewById(R.id.park34), view.findViewById(R.id.park35), view.findViewById(R.id.park36)}
        };

        // resetting slots
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 7; j++){
                slots[i][j].setVisibility(TextView.INVISIBLE);
            }

        TextView parkName = view.findViewById(R.id.park_name);
        parkName.setText(args.getString("park"));

        TextView cost = view.findViewById(R.id.cost);
        String s = String.valueOf(park.getPrice()) + "$ per second";
        cost.setText(s);

    }

    private void handleCarArrival(){
        blockHandler.setCanAcceptNewCar(false);
        new Thread(()->{
            ImageView car = new ImageView(requireContext());
            requireActivity().runOnUiThread(()->{
                car.setX(ParkData.CAR_START_X);
                car.setY(ParkData.CAR_START_Y);
                car.setImageResource(R.drawable.car);
                car.setVisibility(View.VISIBLE);

                parentLayout.addView(car);
            });
            blockHandler.start(car);
        }).start();
    }

}
