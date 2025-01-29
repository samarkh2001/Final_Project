package main.request.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import commons.entities.User;
import main.MainServer;
import main.request.RequestHandler;

public class UserRequestHandler {

	public static boolean register(User u) {
		if (userExist(u.getEmail()))
			return false;
		try {
			Statement st = MainServer.dbConnection.createStatement();
			int res = st.executeUpdate("INSERT INTO users (fname, lname, email, password)"
					+ " VALUES ('"+u.getFirstName()+"','"+u.getLastName()+"','"+u.getEmail()+"','"+u.getPassword()+"')");
			return res > 0;
		} catch (SQLException e) {
			System.out.println("[UserRequestHandler] - failed to execute query");
			e.printStackTrace();
		}
		return false;
	}

	public static boolean userExist(String email) {
		if (MainServer.dbConnection == null)
			return false;
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE email='"+email+"'");
			if (!rs.next()) {
				RequestHandler.debug("UserRequestHandler", "result set was empty");
				return false;
			}
			rs.close();
			return true;
		} catch (SQLException e) {
			System.out.println("[UserRequestHandler] - failed to execute query");
			e.printStackTrace();
		}
		return false;
	}
	
	public static User userLogin(String email, String pass) {
		if (MainServer.dbConnection == null)
			return null;
		try {
			Statement st = MainServer.dbConnection.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM users WHERE email='"+email+"' AND password='"+pass+"'");
			if (!rs.next()) {
				RequestHandler.debug("UserRequestHandler", "result set was empty");
				return null;
			}
			RequestHandler.debug("UserRequestHandler@userLogin", "User exist!");
			User u = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
			rs.close();
			return u;
		} catch (SQLException e) {
			System.out.println("[UserRequestHandler] - failed to execute query");
			e.printStackTrace();
		}
		return null;
	}

}
