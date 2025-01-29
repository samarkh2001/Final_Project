package com.example.parking.ui.parking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;

public class ParkSelectionView extends ViewModel {
    private final MutableLiveData<String> selectedCity = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPark = new MutableLiveData<>();
    private final MutableLiveData<List<String>> parks = new MutableLiveData<>();

    public String getSelectedPark(){
        return selectedPark.getValue();
    }
    public void setSelectedPark(String newPark){
        this.selectedPark.setValue(newPark);
    }

    public String getSelectedCity() {
        return selectedCity.getValue();
    }
    public void setSelectedCity(String newCity){
        this.selectedCity.setValue(newCity);
    }

    public MutableLiveData<List<String>> getParks() {
        return parks;
    }
    public void setParks(List<String> newParks) {
        parks.setValue(newParks);
    }
}
