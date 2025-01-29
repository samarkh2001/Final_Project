package com.example.parking.client;

import com.example.parking.client.request.RequestHandler;

import java.io.IOException;

import commons.entities.User;
import ocsf.client.AbstractClient;
import commons.requests.Message;


public class Client extends AbstractClient {

    private static Client client;

    public static User loggedInUser;
    public static boolean connected = false;
    private boolean awaitResponse = true;

    public static boolean forceWait = false;
    private Client(String host, int port) {
        super(host, port);
    }

    @Override
    protected void handleMessageFromServer(Object o) {
        awaitResponse = false;
        if (!(o instanceof Message)){
            System.out.println("[Client] - invalid response from Server!");// TODO - better handling
            return;
        }
        Message m = (Message) o;
        RequestHandler.handleRequest(m);
        Client.forceWait = false;
    }

    /**
     * function to send a message to the server
     * */
    public void sendMessageToServer(Message message) {
        new Thread(()-> {
            try
            {
                openConnection();//in order to send a message
                connected = true;
                awaitResponse = true;
                debug("Client@sendMessageToServer", "opened connection");
                sendToServer(message);
                debug("Client@sendMessageToServer", "sent message to server");
                int loops = 0;
                // wait for response
                while (awaitResponse) {
                    try {
                        Thread.sleep(100); //1000 = 1 second -> 15 seconds = 15000ms
                        loops++;
                        debug("Client@sendMessageToServer", "Time elapsed: " + (loops/10) + " seconds");
                        if (loops >= 150){
                            awaitResponse = false;
                            Client.forceWait = false;
                            //TODO - handle no response
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(IOException e)
            {
                connected = false;
                System.out.println("[Client] - Error sending message to server");
                e.printStackTrace();
            }
        }).start();

    }

    /**
     * Initiates the client instance
     */
    public static Client getClient(){
        if (client != null) // if there is already a client instance, return the existing instance
            return client;
        //otherwise create a new instance
        client = new Client(Configs.HOST, Configs.PORT);
        return client;
    }

    public boolean isAwaitingResponse(){
        return awaitResponse;
    }

    public static void debug(String src, String msg){
        if (!Configs.DEBUG)
            return;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(src);
        sb.append("] - ");
        sb.append(msg);
        System.out.println(sb.toString());
    }

}
