package android.app1.findfriends;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

public class Contacts extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();

		TabHost.TabSpec spec=tabs.newTabSpec("tag1");
		spec.setContent(R.id.contactList);
		spec.setIndicator("Contacts");
		tabs.addTab(spec);

		spec=tabs.newTabSpec("tag2");
		spec.setContent(R.id.trackedContactList);
		spec.setIndicator("Tracked Contacts");
		tabs.addTab(spec);
		tabs.setCurrentTab(getIntent().getIntExtra("tab", 0));

		registerForContextMenu((ListView)findViewById(R.id.trackedContactList));
		registerForContextMenu((ListView)findViewById(R.id.contactList));

		this.displayContact(tabs);
		this.displayTrackedContact(tabs);
	}

	// Display contacts	
	private void displayContact(final TabHost tab) {

		InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(this, null, 1);
		final ArrayList<Contact> contactsArray = databaseHelper.getContactList(null);
		databaseHelper.close();
		ListView listView = (ListView) findViewById(R.id.contactList);
		listView.setAdapter(new CustomArrayAdaptor(this, R.layout.contactlayout, contactsArray));

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {
				contactsAction(contactsArray, selection, tab);
			}
		});
	}


	/** 
	 * show a popup to choose SMS or TRACK
	 * 
	 */
	private void contactsAction(final ArrayList<Contact> contactsArray, final int selection, final TabHost tab){
		final Dialog dialog = new Dialog(Contacts.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.contact_action_layout);
		dialog.show();
		TextView sms = (TextView) dialog.findViewById(R.id.sms);
		sms.setText("SMS");
		final TextView track = (TextView) dialog.findViewById(R.id.track);
		sms.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Contacts.this, SMS.class);
				intent.putExtra("name", contactsArray.get(selection).getFirstName() + " " + contactsArray.get(selection).getLastName());
				intent.putExtra("phoneNumber", contactsArray.get(selection).getPhoneNumber());
				dialog.dismiss();
				startActivity(intent); 
			}			
		});


		if(contactsArray.get(selection).getTracked().equalsIgnoreCase("F")){			
			track.setText("Track");
		}
		else{			
			track.setText("UnTrack");					
		}
		track.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(Contacts.this, null, 1);
				// Get your contacts from the local DB and check, and alter the tracked status
				// IE if a friend is tracked set them to not tracked and vice versa
				// final Contact contact = databaseHelper.getContact(marker.getTitle().toString());

				if(contactsArray.get(selection).getTracked().equalsIgnoreCase("F")){
					databaseHelper.setTracked("T", contactsArray.get(selection).getPhoneNumber());
					Toast.makeText(Contacts.this, "Now Tracking " + contactsArray.get(selection).getFirstName(), Toast.LENGTH_SHORT).show();// display toast
					track.setText("UnTrack");
				}
				else{
					databaseHelper.setTracked("F", contactsArray.get(selection).getPhoneNumber());
					Toast.makeText(Contacts.this, "No longer Tracking " + contactsArray.get(selection).getFirstName(), Toast.LENGTH_SHORT).show();// display toast
					track.setText("Track");					
				}
				databaseHelper.close();
				dialog.dismiss();
				startActivity(new Intent(Contacts.this, Contacts.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra("tab", tab.getCurrentTab())
				.putExtra("profileData", getIntent().getBundleExtra("profileData")));
			}			
		});
	}



	// Display contacts	
	private void displayTrackedContact(final TabHost tab) {

		InternalDatabaseHelper databaseHelper = new InternalDatabaseHelper(this, null, 1);
		final ArrayList<Contact> contactsArray = databaseHelper.getTrackedList();
		databaseHelper.close();
		ListView listView = (ListView) findViewById(R.id.trackedContactList);
		listView.setAdapter(new CustomArrayAdaptor(this, R.layout.contactlayout, contactsArray));

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {
				contactsAction(contactsArray, selection, tab);  
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.contact_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Object contact = ((ListView) findViewById(R.id.contactList)).getAdapter().getItem(info.position);
		switch (item.getItemId()) {
		case R.id.toggletracked:
			InternalDatabaseHelper dh = new InternalDatabaseHelper(Contacts.this, null, 1);
			if(((Contact)contact).getTracked().equals("F"))
				dh.setTracked("T", ((Contact)contact).getPhoneNumber());
			else
				dh.setTracked("F", ((Contact)contact).getPhoneNumber());

			dh.close();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
