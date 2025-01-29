package com.example.parking.ui.parking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.entities.Park;
public class ParkData {
    public static List<String> CITIES;
    public static Map<String, List<String>> PARK_MAP = new HashMap<>();
    public static Map<String, List<Park>> PARKS;

    public static final float CAR_START_X = (float) -3.0;
    public static final float CAR_START_Y = 448.0F;
    public static final float LANE_3_Y = 992.0F;
    public static final float LANE_2_Y = 740.0F;
    public static final float LANE_1_Y = 448.0F;
}
