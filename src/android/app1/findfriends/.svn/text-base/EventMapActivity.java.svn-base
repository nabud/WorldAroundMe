package android.app1.findfriends;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.events.EventMarkerInfoWindow;
import android.app1.findfriends.models.Contact;
import android.app1.findfriends.models.Event;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.directions.route.Routing;
import android.directions.route.Routing.TravelMode;
import android.directions.route.RoutingListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;


/**
 * This is our main Map activity
 * shows the google map and adds 
 * our location and friends location
 *
 */
public class EventMapActivity extends Activity implements LocationListener{

	private GoogleMap map;
	private Marker myMarker;	
	protected Location location;
	private LatLng myGeoLocation;	
	private LatLng destinationEvent;
	private LocationManager locationManager;	
	private List<MarkerOptions> eventList;	
	private Routing routing;
	private Polyline polyline;
	private int[] markerSize = new int[2];
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		eventList = new ArrayList<MarkerOptions>();
		setContentView(R.layout.activity_events_map);	
		location = new Location(LOCATION_SERVICE);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);				
		this.myGeoLocation = this.getLastLocation();
		if(setUp()){
			onClick_marker();
			onClick_MapTypesButton();
			CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
			this.updateMarker(myGeoLocation);
			// on map load go to the users location
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(),17);
			map.animateCamera(update);
			new GetEvents().execute();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		// cancel any notification we may have received from TestBroadcastReceiver
		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(1234);        


		// This demonstrates how to dynamically create a receiver to listen to the location updates.
		// You could also register a receiver in your manifest.
		final IntentFilter lftIntentFilter = new IntentFilter(LocationLibraryConstants.getLocationChangedPeriodicBroadcastAction());
		registerReceiver(lftBroadcastReceiver, lftIntentFilter);
		LocationInfo latestInfo = new LocationInfo(getBaseContext());
		System.out.println("Using Provider: ............................" + latestInfo.lastProvider);
		setUp();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(lftBroadcastReceiver);
		locationManager.removeUpdates(this);
	}


	private boolean setUp(){
		Criteria criteria = new Criteria();
		String provider = locationManager.getBestProvider(criteria, true);
		locationManager.requestLocationUpdates(provider, 0, 0, this); 
		routing = null;
		polyline = null;

		// get the fragment map from the xml layout make sure map is not null
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();		
		if(map != null){
			map.setTrafficEnabled(false);
			map.setMapType(4);
			return true;
		}
		return false;



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

		// Map marker Click event
		map.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) { 
				if(!marker.getSnippet().equals(myMarker.getSnippet())){
					map.setInfoWindowAdapter(new EventMarkerInfoWindow((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)));
					marker.showInfoWindow();
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
	public void onClick_MapTypesButton() {	

		Button button = (Button) findViewById(R.id.type);
		button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Display display = getWindowManager().getDefaultDisplay();
				Point screenSize = new Point();
				display.getSize(screenSize);
				final Dialog dialog = new Dialog(EventMapActivity.this);
				dialog.setContentView(R.layout.activity_map_types_menu);
				dialog.setTitle("Choose a Map Type");
				dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), (int) (screenSize.y - (screenSize.y * 0.2)));
				dialog.show();
				dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
				// set up the list view for the maptypes 
				final String[] mapTypesList = {"Traffic", "Normal", "Satallite", "Terrain", "Hybrid","Cancel"};
				ListView listView = (ListView)dialog.findViewById(R.id.mapTypesListView);       				
				listView.setAdapter(new ArrayAdapter<String>(EventMapActivity.this, R.layout.activity_map_types_menu, R.id.text, mapTypesList));                       

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







	// LOCATION LISTENER METHODS-------------------------------------------------------------------------------

	@Override
	public void onLocationChanged(Location loc) {
		EventMapActivity.this.location = loc;	
		myGeoLocation = new LatLng(loc.getLatitude(),loc.getLongitude());		
		//this.updateMarker(myGeoLocation);
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


	private class GetEvents extends AsyncTask<Void, Void, Void> {	

		List<Event> eventsMarker = new ArrayList<Event>();
		List<Event> myEvents = new ArrayList<Event>();
		@Override
		protected Void doInBackground(Void... params) {			
			ExternalDatabaseHelper externalDatabaseHelper = new ExternalDatabaseHelper();	
			InternalDatabaseHelper db = new InternalDatabaseHelper(EventMapActivity.this,null,1);
			// get the tracked friends
			eventsMarker = externalDatabaseHelper.getEvents(getIntent().getBundleExtra("profileData").getString("phone").toString(), true);
			myEvents = db.getEventsList(getIntent().getBundleExtra("profileData").getString("phone").toString());
			db.close();
			makeMarkers(eventsMarker);
			makeMarkers(myEvents);
			return null;
		}


		private void makeMarkers(List<Event> events){	

			LatLng latlng;          
			for(Event event : events){	
				try{
					latlng = new LatLng(0,0);
					latlng = GetLatLngFromAddress.getLatLongFromAddress(event.getAddress()).get(0);				
					String s = "Name:          " + event.getEventName() + "\n" +
							"Organizer:    " + event.getOrganizer() + "\n" +
							"Date:             " + event.getDate() + "\n" +
							"Time:            " + event.getTime();
					eventList.add(new MarkerOptions()
					.position(latlng)
					.title("MYEVENT")				
					.snippet(s)
					.anchor(0.5f, 1));
				}
				catch(Exception e){

				}

			}
		}

		@Override
		protected void onPostExecute(Void args) {     
			for(int i = 0; i < eventList.size();i++){				
				map.addMarker(eventList.get(i));
			}
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			Button button = (Button) findViewById(R.id.eventDirections);
			eventsMarker.addAll(myEvents);
			button.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if(eventsMarker.size() > 0){

						onClick_MapEvents(eventsMarker);
					}
					else {
						Toast.makeText(getApplicationContext(), "You currently have no active events that you are attending", Toast.LENGTH_SHORT).show();
					}

				}

			});
		} // get data async task


		public void onClick_MapEvents(final List<Event> list) {
			final Dialog travelDialog = travelDialog();
			final RadioButton bikeing = (RadioButton)travelDialog.findViewById(R.id.bike);
			final RadioButton driving = (RadioButton)travelDialog.findViewById(R.id.drive);
			final RadioButton walking = (RadioButton)travelDialog.findViewById(R.id.walk);

			TextView submit = (TextView
					) travelDialog.findViewById(R.id.travelmode);
			submit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {	
					// choose the eventRouting routing you want to travel to
					travelDialog.dismiss();
					chooseEvent(list, setMode(bikeing, driving, walking));					
				}

			});
		}

	}

	private Dialog travelDialog(){
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		Dialog dialog = new Dialog(EventMapActivity.this);
		dialog.setContentView(R.layout.choose_mode_of_travel_popup);
		dialog.setTitle("Travel Mode");
		dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.5)), WindowManager.LayoutParams.WRAP_CONTENT);	
		dialog.show();

		return dialog;
	}

	private Dialog eventDialog(){
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		Dialog dialog = new Dialog(EventMapActivity.this);		
		dialog.setTitle("Select an Event");
		dialog.setContentView(R.layout.invite_popup_listview_layout);
		dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), WindowManager.LayoutParams.WRAP_CONTENT);	
		//		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();

		return dialog;
	}

	private TravelMode setMode(RadioButton bikeing, RadioButton driving, RadioButton walking){
		TravelMode travel = null;
		if(bikeing.isChecked()){
			travel = Routing.TravelMode.BIKING;
		}
		else if(driving.isChecked()){
			travel = Routing.TravelMode.DRIVING;
		}
		else if(walking.isChecked()){
			travel = Routing.TravelMode.WALKING;
		}
		return travel;
	}


	private void chooseEvent(List<Event> list, final TravelMode travel) {
		final Dialog chooseEventDialog = eventDialog();
		ListView listView = (ListView) chooseEventDialog.findViewById(R.id.menuList);


		listView.setAdapter(new CustomEventArrayAdaptor(EventMapActivity.this,
				R.layout.invite_contacts_custom_listview, list));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view,	int selection, long id) {				
				destinationEvent = eventList.get(selection).getPosition();
				if(routing != null){
					routing.cancel(true);
				}
				routing = new Routing(travel);
				routing.registerListener(new MyRoutingListener());
				routing.execute(myMarker.getPosition(), destinationEvent);

				chooseEventDialog.dismiss();

			}
		});

	}


	class MyRoutingListener implements RoutingListener{

		@Override
		public void onRoutingFailure() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutingStart() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRoutingSuccess(PolylineOptions mPolyOptions) {
			PolylineOptions polyoptions = new PolylineOptions();
			polyoptions.color(Color.BLUE);
			polyoptions.width(5);
			polyoptions.addAll(mPolyOptions.getPoints());
			if(polyline != null){
				polyline.remove();
			}
			polyline = map.addPolyline(polyoptions);
			// Start marker
			MarkerOptions options = new MarkerOptions();
			options.position(myMarker.getPosition());
			options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
			map.addMarker(options);		
		}

	}

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


	private final BroadcastReceiver lftBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// extract the location info in the broadcast
			final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);
			// refresh the display with it
			updateMarker(new LatLng(locationInfo.lastLat,locationInfo.lastLong));
			System.out.println("Provider: " +locationInfo.lastProvider +"\nLAT: " + locationInfo.lastLat + "\nLNG" + locationInfo.lastLong);
		}
	};



}
