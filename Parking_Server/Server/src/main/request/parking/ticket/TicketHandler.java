package main.request.parking.ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import commons.entities.Ticket;
import main.MainServer;
import main.request.RequestHandler;
import main.request.parking.ParkRequestHandler;

public class TicketHandler {

	public static boolean createTicket(Ticket t) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("TicketHandler", "No database connection");
			return false;
		}
		
		int parkId = ParkRequestHandler.getParkIdByAddress(t.getSlot().getPark().getCity(), t.getSlot().getPark().getParkName());
		
		try {
			Statement st = MainServer.dbConnection.createStatement();
			int result = st.executeUpdate("INSERT INTO ticket (park_id, plateId, entry_date, entryHr, entryMin, leaveHr, leaveMin, paid) VALUES"
					+ " ('"+parkId+"','"+t.getPlateId()+"','"+t.getSlot().getEnterDate()+"'"
							+ ",'"+t.getSlot().getHour()+"','"+t.getSlot().getMin()+"','"+t.getLeaveHour()+"','"+t.getLeaveMin()+"','"+t.getPayment()+"')");
			return result > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static Ticket[] getAllTickets(){
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("TicketHandler", "No database connection");
			return null;
		}

		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT p.city_name, p.park_name, t.plateId, t.entry_date, "
					+ "t.entryHr, t.entryMin, t.leaveHr, t.leaveMin, t.paid FROM ticket t JOIN park p "
					+ "ON t.park_id=p.park_id");
			List<Ticket> tickets = new ArrayList<>();
			Ticket t;
			while(rs.next()) {
				t = new Ticket(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getDouble(9));
				
				tickets.add(t);
			}
			Ticket[] tArray = new Ticket[tickets.size()];
			for (int i =0; i < tickets.size(); i++)
				tArray[i] = tickets.get(i);
			
			return tArray;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}



