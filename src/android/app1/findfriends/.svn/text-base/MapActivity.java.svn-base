package android.app1.findfriends;


import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * This is our main Map activity
 * shows the google map and adds 
 * our location and friends location
 *
 */
public class MapActivity extends Activity implements LocationListener{

	private GoogleMap map;
	private Marker myMarker;	
	protected Location location;
	private LatLng myGeoLocation;	
	private PopupWindow popupWindow;
	private LocationManager locationManager;	
	private List<MarkerOptions> myContacts;
	private List<MarkerOptions> users;
	private List<MarkerOptions> eventList;
	private List<String> closeUsers;
	private boolean active;
	private int[] markerSize = new int[2];
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		eventList = new ArrayList<MarkerOptions>();
		setContentView(R.layout.activity_map);	
		location = new Location(LOCATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);			
		this.myGeoLocation = this.getLastLocation();
		if(setUp()){		
			onClick_marker();
			CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
			runThread();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		setUp();


	}

	@Override
	protected void onPause() {
		super.onPause();
		active = false;
		//locationManager.removeUpdates(this);
	}

	private boolean setUp(){

		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 0, 0, this); 
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();	

		// make sure the map is not null
		if(map != null){
			// marker click event
			map.setTrafficEnabled(false);
			this.myGeoLocation = getLastLocation();


			// on map load go to the users location
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myGeoLocation,17);
			map.animateCamera(update);
			map.setMapType(2);
			active = true;
			return true;
		}
		return false;

		// go to this methods location to change what is displayed on the map

	}


	// Button to move map to "My" current location
	public void onClick_CurrentLocation(View v) {	
		if(myMarker != null){
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(),17);
			map.animateCamera(update);
		}
	}

	/**
	 * this will be the marker click listener shows a popup window above the maps marker
	 * The popup will contain 3 buttons: Chat, Track, SMS
	 */
	public void onClick_marker() {
		final LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);  
		final View popupView = layoutInflater.inflate(R.layout.friends_marker_popup, null);
		final View popupView2 = layoutInflater.inflate(R.layout.unknown_user_marker_popup, null);
		View markerLayout = layoutInflater.inflate(R.layout.custom_map_marker_layout, null);  
		//final TextView textview = (TextView) layoutInflater.inflate(R.layout.activity_map_marker, null).findViewById(R.id.username);
		popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// Map marker Click event
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) { 
				// if the marker is for a non friend change the layout
				if(marker.getSnippet().equalsIgnoreCase("Anynomous User")){						
					popupWindow.setContentView(popupView2);
					popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
					popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
					new MapActivityMarkerClickEvent(MapActivity.this, markerSize, map, getIntent(), popupWindow).markerClicked(marker, popupView2);
				}
				else{
					popupWindow.setContentView(popupView);
					popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
					popupWindow.setWidth(LayoutParams.WRAP_CONTENT);
					new MapActivityMarkerClickEvent(MapActivity.this, markerSize, map, getIntent(), popupWindow).markerClicked(marker, popupView);
				}				
				return true;
			}
		}); // marker click listener
	}	

	// Add Markers to map section--------------------------------------------------------------------------------

	// Update the users markers location
	private void updateMarker(LatLng mylatlng) {	

		if(myMarker != null)
			myMarker.remove();
		//get the users profile data from the local DB
		InternalDatabaseHelper dbhelper = new InternalDatabaseHelper(this,null,1);
		Contact contact = dbhelper.getUserProfile(getIntent().getBundleExtra("profileData").getString("email"));
		dbhelper.close();
		View view =((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_map_marker, null);
		ImageView image = (ImageView) view.findViewById(R.id.myimage);
		TextView text = (TextView) view.findViewById(R.id.profileusername);
		text.setText(contact.getFirstName() + " " + contact.getLastName());
		//if the user has a profile picture replace the generic one
		if(contact.getImage() != null){
			Bitmap bm = BitmapFactory.decodeByteArray(contact.getImage(), 0 ,contact.getImage().length);			
			image.setImageBitmap(bm);
		}

		// create the marker and place it on the map
		BitmapDescriptor bdf = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, view));		
		myMarker = map.addMarker(new MarkerOptions().position(mylatlng).title(getIntent().getBundleExtra("profileData").getString("phone"))	
				.snippet(getIntent().getBundleExtra("profileData").getString("firstName")+" " + getIntent().getBundleExtra("profileData").getString("lastName"))
				.icon(bdf)
				.anchor(0.5f, 1));		
	}


	/**
	 * 
	 * This will receive a list containing all current visible friends in the contact list
	 * and will create a map marker for each one friends name will be shown
	 */
	private void displayFriends(List<MarkerOptions> list){		
		int tracked = getIntent().getIntExtra("Map", 0);
		closeUsers = new ArrayList<String>();

		for(MarkerOptions marker : list){
			map.addMarker(marker);
			// check to see if friends are near by only if under tracked map
			if(tracked == 2){
				float[] result = new float[3];
				Location.distanceBetween(myMarker.getPosition().latitude, myMarker.getPosition().longitude, marker.getPosition().latitude, marker.getPosition().longitude, result);
				if(Math.round(((result[0]/1609.344)*100)/100) < 1){
					closeUsers.add(marker.getSnippet());
				}

			}
		}
	}

	/**
	 * 
	 * This will receive a list containing all visible Users in the Database
	 * and will create a map marker for each one since these users are not friends 
	 * there names will not be shown on the map
	 */
	private void displayOthers(List<MarkerOptions> list){		
		for(MarkerOptions marker : list){			
			map.addMarker(marker);
		}
	}
	//--------------------------------------------------------------------------------------------------------------------

	/** 
	 * Convert activity_map_marker to a bitmap this will be the png image for the marker that will be displayed on the map
	 * this method comes from here: http://www.nasc.fr/android/android-using-layout-as-custom-marker-on-google-map-api/
	 * the users profile image will need to be taken during registration. Contact images will come from other locations.
	 */
	public Bitmap createDrawableFromView(Context context, View view) {

		DisplayMetrics displayMetrics = new DisplayMetrics();		
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);		
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);	
		markerSize[0] = view.getMeasuredWidth();
		markerSize[1] = view.getMeasuredHeight();			
		view.draw(new Canvas(bitmap)); 
		return bitmap;
	}


	/**
	 * Display a pop up window to allow the user to choose a map type.
	 * the types include: Normal, Satellite, Hybrid, Terrain. The user 
	 * also has the option to cancel changing the map type  
	 */
	public void onClick_MapTypesButton(View view) {	

		Button button = (Button) findViewById(R.id.type);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Display display = getWindowManager().getDefaultDisplay();
				Point screenSize = new Point();
				display.getSize(screenSize);
				final Dialog dialog = new Dialog(MapActivity.this);
				dialog.setContentView(R.layout.activity_map_types_menu);
				dialog.setTitle("Choose a Map Type");
				dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), (int) (screenSize.y - (screenSize.y * 0.2)));
				dialog.show();

				// set up the list view for the maptypes 
				final String[] mapTypesList = {"Traffic", "Normal", "Satallite", "Terrain", "Hybrid","Cancel"};
				ListView listView = (ListView)dialog.findViewById(R.id.mapTypesListView);       				
				listView.setAdapter(new ArrayAdapter<String>(MapActivity.this, R.layout.activity_map_types_menu, R.id.text, mapTypesList));                       

				listView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {
						// Traffic View
						if(selection == 0){
							if(map.isTrafficEnabled()){
								map.setTrafficEnabled(false);
								Toast.makeText(getApplicationContext(), "Traffic View Disabled", Toast.LENGTH_SHORT).show();
							}
							else{
								map.setTrafficEnabled(true);
								Toast.makeText(getApplicationContext(), "Traffic View Enabled", Toast.LENGTH_SHORT).show();
							}
							dialog.dismiss(); 
						}

						// Cancel
						else if(selection == 5){
							dialog.dismiss();                	  
						} 

						// Map Types
						else{
							Toast.makeText(getApplicationContext(), mapTypesList[selection], Toast.LENGTH_SHORT).show();
							map.setMapType(selection);
							dialog.dismiss();
						}                  	
					}
				}); // list view click listener
			}
		}); // map types button listener
	}		


	/**	 
	 * if whos around is selected(selection == 0) then all users who are not hidden will be displayed
	 * if Find friends is selected(selection == 1) then only visible users in the profile users contact list will be displayed
	 * if track friends is selected(selection == 2) then only those friends that are tracked and visible will be shown
	 */
	private void displayMapMarkers(int selection){	
		//myGeoLocation = getLocation();
		updateMarker(myGeoLocation);
		//InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(this, null, 1);
		AsyncTask task1 = null;
		AsyncTask task2 = null;
		myContacts = new ArrayList<MarkerOptions>();  // friends list
		users = new ArrayList<MarkerOptions>(); // other users list
		switch(selection){
		// display all non hidden app users including friends
		case 0:

			//Toast.makeText(getApplicationContext(), "Who's around", Toast.LENGTH_SHORT).show();
			task1 = new GetFriends().execute("");
			task2 = new GetUsersFromExternalDB().execute();	
			asyncListener(task1, task2);
			break;

			// display all visible friends only	
		case 1:

			//Toast.makeText(getApplicationContext(), "My Friends Location", Toast.LENGTH_SHORT).show();
			task1 = new GetFriends().execute("");
			asyncListener(task1, null);
			break;

			//display tracked and visible friends only	
		case 2:

			//Toast.makeText(getApplicationContext(), "My Tracked Friends", Toast.LENGTH_SHORT).show();
			task1 = new GetFriends().execute("tracked");
			asyncListener(task1, null);
			break;

			// display events if we choose to do this
		case 3:
			break;
		}
		//databaseHelper.close();
	}


	private void sendNotification(String str){
		//Intent intent = new Intent(MapActivity.this, NotificationReceiver.class);
		Intent intent = new Intent();
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		Notification mNotification = new Notification.Builder(this)
		.setContentTitle("Tracked Friends Alert")	
		.setContentText("")		
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentIntent(pIntent)
		.setStyle(new Notification.BigTextStyle().bigText(str))		
		.setDefaults(Notification.DEFAULT_ALL)
		.build();

		NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(0, mNotification);
	}


	/**
	 * spawn a thread to listen for the completion of async task that download location data from the database
	 * once all those tasks finish remove markers from the map and replace them with the updated ones
	 * @param task1
	 * @param task2
	 */
	private void asyncListener(final AsyncTask task1, final AsyncTask task2){
		final Timer t = new Timer();

		//Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {	
				//final List<LatLng> eventLocation = GetLatLngFromAddress.getLatLongFromAddress(("6205 Veterans Memorial Blvd Metaire, LA 70003 ").replace(" ", "+"));
				runOnUiThread(new Runnable() {
					@Override
					public void run() {			  
						// if who's around is chosen
						if(getIntent().getIntExtra("Map", 0) == 0 && active){
							if(task1.getStatus() == AsyncTask.Status.FINISHED && task2.getStatus() == AsyncTask.Status.FINISHED){
								map.clear();									
								updateMarker(myGeoLocation);
								//Toast.makeText(getApplicationContext(), "LAT: " + location.getLatitude() + "\n" + "LONG: " + location.getLongitude() , Toast.LENGTH_SHORT).show();
								displayFriends(myContacts);
								displayOthers(users);
								CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);								
								t.cancel();
							}														
						}

						// if friends finder or track friends is chosen
						else if(getIntent().getIntExtra("Map", 0) != 0 && active){
							if(task1.getStatus() == AsyncTask.Status.FINISHED){
								map.clear();	
								//myGeoLocation = getLocation();
								updateMarker(myGeoLocation);
								//Toast.makeText(getApplicationContext(), "LAT: " + myGeoLocation.latitude + "\n" + "LONG: " + myGeoLocation.longitude , Toast.LENGTH_SHORT).show();								
								displayFriends(myContacts);	
								CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);

								if(closeUsers.size() > 0){
									// notification
									StringBuilder str = new StringBuilder();	
									str.append("These friends are within a mile of you: \n");
									for(int i = 0; i < closeUsers.size()-1; i++){
										str.append(closeUsers.get(i) + "\n");
									}
									str.append(closeUsers.get(closeUsers.size()-1));
									Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();	

									sendNotification(str.toString());
								}
								t.cancel();
							}

						}
						else if(!active){
							t.cancel();
						}

					}					
				});
			}
		},0, 1000);		
	}






	// Get Update Data From DB on an X interval of time
	private void runThread(){
		//Declare the timer
		final Timer t = new Timer();
		//Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// send users location data to the database
				Contact contact = new Contact();
				contact.setLatitiude(Double.toString(myGeoLocation.latitude));
				contact.setLongitude(Double.toString(myGeoLocation.longitude));

				//send location data to the DB
				new ExternalDatabaseHelper().updateLocationData(contact, getIntent().getBundleExtra("profileData").getString("phone"));
				if(!active){
					t.cancel();
				}

				// We must use this function in order to change the Map markers, since non UI threads can't change the UI
				runOnUiThread(new Runnable() {

					@Override
					public void run() {			    	    	
						displayMapMarkers(getIntent().getIntExtra("Map", 0));		    	    	
					}		    	     
				});
			}
		},
		//Set how long before to start calling the TimerTask (in milliseconds)
		0,
		//Set the amount of time between each execution (in milliseconds)
		60*1000);				
	}

	// LOCATION LISTENER METHODS-------------------------------------------------------------------------------

	@Override
	public void onLocationChanged(Location loc) {
		MapActivity.this.location = loc;	
		myGeoLocation = new LatLng(loc.getLatitude(),loc.getLongitude());		

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Criteria criteria = new Criteria();
		locationManager.removeUpdates(this);
		provider = locationManager.getBestProvider(criteria, false);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
	}

	// ASYNC TASKS -------------------------------------------------------------------------------------------

	/**
	 * Get a list of all the visible users in the External DB
	 */
	private class GetUsersFromExternalDB extends AsyncTask<Void, Void, Void> {		
		List<MarkerOptions> userMarker = new ArrayList<MarkerOptions>();
		InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(MapActivity.this, null, 1);	
		@Override
		protected Void doInBackground(Void... params) {
			List<Contact> allUsers = new ExternalDatabaseHelper().getUsers(getIntent().getBundleExtra("profileData").getString("phone"), databaseHelper.getContactNumbers());			
			databaseHelper.close();
			makeMarkers(allUsers);
			return null;
		}


		private void makeMarkers(List<Contact> list){		
			View view =((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_map_marker, null);			
			LatLng latlng;


			for(Contact unknownContact : list){
				TextView userName = (TextView) view.findViewById(R.id.profileusername);				
				userName.setText(unknownContact.getUserName().toString());		        
				latlng = new LatLng(Double.parseDouble(unknownContact.getLatitiude()),Double.parseDouble(unknownContact.getLongitude()));			
				userMarker.add(new MarkerOptions()
				.position(latlng)
				.title(unknownContact.getPhoneNumber())
				.snippet("Anynomous User")
				.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, view)))
				.anchor(0.5f, 1));
			}
		}

		@Override
		protected void onPostExecute(Void args) {  		
			users = userMarker;
		}			
	} // get data async task


	/**
	 * Get a list of all the visible friends or visible tracked in the External DB by using the local DB to 
	 * filter out the anonymous users 
	 */
	private class GetFriends extends AsyncTask<String, Void, Void> {		
		List<MarkerOptions> friendsMarker = new ArrayList<MarkerOptions>();
		@Override
		protected Void doInBackground(String... params) {
			List<Contact> contacts;
			ExternalDatabaseHelper externalDatabaseHelper = new ExternalDatabaseHelper();
			InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(MapActivity.this, null, 1);	

			// get the tracked friends
			if(params[0].equalsIgnoreCase("tracked")){
				contacts = externalDatabaseHelper.getVisibleFriends(databaseHelper.getTrackedNumbers());
			}

			// get all friends
			else{
				contacts = externalDatabaseHelper.getVisibleFriends(databaseHelper.getContactNumbers());	
			}
			databaseHelper.close();
			makeMarkers(contacts);
			return null;
		}


		private void makeMarkers(List<Contact> contacts){
			// create the markers and add them to a list
			View view =((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.activity_map_marker, null);		
			LatLng latlng;
			ImageView image = (ImageView) view.findViewById(R.id.myimage);

			for(Contact contact : contacts){
				TextView text = (TextView) view.findViewById(R.id.profileusername);
				text.setText(contact.getFirstName());
				//check to see if the contact has a profile image
				if(contact.getImage() != null){
					Bitmap bm = BitmapFactory.decodeByteArray(contact.getImage(), 0 ,contact.getImage().length);			
					image.setImageBitmap(bm);
				}

				// create and place the marker on the map
				BitmapDescriptor bdf = BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MapActivity.this, view));
				latlng = new LatLng(Double.parseDouble(contact.getLatitiude()),Double.parseDouble(contact.getLongitude()));
				friendsMarker.add(new MarkerOptions()
				.position(latlng)
				.title(contact.getPhoneNumber())
				.snippet(contact.getFirstName() + " " + contact.getLastName())
				.icon(bdf)
				.anchor(0.5f, 1));
			}
		}

		@Override
		protected void onPostExecute(Void args) {     
			myContacts = friendsMarker;
		}			
	} // get data async task



	/** 
	 * get the last known location if there is one
	 * and place the marker there when the map opens
	 * return a location of 0.0, 0.0 if no location exists 
	 */
	public LatLng getLastLocation() {
		double lat = 0;
		double lng = 0;
		try {
			Criteria criteria = new Criteria();
			String provider = locationManager.getBestProvider(criteria, false);
			locationManager.requestLocationUpdates(provider, 0, 0, this);
			location = locationManager.getLastKnownLocation(provider);
			if(location != null){
				lat = location.getLatitude();
				lng = location.getLongitude();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new LatLng(lat,lng);
	}
}
