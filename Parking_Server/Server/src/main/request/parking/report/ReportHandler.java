package main.request.parking.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import commons.entities.FullPark;
import main.MainServer;
import main.request.RequestHandler;
import main.request.parking.ParkRequestHandler;

public class ReportHandler {
	
	public static int getCntForPark(FullPark p) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("ReportHandler", "No database connection");
			return -1;
		}
		
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT fp.cnt FROM full_park fp JOIN park p ON p.park_name='"+p.getParkName()+"' AND p.city_name='"+p.getCity()+"' AND fp.park_id=p.park_id"
					+ " WHERE fp.date='"+p.getDate()+"' AND fp.hr='"+p.getHour()+"'");
			if (!rs.next())
				return 0;
			return rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		
	}
	
	public static boolean createReport(FullPark p) {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("ReportHandler", "No database connection");
			return false;
		}
		
		try {
			Statement st = MainServer.dbConnection.createStatement();
			int currentCnt = getCntForPark(p);
			if (currentCnt == -1) {
				RequestHandler.debug("ReportHandler", "Error occured when fetching count.");
				return false;
			}

			int parkId = ParkRequestHandler.getParkIdByAddress(p.getCity(), p.getParkName());
			int res;
			if (currentCnt == 0) {
				 res = st.executeUpdate("INSERT INTO full_park (park_id, date, hr, cnt) VALUES "
				 		+ "('"+parkId+"', '"+p.getDate()+"', '"+p.getHour()+"', '1')");
			}else {
				res = st.executeUpdate("UPDATE full_park SET cnt='"+(currentCnt+1)+"' WHERE park_id='"+parkId+"' AND date='"+p.getDate()+"' AND hr='"+p.getHour()+"'");
			}
			return res > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static FullPark[] getAllReports() {
		if (MainServer.dbConnection == null) {
			RequestHandler.debug("ReportHandler", "No database connection");
			return null;
		}
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT p.park_name, p.city_name, fp.date, fp.hr, fp.cnt FROM full_park fp JOIN park p ON p.park_id=fp.park_id");
			
			List<FullPark> list = new ArrayList<>();
			FullPark fp;
			
			while(rs.next()) {
				fp = new FullPark(rs.getString(2), rs.getString(1), rs.getString(3), rs.getInt(4), rs.getInt(5));
				list.add(fp);
			}
			
			FullPark[] arr = new FullPark[list.size()];
			
			for (int i=0; i < arr.length; i++)
				arr[i] = list.get(i);
			return arr;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		
	}

}
