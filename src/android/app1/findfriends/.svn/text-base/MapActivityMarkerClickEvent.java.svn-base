package android.app1.findfriends;

import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapActivityMarkerClickEvent {
	private GoogleMap map;	
	private int[] markerSize;
	private PopupWindow popupWindow;
	private Context context;
	private Intent intent;	



	public MapActivityMarkerClickEvent(Context context, int[] markerSize, GoogleMap map, Intent intent, PopupWindow popupWindow){
		this.context = context;
		this.markerSize = markerSize;
		this.map = map;
		this.intent = intent;
		this.popupWindow = popupWindow;
	}








	// called when a marker is clicked this does the action that shows a pop up when certain markers are clicked	
	public void markerClicked(final Marker marker, final View popupView){
		String phone = intent.getBundleExtra("profileData").get("phone").toString().replaceAll("[^\\d]", "");
		if(!marker.getTitle().replaceAll("[^\\d]", "").equals(phone) && !marker.getTitle().equals("MYEVENT")){			

			// get the clicked marker location on the screen
			Projection projection = map.getProjection();               
			LatLng markerLocation = marker.getPosition();
			Point screenPosition = projection.toScreenLocation(markerLocation);			

			// allow the pop up window to appear off screen if needed
			popupWindow.setClippingEnabled(false);
			popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);  

			// shows the Map Marker popup window if its hidden or hides it if its in view
			if(!popupWindow.isShowing()){
				//popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, (int) (screenPosition.x - markerSize[0]+150), (int) (screenPosition.y - markerSize[1]-40));
				popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, screenPosition.x, (int) (screenPosition.y - markerSize[1]));
				this.cameraChangeEvent(marker, popupView);          

			}// end if

			else{
				popupWindow.dismiss();
				//popupShowing = false;            	   
			}         

			// once the pop up window is up there are 1 or 3 buttons, the actions are determined here
			popUpWindowButtonsAction(marker, popupView); 
		}

	}

	private void cameraChangeEvent(final Marker marker, final View popupView){
		// map camera change event listener this tries to keep the marker pop up on the marker 
		map.setOnCameraChangeListener(new OnCameraChangeListener(){
			@Override
			public void onCameraChange(CameraPosition position) {
				if(popupWindow.isShowing()){
					Projection projection = map.getProjection();   		               
					LatLng markerLocation = marker.getPosition();
					Point screenPosition = projection.toScreenLocation(markerLocation);
					popupWindow.dismiss(); 		
					popupWindow.showAtLocation(popupView, Gravity.NO_GRAVITY, screenPosition.x, (int) (screenPosition.y - markerSize[1]));
				}
			}           	   
		}); // end camera listener
	}

	/**
	 * Click listener actions for the buttons in the marker popup windows
	 * SMS, Tracking, Chat
	 */
	private void popUpWindowButtonsAction(final Marker marker, View popupView){
		// set up buttons for the popup

		final TextView sms = (TextView) popupView.findViewById(R.id.sms);
		final TextView track = (TextView) popupView.findViewById(R.id.track);
		final TextView chat = (TextView) popupView.findViewById(R.id.chat);
		sms.setVisibility(View.VISIBLE);
		track.setVisibility(View.VISIBLE);
		chat.setVisibility(View.VISIBLE);

		// initialize the text on the track button

		final InternalDatabaseHelper dbhelp = new InternalDatabaseHelper(context, null, 1);
		Contact contact = dbhelp.getContact(marker.getTitle().toString());
		dbhelp.close();
		if(contact.getTracked().equalsIgnoreCase("T")){						
			track.setText("UnTrack");
		}
		else{						
			track.setText("Track");			
		}
		// hide the SMS and Track button if the clicked marker belongs to a Anonymous user
		if(marker.getSnippet().equalsIgnoreCase("Anynomous User")){
			sms.setVisibility(View.GONE);
			track.setVisibility(View.GONE);				
		}

		// Send SMS From Marker Action-------------------------------------------------------------------------------
		sms.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) { 

				InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(context, null, 1);
				final Contact contact = databaseHelper.getContact(marker.getTitle().toString());
				Toast.makeText(context, marker.getSnippet() + " SMS", Toast.LENGTH_SHORT).show();// display toast 
				databaseHelper.close();
				// create and set up the SMS Intent Action
				Intent intent = new Intent(context,SMS.class);
				intent.putExtra("name", contact.getFirstName() + " " + contact.getLastName());
				intent.putExtra("phoneNumber", contact.getPhoneNumber());
				popupWindow.dismiss();
				//popupShowing = false;
				context.startActivity(intent);
			}   
		});

		// Tracking Friends Action------------------------------------------------------------------------------- 
		track.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {   
				InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(context, null, 1);

				// Get your contacts from the local DB and check, and alter the tracked status
				// IE if a friend is tracked set them to not tracked and vice versa
				final Contact contact = databaseHelper.getContact(marker.getTitle().toString());

				if(contact.getTracked().equalsIgnoreCase("F")){
					databaseHelper.setTracked("T", contact.getPhoneNumber());
					Toast.makeText(context, "Now Tracking " + marker.getSnippet(), Toast.LENGTH_SHORT).show();// display toast
					track.setText("UnTrack");
				}
				else{
					databaseHelper.setTracked("F", contact.getPhoneNumber());
					Toast.makeText(context, "No longer Tracking " + marker.getSnippet(), Toast.LENGTH_SHORT).show();// display toast
					track.setText("Track");
					if(intent.getIntExtra("Map", 0) == 2){
						marker.remove();
					}
				}
				popupWindow.dismiss();
				//popupShowing = false;
				databaseHelper.close();				
			}     
		});

		// Chat Action not yet implemented-------------------------------------------------------------------------------
		chat.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {   					
				Toast.makeText(context, marker.getSnippet() + " Chat", Toast.LENGTH_SHORT).show();// display toast 
				popupWindow.dismiss();
				//popupShowing = false;
			}  
		});
	}

}
