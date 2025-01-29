package main.request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import commons.entities.FullPark;
import commons.entities.Park;
import commons.entities.Slot;
import commons.entities.Ticket;
import commons.entities.User;
import commons.requests.Message;
import commons.requests.RequestType;
import main.Configs;
import main.request.parking.ParkRequestHandler;
import main.request.parking.SlotRequestHandler;
import main.request.parking.report.ReportHandler;
import main.request.parking.ticket.TicketHandler;
import main.request.user.UserRequestHandler;
import ocsf.server.src.ConnectionToClient;


public class RequestHandler {

	public static void handleRequest(Message msg, ConnectionToClient client) {
		boolean success = false;
		User u;
		Park park1, park2;
		Slot slot;
		Ticket t;
		FullPark fp;
		switch(msg.getRequestEnumType()) {
		case REGISTER:
			if (msg.getRequestData() instanceof User) {
				u = (User) msg.getRequestData();
				success = UserRequestHandler.register(u);
			}
			break;
		case LOGIN:
			if (msg.getRequestData() instanceof User) {
				u = (User) msg.getRequestData();
				u = UserRequestHandler.userLogin(u.getEmail(), u.getPassword());
				success = u != null;
				msg.setResponse(u);
			}
		break;
		case GET_PARKS:
			Map<String, List<Park>> parks = ParkRequestHandler.getAllParks();
			msg.setResponse(parks);
			success = parks != null;
			break;
			
		case GET_PARK_SLOTS:
			if (msg.getRequestData() instanceof Park) {
				try {
				park1 = (Park) msg.getRequestData();
				park2 = SlotRequestHandler.getAllParkSlots(park1.getParkName(), park1.getCity());
				success = park2 != null && park2.getSlots() != null;
				msg.setResponse(park2);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case RESERVE_SLOT:
			if (msg.getRequestData() instanceof Slot) {
				slot = (Slot) msg.getRequestData();
				success = SlotRequestHandler.reserveSlot(slot);
			}
			break;
		case CREATE_TICKET:
			if (msg.getRequestData() instanceof Ticket) {
				t = (Ticket) msg.getRequestData();
				success = TicketHandler.createTicket(t);
			}
			break;
		case GET_TICKETS:
			msg.setResponse(TicketHandler.getAllTickets());
			success = msg.getResponse() != null;
			break;
		case CREATE_FULL_PARK_REPORT:
			if (msg.getRequestData() instanceof FullPark) {
				fp = (FullPark) msg.getRequestData();
				success = ReportHandler.createReport(fp);
			}
			break;
		case GET_FULL_PARKS:
			FullPark[] res = ReportHandler.getAllReports();
			success = res != null;
			msg.setResponse(res);
			break;
		default:
			respondToClient(client, new Message(RequestType.INVALID_DATATYPE, "Message Type was not found.", false));
			return;
		}
		msg.setSuccess(success);
		respondToClient(client, msg);

	}

	/**
	 * Sends a response to the client through their connection.
	 *
	 * @param client The client connection through which to send the response.
	 * @param msg The message to be sent.
	 */
	public static void respondToClient(ConnectionToClient client, Message msg) {
		try {
			client.sendToClient(msg);
		} catch (IOException e) {
			System.out.println("[ServerRequestHandler] - Error responding to client");
			e.printStackTrace();
		}
	}

	public static void debug(String src, String msg) {
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
