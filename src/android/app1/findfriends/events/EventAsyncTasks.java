package android.app1.findfriends.events;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app1.findfriends.CustomArrayAdaptor;
import android.app1.findfriends.R;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EventAsyncTasks {

	private Context context;
	private String[] data;

	public EventAsyncTasks(Context context, String[] data) {
		this.context = context;
		this.data = data;
	}

	public void whoIsAttending(String[] data) {
		new WhoIsAttending().execute(data);
	}

	/**
	 * invite a user popup window set up
	 */
	public void inviteAContact(final String[] data, List<String> phones) {
		InternalDatabaseHelper db = new InternalDatabaseHelper(context, null, 1);
		final List<Contact> list = db.getContactList(phones);
		if(list.size() > 0){
			final Dialog dialog = new Dialog(context);
			dialog.setTitle("Select a Friend");			
			dialog.setContentView(R.layout.invite_popup_listview_layout);		
			dialog.show();
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			Point screenSize = new Point();
			display.getSize(screenSize);
			dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), WindowManager.LayoutParams.WRAP_CONTENT);	
			ListView listView = (ListView) dialog.findViewById(R.id.menuList);


			listView.setAdapter(new CustomArrayAdaptor(context,	R.layout.invite_contacts_custom_listview, list));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> listView, View view,
						int selection, long id) {

					new InviteFriendstoEvent().execute(data[7], list.get(selection).getPhoneNumber());
					dialog.dismiss();
				}
			});
		}
		else{
			Toast.makeText(context.getApplicationContext(), "There is not one you can invite", Toast.LENGTH_SHORT).show();
		}
		db.close();

	}


	public void notAttending(Bundle bundle, String[] data2) {
		new NotAttending().execute(bundle);
	}

	public void Attending(TextView attending, TextView notAttending) {
		new Attending().execute(new TextView[]{attending, notAttending});
	}

	public void delete(Bundle bundle) {
		new Delete().execute(bundle);
	}

	public void attending(){
		new AttendingPhones().execute();
	}


	/**
	 * get a list of attendees for an event and show them in a popup dialog
	 *
	 */
	private class WhoIsAttending extends AsyncTask<String[], Void, Void> {

		List<String> list = new ArrayList<String>();

		@Override
		protected Void doInBackground(String[]... params) {
			String[] data = params[0];
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			list = db.getAttendingList(data[7]);
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			if(!list.isEmpty()){
				Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
				Point screenSize = new Point();
				display.getSize(screenSize);
				final Dialog dialog = new Dialog(context);
				dialog.setTitle("Attendees");
				dialog.setContentView(R.layout.who_is_attending_layout);	
				dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), WindowManager.LayoutParams.WRAP_CONTENT);	
				dialog.show();

				ListView listView = (ListView) dialog.findViewById(R.id.whosAttendingList);

				// set up the list view for the maptypes
				listView.setAdapter(new ArrayAdapter<String>(context, R.layout.who_is_attending_layout, R.id.attendeeName, list));
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> listView, View view,
							int selection, long id) {

					}
				}); // list view click listener
			}
			else{
				Toast.makeText(context.getApplicationContext(), "No one attending", Toast.LENGTH_SHORT).show();
			}
		}		
	}


	/**
	 * set my attending status to not going for an event
	 *
	 */
	private class NotAttending extends AsyncTask<Bundle, Void, Void> {
		Bundle bundle;

		@Override
		protected Void doInBackground(Bundle... params) {
			bundle = params[0];
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			db.notAttending(data[7], bundle.getString("phone"));
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			context.startActivity(new Intent(context, EventsActivity.class)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			.putExtra("profileData", bundle)
			.putExtra("tab", 0));
		}
	}

	/**
	 * change my attending status to going for a given event
	 *
	 */
	private class Attending extends AsyncTask<TextView, Void, Void> {
		TextView attending;
		TextView notAttending;

		@Override
		protected Void doInBackground(TextView... params) {
			attending = params[0];
			notAttending = params[1];
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			db.changeAttendingStatus(data[7], "A");
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {	
			attending.setText("Yes");
			notAttending.setText("Not Attending");

		}
	}


	/**
	 * delete an event the user creates
	 *
	 */
	private class Delete extends AsyncTask<Bundle, Void, Void> {
		Bundle bundle;

		@Override
		protected Void doInBackground(Bundle... params) {
			bundle = params[0];
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			InternalDatabaseHelper ldb = new InternalDatabaseHelper(context, null, 1);
			ldb.deleteAnEvent(data[7]);
			db.deleteEvent(data[7]);
			ldb.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {

			context.startActivity(new Intent(context, EventsActivity.class)
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
			.putExtra("profileData", bundle)
			.putExtra("tab", 1));
		}
	}

	/**
	 *  Invite friends to an event
	 * 
	 */
	private class InviteFriendstoEvent extends AsyncTask<String, Void, Void> {

		List<Contact> list = new ArrayList<Contact>();

		@Override
		protected Void doInBackground(String... params) {
			String id = params[0];
			String phone = params[1];
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			db.inviteUser(id, phone);
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {

		}
	}

	/**
	 * get a list of attendee phones so we can filter out these people from the invite user list
	 *
	 */
	private class AttendingPhones extends AsyncTask<Void, Void, Void> {

		List<String> list = new ArrayList<String>();

		@Override
		protected Void doInBackground(Void... params) {
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			list = db.getAttendingPhones(data[7]);
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {
			inviteAContact(data, list);
		}
	}

}
