package main.request.parking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import commons.entities.Park;
import commons.entities.Slot;
import main.MainServer;
import main.request.RequestHandler;

public class SlotRequestHandler {

	public static Park getAllParkSlots(String parkName, String cityName) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("SlotRequestHandler", "No database connection");
			return null;
		}
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT  s.park_id, s.row_id, s.col_id, s.enter_date, s.enter_hour, s.enter_mins, s.park_status, p.row_cnt, p.col_cnt FROM slot s JOIN park p "
					+ "ON s.park_id=p.park_id "
					+ "WHERE p.park_name='"+parkName+"' AND p.city_name='"+cityName+"'");
		
			Park park = ParkRequestHandler.getPark(cityName, parkName);
			Slot slot;
			/*
			 * park_id, row_id, col_id, enter_date, enter_hour, enter_mins, status
			 * */
			while(rs.next()) {
				slot = new Slot(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),
						rs.getInt(5), rs.getInt(6), rs.getBoolean(7));
				park.setSlot(slot.getRow(), slot.getCol(), slot);
			}
			return park;
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isSlotAvbl(int parkId, int rowId, int colId) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("SlotRequestHandler", "No database connection");
			return false;
		}
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM slot WHERE park_id='"+parkId+"' AND row_id='"+rowId+"' AND col_id='"+colId+"'");
			if (!rs.next()) {
				RequestHandler.debug("SlotRequestHandler", "result set was empty");
				return true;
			}
			rs.close();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean reserveSlot(Slot s) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("SlotRequestHandler", "No database connection");
			return false;
		}
		int parkId = ParkRequestHandler.getParkIdByAddress(s.getPark().getCity(), s.getPark().getParkName());
		if (parkId < 0)
			return false;
		
		if (!isSlotAvbl(parkId, s.getRow(), s.getCol())) {
			RequestHandler.debug("SlotRequestHandler", "Slot already reserved");
			return false;
		}
		
		try {
			Statement st = MainServer.dbConnection.createStatement();
			int res = st.executeUpdate("INSERT INTO slot (park_id, row_id, col_id, enter_date, enter_hour, enter_mins, park_status)"
					+ "VALUES ('"+parkId+"','"+s.getRow()+"','"+s.getCol()+"','"+s.getEnterDate()+"','"+s.getHour()+"','"+s.getMin()+"','0')");
			return res > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
}
