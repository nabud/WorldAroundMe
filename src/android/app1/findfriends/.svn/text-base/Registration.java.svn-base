package android.app1.findfriends;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class Registration extends Activity {
	private EditText emailAddress; 
	private EditText phoneNumber;
	private Location location; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_registration);
		this.registerAction();
	}



	private void registerAction() {

		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
		final InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(this, null, 1);

		final EditText firstName = (EditText) findViewById(R.id.firstNameField);
		final EditText lastName = (EditText) findViewById(R.id.lastNameField);	
		emailAddress = (EditText) findViewById(R.id.emailField);
		final EditText userName = (EditText) findViewById(R.id.usernameField);
		final EditText password = (EditText) findViewById(R.id.passwordField);
		phoneNumber = (EditText) findViewById(R.id.phoneNumberField);		

		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);	    
		phoneNumber.setText(mTelephonyMgr.getLine1Number());

		Button button = (Button)findViewById(R.id.registerButton);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (validate(firstName,lastName, userName, emailAddress,password,phoneNumber)) {
					Contact contact = getContact(firstName,lastName,userName,emailAddress,password,phoneNumber);
					CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
					if (databaseHelper.addProfile(contact) == -1) {
						Toast.makeText(Registration.this, "Registration Failed", Toast.LENGTH_LONG).show();				
						CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
					} else {
						new InsertRegisteredUserToDB().execute(contact);
					}
					databaseHelper.close();
				}
			}
		});
	}

	// Set contact
	private Contact getContact(EditText firstName, EditText lastName, EditText userName, EditText emailAddress, EditText password, EditText phoneNumber) {


		Contact contact = new Contact();	
		contact.setFirstName(firstName.getText().toString());
		contact.setLastName(lastName.getText().toString());
		contact.setUserName(userName.getText().toString());
		contact.setEmailAddress(emailAddress.getText().toString());
		contact.setPassword(password.getText().toString());
		contact.setPhoneNumber(phoneNumber.getText().toString());		
		LatLng latlng = getLocation();		
		contact.setLatitiude(Double.toString(latlng.latitude));
		contact.setLongitude(Double.toString(latlng.longitude));
		return contact;
	}


	// get a latlng to start with when you register
	public LatLng getLocation() {
		// Get the location manager
		location = new Location(LOCATION_SERVICE);
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);						
		LocationListener locationListener = new MyLocationListener();	
		Criteria criteria = new Criteria();
		String bestProvider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(bestProvider, 0, 0, locationListener);		
		Location location = locationManager.getLastKnownLocation(bestProvider);
		Double lat,lon;
		try {
			lat = location.getLatitude ();
			lon = location.getLongitude ();
			return new LatLng(lat, lon);
		}
		catch (NullPointerException e){        
			return new LatLng(0.0, 0.0);
		}
	}

	// do some validation make sure data is entered in the text fields
	private boolean validate(EditText firstName, EditText lastName, EditText userName, EditText emailAddress, EditText password, EditText phoneNumber) {
		return this.sub_validate(firstName, R.string.firstName_error,"") &&
				this.sub_validate(lastName, R.string.lastName_error,"") &&
				this.sub_validate(userName, R.string.username_error, "") &&
				this.sub_validate(phoneNumber, R.string.phoneNumber_error,"phone") &&
				this.sub_validate(emailAddress, R.string.email_error,"email") &&
				this.sub_validate(password, R.string.password_error,"");

	}

	// do some validation make sure data is entered in the text fields
	private boolean sub_validate(EditText text, int id, String field) {

		boolean flag = true;
		if (text.getText().toString().isEmpty() || (field.equals("email") && !text.getText().toString().contains("@")) || 
				(field.equals("phone") && text.getText().length() != 10)) {
			text.setError(getString(id));
			text.requestFocus();
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			flag = false;
		}
		return flag;
	}


	// go take a profile picture 
	private void cameraOptions() {	
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		final Dialog dialog = new Dialog(Registration.this);
		dialog.setContentView(R.layout.activity_map_types_menu);
		dialog.setTitle("Profile Picture");
		dialog.getWindow().setLayout((int) (LayoutParams.WRAP_CONTENT), (int) (LayoutParams.WRAP_CONTENT));
		dialog.show();

		final String[] options = {"Take Picture", "Skip To Login"};
		ListView listView = (ListView)dialog.findViewById(R.id.mapTypesListView);              
		listView.setAdapter(new ArrayAdapter<String>(Registration.this, R.layout.activity_map_types_menu, R.id.text, options));                       

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {

				if (selection == 0) {
					dialog.dismiss();	
					String number = phoneNumber.getText().toString();
					number = String.format("(%s) %s-%s", number.substring(0, 3), 
							number.substring(3, 6), number.substring(6, 10));
					Intent intent = new Intent(Registration.this, Camera.class);
					intent.putExtra("isRegister", true);
					intent.putExtra("phone", number);
					startActivity(intent);
				} else {
					dialog.dismiss();
					Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.smily);
					CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
					new SendImageToDB().execute(b);							                			
				}
			}                  	
		}); 		
	}

	// add the user to the Database
	private class InsertRegisteredUserToDB extends AsyncTask<Contact, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Contact... params) {
			boolean result = new ExternalDatabaseHelper().insertUser(params[0],Calendar.getInstance().getTime().toString());			
			return result;
		}

		@Override
		protected void onPostExecute(Boolean args) {       
			// display the users on the map when doInBackground is done
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			cameraOptions();
		}	
	} // Register user async task



	// send the default image to the DB
	private class SendImageToDB extends AsyncTask<Bitmap, Void, Void> {
		@Override
		protected Void doInBackground(Bitmap... params) {				
			Bitmap b = params[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();			
			b.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			String number = phoneNumber.getText().toString();
			number = String.format("(%s) %s-%s", number.substring(0, 3), 
					number.substring(3, 6), number.substring(6, 10));
			new ExternalDatabaseHelper().insertImage(byteArray, number, Calendar.getInstance().getTime().toString());			
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {   			
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			startActivity(new Intent(Registration.this,Login.class));
		}	
	} // set image async task



	/*----------Listener class to get coordinates ------------- */
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			Registration.this.location = location;				
		}
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub        	
		}
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub        	
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub        	
		}
	}
}
