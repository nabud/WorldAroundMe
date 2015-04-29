package android.app1.findfriends;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import android.os.UserManager.*;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);		

		this.loginButtonAction();
		this.moveTORegistrationPage();
		this.moveToForgotPassword();
	}

	public void onResume(){
		super.onResume();
		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
	}

	private void loginButtonAction(){

		final EditText userName = (EditText)findViewById(R.id.usernameField);
		final EditText password = (EditText)findViewById(R.id.passwordField);


		Button loginButton = (Button)findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
				validate(userName, password);
			}
		});

		// Use an Async task to access the external DB since Network operations cant be done on the main thread 
		new ImportContacts().execute();
	}

	/*
	 * Validate if user entered email address and password
	 * Validate if user has a profile in the app
	 */
	@SuppressLint("ShowToast")
	private void validate(EditText userName, EditText password) {

		if (!TextUtils.isEmpty(userName.getText()) && userName.getText().toString().contains("@") && !TextUtils.isEmpty(password.getText())) {			

			InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(this, null, 1);
			Contact contact = databaseHelper.getUserProfile(userName.getText().toString());
			databaseHelper.close();			
			// check if user entered his email address and password
			// check if user's email address and password exist in the database
			if (contact != null && contact.getEmailAddress() != null && contact.getEmailAddress() != null &&
					contact.getEmailAddress().equalsIgnoreCase(userName.getText().toString()) &&
					contact.getPassword().equals(password.getText().toString())) {				
				startActivity(getIntent(contact));
			} else {
				CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
				Toast.makeText(Login.this, "Account does not exist", Toast.LENGTH_LONG).show();				
			}
		} 

		if (TextUtils.isEmpty(userName.getText()) || !userName.getText().toString().contains("@")) {

			userName.setError(getString(R.string.username_error));
			userName.requestFocus();
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
		} 

		if (TextUtils.isEmpty(password.getText())) {

			password.setError(getString(R.string.password_error));
			password.requestFocus();
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
		}
	}

	private Intent getIntent(Contact contact){

		Bundle bundle = new Bundle();
		bundle.putString("firstName", contact.getFirstName());
		bundle.putString("lastName", contact.getLastName());
		bundle.putString("email", contact.getEmailAddress());
		bundle.putString("phone", contact.getPhoneNumber());
		Intent intent = new Intent(Login.this, Main.class);
		intent.putExtra("profileData", bundle);
		return intent;
	}

	// Move to registration page
	private void moveTORegistrationPage() {

		final TextView registration = (TextView) findViewById(R.id.link_to_register);
		registration.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				registration.setPaintFlags(registration.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
				startActivity(new Intent(Login.this, Registration.class));

			}
		});
	}

	// Move to Forgot Password
	private void moveToForgotPassword() {

		final TextView forgotPassword = (TextView)findViewById(R.id.link_to_forgotPassword);
		forgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				forgotPassword.setPaintFlags(forgotPassword.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
				startActivity(new Intent(Login.this, ForgotPassword.class));				
			}
		});
	}



	// Import phone contacts that already registered with our app. 
	private void importPhoneContacts() {
		Contact externalContact = new Contact();
		Contact internalContact = new Contact();
		HashMap<String,String> phoneContacts; // <phone number, name>

		ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		phoneContacts = GetPhonesContacts.getContacts(cr, cur);


		cur.close();

		// Check to see if the phone's contact list is not empty
		if (!phoneContacts.isEmpty()) {

			// get internal DB contacts
			ArrayList<Contact> internalContacts = new ArrayList<Contact>();
			InternalDatabaseHelper internalDbHelper = new InternalDatabaseHelper(this, null, 1);
			internalContacts = internalDbHelper.getContactList(null);

			// get external DB Users
			List<Contact> externalContacts = new ArrayList<Contact>();
			ExternalDatabaseHelper externalDbHelper = new ExternalDatabaseHelper();
			externalContacts = externalDbHelper.getAllUsers(phoneContacts.entrySet());

			// parse through the phones contacts  
			Iterator<HashMap.Entry<String,String>> myIterator = phoneContacts.entrySet().iterator();
			while (myIterator.hasNext()) {
				Map.Entry<String,String> entry = myIterator.next();	    	

				// check external DB to see if any of the phones contacts has registered with the app
				boolean alreadyExistsInExternal = false;
				for (int i=0; i<externalContacts.size(); i++) {	
					String s = entry.getKey().replaceAll("[^\\d]", "");
					String extern = externalContacts.get(i).getPhoneNumber().replaceAll("[^\\d]", "");					
					if (s.equals(extern)) {						
						alreadyExistsInExternal = true;
						externalContact = externalContacts.get(i);						
						break;
					}	
				}


				// check internal DB to see if the contact is already in the apps contact list
				boolean alreadyExistsInInternal = false;
				if (!internalContacts.isEmpty()) {
					for (int j=0; j<internalContacts.size(); j++) {
						String s = entry.getKey().replaceAll("[^\\d]", "");	
						String intern = internalContacts.get(j).getPhoneNumber().replaceAll("[^\\d]", "");
						if (s.equals(intern)) {
							alreadyExistsInInternal = true;
							internalContact = internalContacts.get(j);
							break;
						}
					}
				} 

				// CASE USER IN EXTERNAL
				if (alreadyExistsInExternal) {
					if (!alreadyExistsInInternal) {
						// if not in internal DB, add this contact to internal DB	    				
						// other attributes
						internalDbHelper.addContact(externalContact);
					}
					else {
						if(!internalContact.getTime_stamp().equals(externalContact.getTime_stamp())){
							internalDbHelper.updateContactData(externalContact, externalContact.getPhoneNumber());
							System.out.println("Contact data changed");
						}
					}
				}

				// CASE USER NOT IN EXTERNAL 
				else {
					// if not in external
					if (alreadyExistsInInternal) {
						System.out.println("No contact in external");
						internalDbHelper.deleteContact(internalContact.getPhoneNumber());
					}
				}
			}
			internalDbHelper.close();
		} 	    
	}
	/**
	 * Perform the import Contacts action
	 */
	private class ImportContacts extends AsyncTask<Void, Void, Void> {		
		@Override
		protected Void doInBackground(Void... params) {
			importPhoneContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {       

		}			
	} // get data async task
}
