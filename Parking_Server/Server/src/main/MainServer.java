package main;
import java.io.IOException;
import java.sql.Connection;

import commons.requests.*;
import main.databse.DatabaseConnector;
import main.request.RequestHandler;
import ocsf.server.src.AbstractServer;
import ocsf.server.src.ConnectionToClient;

public class MainServer extends AbstractServer{
	public static Connection dbConnection;
	private int port;

	public MainServer(int port) {
		super(port);
		this.port = port;
	}

	public void startServer() {
		try {
			DatabaseConnector dbConnector = new DatabaseConnector();
			dbConnection = dbConnector.getConnection("jdbc:mysql://localhost/park_db?serverTimezone=IST","root","314741455");
			if(dbConnection != null)
				System.out.println("DataBase Connection successful");
			else
				System.out.println("Failed to connect to database");
			System.out.println("starting server on port " + port);
			listen();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MainServer server = new MainServer(5556);
		server.startServer();
	}

	@Override
	protected void handleMessageFromClient(Object message, ConnectionToClient client) {
		new Thread(()->{
			try {
				if (!(message instanceof Message)) {
					System.out.println("[MainServer] - invalid message received from client.");
					client.sendToClient(new Message(RequestType.INVALID_DATATYPE, "Invalid data received."));
				}
				Message msg = (Message) message;
				RequestHandler.handleRequest(msg, client);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}).start();

	}

}
