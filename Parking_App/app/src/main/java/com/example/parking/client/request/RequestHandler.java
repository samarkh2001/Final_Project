package com.example.parking.client.request;

import com.example.parking.client.Client;
import com.example.parking.ui.management.ManagementFragment;
import com.example.parking.ui.parking.ParkData;
import com.example.parking.ui.parking.ParkingSimulatorFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.entities.FullPark;
import commons.entities.Park;
import commons.entities.Ticket;
import commons.entities.User;
import commons.requests.Message;

public class RequestHandler {

    @SuppressWarnings("unchecked")
    public static void handleRequest(Message msg){

        switch (msg.getRequestEnumType()){
            case REGISTER:
                if (msg.isSuccess())
                    System.out.println("Successfully Registered!");
                else
                    System.out.println("Something went wrong registering...!");
                return;
            case LOGIN:
                if (msg.isSuccess() && msg.getResponse() instanceof User){
                    Client.debug("RequestHandler@LOGIN", "User logging in");
                    Client.loggedInUser = (User) msg.getResponse();
                }else{
                    Client.debug("RequestHandler@LOGIN", "invalid login request");
                    Client.loggedInUser = null;
                }
                return;
            case GET_PARKS:
                if (msg.isSuccess() && msg.getResponse() instanceof Map){
                    ParkData.PARKS = (HashMap<String, List<Park>>) msg.getResponse();
                }else
                    Client.debug("RequestHandler@GET_PARKS", "Invalid response type.");
                return;
            case GET_PARK_SLOTS:
                if (msg.isSuccess() && msg.getResponse() instanceof Park){
                    ParkingSimulatorFragment.park = (Park)msg.getResponse();
                }
                return;
            case RESERVE_SLOT:
                ParkingSimulatorFragment.reserveSuccess = msg.isSuccess();
                return;
            case GET_TICKETS:
                if (msg.isSuccess() && msg.getResponse() instanceof Ticket[])
                    ManagementFragment.tickets = (Ticket[]) msg.getResponse();
                return;
            case GET_FULL_PARKS:
                if (msg.isSuccess() && msg.getResponse() instanceof FullPark[])
                    ManagementFragment.fullParkReports = (FullPark[]) msg.getResponse();
                return;
            case CREATE_FULL_PARK_REPORT:
            case CREATE_TICKET:
            case INVALID_DATATYPE:
                return;
            default:
                System.out.println("[RequestHandler] - unhandled request type");
        }

    }

}
