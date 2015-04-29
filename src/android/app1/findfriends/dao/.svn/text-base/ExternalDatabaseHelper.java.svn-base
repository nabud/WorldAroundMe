package android.app1.findfriends.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app1.findfriends.models.Contact;
import android.app1.findfriends.models.Event;


public class ExternalDatabaseHelper {

	private static final String URL = "jdbc:mysql://db4free.net:3306/friendfinderedb";
	private static final String USER_NAME = "friendsfinder";
	private static final String PASSWORD = "Friendsfinder2013";

	private Connection getConnection() {

		Connection connection = null;
		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);                  

		} catch (InstantiationException e) {
			System.err.print("ERROR: " +e);
		} catch (IllegalAccessException e) {
			System.err.print("ERROR: " +e);
		} catch (ClassNotFoundException e) {
			System.err.print("ERROR: " +e);
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}
		return connection;
	}	

	private void closeDBConnection(Connection connection, PreparedStatement prepared, ResultSet result){

		try {
			prepared.close();
			connection.close();
			if(result != null){
				result.close();
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}
	}


	/**
	 * Get A list of all users that are not User himself and any of his friends	 
	 */
	public List<Contact> getUsers(String phone, List<String> phoneNumbers) {

		String sql = "SELECT * FROM users WHERE showing_others = 'T' AND phone_number <> ?";

		//ResultSet result = this.getUsersHelper(sql, email);
		List<Contact> contacts = new ArrayList<Contact>();

		try {
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);	
				prepared.setString(1, phone);
				ResultSet result = prepared.executeQuery();  
				while(result != null &&  result.next()) {
					if(!phoneNumbers.contains(result.getString("phone_number"))){
						Contact contact = new Contact();  
						contact.setUserName(result.getString("user_name"));
						contact.setEmailAddress(result.getString("email_address"));            	
						contact.setLatitiude(result.getString("latitude"));
						contact.setLongitude(result.getString("longitude"));
						contacts.add(contact);
					}
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}           
		return contacts;
	}

	/**
	 * Get A list of all Users that have registered with the app
	 */
	public List<Contact> getAllUsers(Set<Entry<String, String>> set) {

		String sql = "SELECT * FROM users";		
		List<Contact> contacts = new ArrayList<Contact>();

		try {			
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);			
				ResultSet result = prepared.executeQuery();   

				while(result != null &&  result.next()) {				
					Contact contact = new Contact(); 
					contact.setFirstName(result.getString("first_name"));
					contact.setLastName(result.getString("last_name"));
					contact.setUserName(result.getString("user_name"));
					contact.setEmailAddress(result.getString("email_address")); 
					contact.setPhoneNumber(result.getString("phone_number"));
					contact.setLatitiude(result.getString("latitude"));
					contact.setLongitude(result.getString("longitude"));
					contact.setTime_stamp(result.getString("time_stamp"));
					contact.setImage(result.getBytes("profile_picture"));
					contacts.add(contact);				
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}
		return contacts;
	}


	/**
	 * invite a contact to the event	
	 */
	public boolean inviteUser(String id, String phone){

		boolean result = false;
		try{

			String query = "INSERT INTO attending (event_id, phone_number,attending) VALUES (?, ?, ?)";

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1,id);
				preparedStmt.setString(2, phone);	
				preparedStmt.setString(3, "NR");
				result = preparedStmt.execute();	      
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
		return result;
	}


	/**
	 * get the list of all the events the user will attend or the ones he/she has been invited too
	 */
	public List<Event> getEvents(String phone, boolean filter) {
		String sql = null;
		if(!filter){
			sql = "SELECT * FROM events, users, attending WHERE "+
					"events.event_id = attending.event_id "+
					"AND attending.phone_number = users.phone_number "+
					"AND users.phone_number = ?";	
		} else {
			sql = "SELECT * FROM events, users, attending WHERE "+
					"events.event_id = attending.event_id "+
					"AND attending.phone_number = users.phone_number "+
					"AND users.phone_number = ? AND attending.attending = 'A'";
		}
		List<Event> events = new ArrayList<Event>();
		try {			

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);	
				prepared.setString(1, phone);
				ResultSet result = prepared.executeQuery();   

				while(result != null && result.next()) {				
					Event event = new Event(); 
					event.setEventName(result.getString("event_name"));
					event.setLocation(result.getString("location_name"));
					event.setAddress(result.getString("address"));
					event.setEventId(result.getString("event_id"));
					event.setDate(result.getString("date"));
					event.setTime(result.getString("time"));
					event.setOrganizer(result.getString("event_organizer"));
					event.setPhone(result.getString("organizer_phone"));
					event.setAttending(result.getString("attending"));
					events.add(event);				
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}          
		return events;
	}

	
	/**
	 * get the list of all the events the user will attend or the ones he/she has been invited too
	 */
	public List<String> getAttendingPhones(String id) {
		String sql = null;
			
		
			sql = "SELECT * FROM attending WHERE event_id = ?";
		
		List<String> events = new ArrayList<String>();
		try {			

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);	
				prepared.setString(1,id);
				ResultSet result = prepared.executeQuery();   

				while(result != null && result.next()) {				
										
					events.add(result.getString("phone_number"));						
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}          
		return events;
	}
	
	

	/**
	 * get the list of all the events the user will attend or the ones he/she has been invited too
	 */
	public List<String> getAttendingList(String id) {

		String sql = "SELECT * FROM users, attending WHERE "+				
				"attending.phone_number = users.phone_number "+
				"AND attending.event_id = ? AND attending.attending = ?";		
		List<String> contacts = new ArrayList<String>();

		try {			

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);	
				prepared.setString(1, id);
				prepared.setString(2, "A");
				ResultSet result = prepared.executeQuery();   

				while(result != null && result.next()) {					
					contacts.add(result.getString("first_name") + " " + result.getString("last_name"));				
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}          
		return contacts;
	}

	/**
	 * return the list of all Currently visible friends	 
	 */
	public List<Contact> getVisibleFriends(List<String> phone) {

		String sql = "SELECT * FROM users WHERE showing_friends ='T'";		
		List<Contact> contacts = new ArrayList<Contact>();

		try {
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement prepared = connection.prepareStatement(sql);				
				ResultSet result = prepared.executeQuery();
				while(result != null &&  result.next()) {				
					if(phone.contains(result.getString("phone_number"))){
						Contact contact = new Contact();
						contact.setFirstName(result.getString("first_name"));
						contact.setLastName(result.getString("last_name"));
						contact.setEmailAddress(result.getString("email_address")); 
						contact.setPhoneNumber(result.getString("phone_number"));
						contact.setLatitiude(result.getString("latitude"));
						contact.setLongitude(result.getString("longitude"));
						contact.setImage(result.getBytes("profile_picture"));
						contacts.add(contact);
					}
				}
				this.closeDBConnection(connection, prepared, result);
			}
		} catch (SQLException e) {
			System.err.print("ERROR: " +e);
		}           
		return contacts;
	}


	/**
	 * Update the users profile picture	
	 * @param string 
	 */
	public boolean insertImage(byte[] image, String phone, String string){

		boolean result = false;
		try {

			String query = "UPDATE users set profile_picture = ?, time_stamp = ? WHERE phone_number = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setBytes(1, image);
				preparedStmt.setString(2, string);
				preparedStmt.setString(3, phone);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}

	/**
	 * toggle visibility to friends	 
	 */
	public boolean updateShowingToFriends(String showing, String phone){

		boolean result = false;
		try {

			String query = "UPDATE users set showing_friends = ? WHERE phone_number = ?";
			Connection connection = this.getConnection(); 	
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, showing);
				preparedStmt.setString(2, phone);
				preparedStmt.executeUpdate();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}

	/**
	 * toggle visibility to non friends	 
	 */
	public boolean updateShowingToOthers(String showing, String phone){

		boolean result = false;
		try {

			String query = "UPDATE users set showing_others = ? WHERE phone_number = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, showing);
				preparedStmt.setString(2, phone);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}


	/**
	 * update user profile data	
	 */
	public boolean updateUserProfile(Contact contact, String phone, String time){

		boolean result = false;
		try {

			String query = "UPDATE users SET first_name =?, last_name = ?, user_name = ?, phone_number = ?, email_address = ?, password = ?, time_stamp = ? WHERE phone_number = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, contact.getFirstName());
				preparedStmt.setString(2, contact.getLastName());
				preparedStmt.setString(3, contact.getUserName());
				preparedStmt.setString(4, contact.getPhoneNumber());
				preparedStmt.setString(5, contact.getEmailAddress());
				preparedStmt.setString(6, contact.getPassword());
				preparedStmt.setString(7, time);
				preparedStmt.setString(8, phone);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}

	public boolean updateUserEventProfile(Contact contact, String phone){
		boolean result = false;
		try {

			String query = "UPDATE attending SET phone_number = ? WHERE phone_number = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);			
				preparedStmt.setString(1, contact.getPhoneNumber());			
				preparedStmt.setString(2, phone);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result; 
	}


	/**
	 * update the profile users location data	 
	 */
	public boolean updateLocationData(Contact contact, String phone){

		boolean result = false;
		try {

			String query = "UPDATE users set latitude = ?, longitude = ? WHERE phone_number = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, contact.getLatitiude());
				preparedStmt.setString(2, contact.getLongitude());
				preparedStmt.setString(3, phone);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}


	/**
	 * add a new user profile IE a new registration	 
	 */
	public boolean insertUser(Contact contact, String time){

		boolean result = false;
		String number = String.format("(%s) %s-%s", contact.getPhoneNumber().substring(0, 3), 
				contact.getPhoneNumber().substring(3, 6), contact.getPhoneNumber().substring(6, 10));
		try{

			String query = " insert into users (first_name, last_name, user_name, email_address, phone_number, password, latitude, longitude, showing_others, showing_friends, time_stamp)"
					+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1,contact.getFirstName());
				preparedStmt.setString(2, contact.getLastName());
				preparedStmt.setString(3, contact.getUserName());
				preparedStmt.setString(4, contact.getEmailAddress());
				preparedStmt.setString(5, number);
				preparedStmt.setString(6, contact.getPassword());
				preparedStmt.setString(7, contact.getLatitiude());
				preparedStmt.setString(8, contact.getLongitude());
				preparedStmt.setString(9, "T");
				preparedStmt.setString(10, "T");
				preparedStmt.setString(11,time);
				result = preparedStmt.execute();	      
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
		return result;
	}


	/**
	 * create a new event	
	 */
	public boolean insertEvent(Event event){

		boolean result = false;
		try{

			String query = " insert into events (event_id, event_name, location_name, address, date, time, event_organizer, organizer_phone)"
					+ " values (?, ?, ?, ?, ?, ?, ?, ?)";

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1,event.getEventId());
				preparedStmt.setString(2,event.getEventName());
				preparedStmt.setString(3, event.getLocation());
				preparedStmt.setString(4, event.getAddress());
				preparedStmt.setString(5, event.getDate());
				preparedStmt.setString(6, event.getTime());
				preparedStmt.setString(7, event.getOrganizer());	
				preparedStmt.setString(8, event.getPhone());	
				result = preparedStmt.execute();	      
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * cancel an event	
	 */
	public int deleteEvent(String id){

		int result = 0;
		try{

			String query = "DELETE FROM attending WHERE event_id = ?";
			String query2 = "DELETE FROM events WHERE event_id = ?";

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1,id);			

				result = preparedStmt.executeUpdate();	
				preparedStmt = connection.prepareStatement(query2);
				preparedStmt.setString(1,id);
				result = preparedStmt.executeUpdate();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * Do not Attend event	
	 */
	public int notAttending(String id, String phone){

		int result = 0;
		try{

			String query = "DELETE FROM attending WHERE event_id = ? AND phone_number = ?";				

			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1,id);	
				preparedStmt.setString(2,phone);	
				result = preparedStmt.executeUpdate();				
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e){
			System.err.println("Got an exception!");
			System.err.println(e.getMessage());
		}
		return result;
	}

	/**
	 * alter event data	 
	 */
	public boolean updateAnEvent(String id, Event event){

		boolean result = false;
		try {

			String query = "UPDATE events set event_name = ?, location_name = ?, address = ?, date = ?, time = ? WHERE event_id = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);	 
				preparedStmt.setString(1, event.getEventName());
				preparedStmt.setString(2, event.getLocation());
				preparedStmt.setString(3, event.getAddress());
				preparedStmt.setString(4, event.getDate());
				preparedStmt.setString(5, event.getTime());
				preparedStmt.setString(6, id);
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}

	/**
	 * alter event attending status	 
	 */
	public boolean changeAttendingStatus(String id, String attend){

		boolean result = false;
		try {

			String query = "UPDATE attending set attending = ? WHERE event_id = ?";
			Connection connection = this.getConnection();
			if(connection != null){
				PreparedStatement preparedStmt = connection.prepareStatement(query);	 
				preparedStmt.setString(1, attend);
				preparedStmt.setString(2, id);			
				result = preparedStmt.execute();
				this.closeDBConnection(connection, preparedStmt, null);
			}
		} catch (Exception e) {
			System.err.print("ERROR: " +e);
		} 
		return result;       
	}

}
