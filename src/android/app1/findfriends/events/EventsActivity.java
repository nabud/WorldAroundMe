package android.app1.findfriends.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app1.findfriends.CommonUtility;
import android.app1.findfriends.R;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Event;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class EventsActivity extends Activity {

	ExpandableListAdapter listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		TextView create = (TextView) findViewById(R.id.eventsbutton);

		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		spec.setContent(R.id.expandableEventsListView);
		spec.setIndicator("Events List");
		tabs.addTab(spec);

		spec=tabs.newTabSpec("tag2");
		spec.setContent(R.id.expandableMyEventsListView);
		spec.setIndicator("My Events");
		tabs.addTab(spec);
		tabs.setCurrentTab(getIntent().getIntExtra("tab", 0));

		CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
		new GetEvents().execute(getIntent().getBundleExtra("profileData").get("phone").toString());
		new GetMyEvents().execute(getIntent().getBundleExtra("profileData").get("phone").toString());
		showHideCreateEvents(tabs, create);
		createEvent(create, tabs);
	}

	private void showHideCreateEvents(final TabHost tabs, final TextView create){
		createVisibility(tabs, create);
		tabs.setOnTabChangedListener(new OnTabChangeListener(){

			@Override
			public void onTabChanged(String tabId) {
				createVisibility(tabs, create);				
			}

		});
	}

	// Display list events
	private void displayEvents(final List<Event> eventsArray, int viewId, String tab) {

		final ArrayList<String> eventsNames = new ArrayList<String>();		
		final HashMap<String, String[]> eventsMap = new HashMap<String, String[]>();
		for(Event event : eventsArray) {
			eventsNames.add(event.getEventName());
			eventsMap.put(event.getEventName(), new String[]{event.getLocation()+ "?"+ event.getAddress() + "?"+event.getDate()+ "?"+event.getTime()+ "?"+event.getAttending()+ "?"+event.getOrganizer() +"?"+event.getPhone() + "?" + event.getEventId() + "?" + event.getEventName() + "?" + tab});
		}

		ExpandableListView expListView = (ExpandableListView) findViewById(viewId);		
		expListView.setAdapter(new ExpandableListAdapter(this, eventsNames, eventsMap, getIntent().getBundleExtra("profileData")));        
	}

	// Create new Event
	private void createEvent(final TextView create, final TabHost tabs) {


		create.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				create.setPaintFlags(create.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
				Intent intent = new Intent(EventsActivity.this, CreateEvent.class);
				intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));				
				startActivity(intent);
			}
		});
	}



	// Get list events user is attending
	private class GetEvents extends AsyncTask<String, Void, Void> {		

		List<Event> eventsArray = new ArrayList<Event>();		

		@Override
		protected Void doInBackground(String... params) {			
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();			
			eventsArray = db.getEvents(params[0], false);	
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {     			
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			displayEvents(eventsArray, R.id.expandableEventsListView, "EVENTS");			
		}			
	} 

	// Get list events that the user created from phone's database
	private class GetMyEvents extends AsyncTask<String, Void, Void> {			

		List<Event> myEventsArray = new ArrayList<Event>();		
		InternalDatabaseHelper dbhelper = new InternalDatabaseHelper(EventsActivity.this,null,1);

		@Override
		protected Void doInBackground(String... params) {
			myEventsArray = dbhelper.getEventsList(params[0]);		
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {  
			dbhelper.close();
			displayEvents(myEventsArray, R.id.expandableMyEventsListView,"MYEVENTS");			
		}			
	}

	private void createVisibility(TabHost tabs, TextView create){
		if(tabs.getCurrentTab() == 1){
			create.setVisibility(View.VISIBLE);
		}
		else{
			create.setVisibility(View.GONE);
		}
	}

}
