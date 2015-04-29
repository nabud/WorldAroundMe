package android.app1.findfriends.dao;

import java.util.ArrayList;
import java.util.List;

import android.app1.findfriends.models.Contact;
import android.app1.findfriends.models.Event;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class InternalDatabaseHelper extends SQLiteOpenHelper{

	static final String DATABASE_NAME = "friendsFinder_internal";

	public InternalDatabaseHelper(Context context, CursorFactory factory, int databaseVersion) {

		super(context, DATABASE_NAME, factory, databaseVersion);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE profile (first_name TEXT NOT NULL, last_name TEXT NOT NULL, user_name TEXT NOT NULL,email_address TEXT NOT NULL, password TEXT NOT NULL, phone_number TEXT NOT NULL,showing_friends TEXT NOT NULL,showing_others TEXT NOT NULL, profile_picture BLOB)");
		db.execSQL("CREATE TABLE contacts (first_name TEXT NOT NULL, last_name TEXT NOT NULL, user_name TEXT NOT NULL, email_address TEXT NOT NULL, phone_number TEXT NOT NULL, latitude Text NOT NULL, longitude TEXT NOT NULL, tracked Text NOT NULL,time_stamp TEXT NOT NULL, profile_picture BLOB)");
		db.execSQL("CREATE TABLE events (event_name TEXT NOT NULL, event_id TEXT, event_organizer TEXT NOT NULL, organizer_phone TEXT NOT NULL,  location_name TEXT NOT NULL, address TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, attending TEXT NOT NULL)");
	}

	// Do not write any code in here
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	// Adding Contact
	public long addContact(Contact contact) {

		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("email_address", contact.getEmailAddress());	    
		values.put("first_name", contact.getFirstName());
		values.put("last_name", contact.getLastName());
		values.put("user_name",contact.getUserName());
		values.put("phone_number", contact.getPhoneNumber());
		values.put("latitude", contact.getLatitiude());
		values.put("longitude", contact.getLongitude());
		values.put("tracked", contact.getTracked());
		values.put("time_stamp", contact.getTime_stamp());
		if(contact.getImage() != null){
			values.put("profile_picture", contact.getImage());
		}

		// insert row
		return database.insert("contacts", null, values);
	}


	// Adding Profile
	public long addProfile(Contact contact) {
		String number = String.format("(%s) %s-%s", contact.getPhoneNumber().substring(0, 3), 
				contact.getPhoneNumber().substring(3, 6), contact.getPhoneNumber().substring(6, 10));
		SQLiteDatabase database = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("email_address", contact.getEmailAddress());
		values.put("password", contact.getPassword());
		values.put("first_name", contact.getFirstName());
		values.put("last_name", contact.getLastName());
		values.put("user_name",contact.getUserName());
		values.put("phone_number", number);
		values.put("showing_friends", "T");
		values.put("showing_others", "T");

		// insert row
		return database.insert("profile", null, values);
	}

	// get User Profile
	public Contact getUserProfile(String email) {

		String query = "SELECT * FROM profile WHERE email_address = ?";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, new String[]{email});

		Contact contact = new Contact();
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {
				contact.setEmailAddress((cursor.getString(cursor.getColumnIndex("email_address"))));
				contact.setPassword((cursor.getString(cursor.getColumnIndex("password"))));
				contact.setFirstName((cursor.getString(cursor.getColumnIndex("first_name"))));
				contact.setLastName((cursor.getString(cursor.getColumnIndex("last_name"))));
				contact.setUserName((cursor.getString(cursor.getColumnIndex("user_name"))));
				contact.setPhoneNumber((cursor.getString(cursor.getColumnIndex("phone_number"))));
				contact.setShowing_friends((cursor.getString(cursor.getColumnIndex("showing_friends"))));
				contact.setShowing_others((cursor.getString(cursor.getColumnIndex("showing_others"))));
				byte[] bais = cursor.getBlob(cursor.getColumnIndex("profile_picture"));				
				contact.setImage((bais));
			}
		}
		return contact;
	}

	// get Contact
	public Contact getContact (String phone) {

		String query = "SELECT * FROM contacts WHERE phone_number = ?";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, new String[]{phone});

		Contact contact = new Contact();
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {			
				contact.setEmailAddress(cursor.getString(cursor.getColumnIndex("email_address")));
				contact.setFirstName((cursor.getString(cursor.getColumnIndex("first_name"))));
				contact.setLastName((cursor.getString(cursor.getColumnIndex("last_name"))));
				contact.setPhoneNumber((cursor.getString(cursor.getColumnIndex("phone_number")))); 
				contact.setTracked((cursor.getString(cursor.getColumnIndex("tracked"))));
				byte[] bais = cursor.getBlob(cursor.getColumnIndex("profile_picture"));				
				contact.setImage((bais));
			}
		}
		return contact;
	}

	// Get contact List
	public ArrayList<Contact> getContactList(List<String> phones) {

		ArrayList<Contact> contactList = new ArrayList<Contact>();

		String query = "SELECT * FROM contacts";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);		
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {
				do{
					Contact contact = new Contact();
					contact.setEmailAddress(cursor.getString(cursor.getColumnIndex("email_address")));
					contact.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
					contact.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
					contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number"))); 
					contact.setLatitiude((cursor.getString(cursor.getColumnIndex("latitude"))));
					contact.setLongitude((cursor.getString(cursor.getColumnIndex("longitude"))));
					contact.setTracked((cursor.getString(cursor.getColumnIndex("tracked"))));
					contact.setTime_stamp((cursor.getString(cursor.getColumnIndex("time_stamp"))));
					byte[] bais = cursor.getBlob(cursor.getColumnIndex("profile_picture"));				
					contact.setImage((bais));
					if(phones == null){
						contactList.add(contact);
					}
					else if(!phones.contains(contact.getPhoneNumber())){
						contactList.add(contact);
					}
				} while(cursor.moveToNext());
			}
		}

		return contactList;
	}

	// get contact emails list
	public List<String> getContactNumbers() {

		List<String> contactList = new ArrayList<String>();

		String query = "SELECT phone_number FROM contacts";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);		
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {
				do{				
					contactList.add(cursor.getString(cursor.getColumnIndex("phone_number")));
				} while(cursor.moveToNext());
			}
		}

		return contactList;
	}

	// get tracked friends emails list
	public List<String> getTrackedNumbers() {

		List<String> contactList = new ArrayList<String>();

		String query = "SELECT phone_number FROM contacts WHERE tracked = 'T'";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);		
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {
				do{									
					contactList.add(cursor.getString(cursor.getColumnIndex("phone_number")));
				} while(cursor.moveToNext());
			}
		}
		return contactList;
	}

	// get tracked friends list
	public ArrayList<Contact> getTrackedList() {

		ArrayList<Contact> contactList = new ArrayList<Contact>();

		String query = "SELECT * FROM contacts WHERE tracked = 'T'";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);		
		if (cursor != null) {        	
			if (cursor.moveToFirst()) {
				do{
					Contact contact = new Contact();
					contact.setEmailAddress(cursor.getString(cursor.getColumnIndex("email_address")));
					contact.setFirstName(cursor.getString(cursor.getColumnIndex("first_name")));
					contact.setLastName(cursor.getString(cursor.getColumnIndex("last_name")));
					contact.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phone_number"))); 
					contact.setLatitiude((cursor.getString(cursor.getColumnIndex("latitude"))));
					contact.setLongitude((cursor.getString(cursor.getColumnIndex("longitude"))));
					contact.setTracked((cursor.getString(cursor.getColumnIndex("tracked"))));
					byte[] bais = cursor.getBlob(cursor.getColumnIndex("profile_picture"));				
					contact.setImage((bais));
					contactList.add(contact);
				} while(cursor.moveToNext());
			}
		}

		return contactList;
	}


	// toggle tracked friends
	public void setTracked(String tracked, String phone){
		String query = "UPDATE contacts SET tracked = ? WHERE phone_number = ?";
		SQLiteDatabase database = this.getReadableDatabase();
		database.execSQL(query, new String[]{tracked, phone}); 
	}

	// update profile settings
	public void updateProfile(Contact contact, String phone){
		String query = "UPDATE profile SET first_name =?, last_name = ?, user_name = ?, phone_number = ?, email_address = ?, password = ? WHERE phone_number = ?";
		SQLiteDatabase database = this.getReadableDatabase();
		database.execSQL(query, new String[] {contact.getFirstName(),contact.getLastName(), contact.getUserName(), contact.getPhoneNumber(), contact.getEmailAddress(), contact.getPassword(), phone}); 
	}

	// Updating single contact
	public int updateContactData(Contact contact, String phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();	
		values.put("first_name", contact.getFirstName());
		values.put("last_name", contact.getLastName());
		values.put("user_name", contact.getUserName());
		values.put("phone_number", contact.getPhoneNumber());
		values.put("email_address", contact.getEmailAddress());
		values.put("time_stamp",contact.getTime_stamp());
		values.put("profile_picture", contact.getImage());
		// updating row
		return db.update("contacts", values, "phone_number" + " = ?",
				new String[] { phone });
	}


	// Updating single contact
	public int updateProfileImage(byte[] contactImage, String phone) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();	
		values.put("profile_picture", contactImage);
		// updating row
		return db.update("profile", values, "phone_number" + " = ?",
				new String[] { phone });
	}


	// delete contact
	public int deleteContact(String phoneNumber) {
		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("contacts", "phone_number" + " = ?", new String[] { phoneNumber });
	}

	// get list of events
	public ArrayList<Event> getEventsList(String phone) {

		ArrayList<Event> eventsList = new ArrayList<Event>();

		String query = "SELECT * FROM events WHERE organizer_phone = ?";
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor cursor = database.rawQuery(query, new String[]{phone});

		if (cursor != null) {        	
			if (cursor.moveToFirst()) {

				do {

					Event event = new Event();
					event.setEventName(cursor.getString(cursor.getColumnIndex("event_name")));
					event.setEventId(cursor.getString(cursor.getColumnIndex("event_id")));
					event.setOrganizer(cursor.getString(cursor.getColumnIndex("event_organizer")));
					event.setLocation(cursor.getString(cursor.getColumnIndex("location_name")));        
					event.setAddress(cursor.getString(cursor.getColumnIndex("address")));        
					event.setDate(cursor.getString(cursor.getColumnIndex("date")));        
					event.setTime(cursor.getString(cursor.getColumnIndex("time")));        
					event.setAttending(cursor.getString(cursor.getColumnIndex("attending")));  
					eventsList.add(event);
				}while(cursor.moveToNext());
			}
		}		

		return eventsList;
	}

	// add events
	public long addEvent(Event event, String phone) {

		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();									
		values.put("event_name", event.getEventName());
		values.put("event_id", event.getEventId());
		values.put("event_organizer", event.getOrganizer());
		values.put("organizer_phone", phone);
		values.put("location_name", event.getLocation());        
		values.put("address", event.getAddress());        
		values.put("date", event.getDate());        
		values.put("time", event.getTime());        
		values.put("attending", "A");        
		return database.insert("events", null, values);					
	}

	// Edit an Event
	public int deleteAnEvent(String id) {

		SQLiteDatabase db = this.getWritableDatabase();
		return db.delete("events", "event_id=?", new String[]{id});
	}

	// Update an Event
	public int updateAnEvent(String id, Event event) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();									
		values.put("event_name", event.getEventName());				
		values.put("location_name", event.getLocation());        
		values.put("address", event.getAddress());        
		values.put("date", event.getDate());        
		values.put("time", event.getTime()); 	     	 
		return db.update("events", values, "event_id = ?", new String[]{id});
	}

	public int updateShowingFriends(String show, String phone) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();									
		values.put("showing_friends", show);				

		return db.update("profile", values, "phone_number = ?", new String[]{phone});
	}

	public int updateShowingOthers(String show, String phone) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();									
		values.put("showing_others", show);				

		return db.update("profile", values, "phone_number = ?", new String[]{phone});
	}
}
