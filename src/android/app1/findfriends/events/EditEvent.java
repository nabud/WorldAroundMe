package android.app1.findfriends.events;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app1.findfriends.CommonUtility;
import android.app1.findfriends.R;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Event;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class EditEvent extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_event);
		showDatePickerDialog();
		showTimePickerDialog();
		EditEvents();

	}

	public void onResume(){
		super.onResume();
		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
	}


	/**
	 * Allow the user to edit his or her event first by showing the data already there
	 * then the user can alter the data and click the submit button and the changed data will be noted 
	 */
	private void EditEvents() {
		final Bundle bundle = getIntent().getBundleExtra("profileData");
		final String[] eventData = getIntent().getStringArrayExtra("eventData");
		final EditText eventName = (EditText) findViewById(R.id.eventname);
		final EditText locationName = (EditText) findViewById(R.id.eventlocationname);
		final EditText eventAddress = (EditText) findViewById(R.id.eventaddress);
		final TextView dateDisplay = (TextView) findViewById(R.id.date_format);
		final TextView timeDisplay = (TextView) findViewById(R.id.time_format);
		showData(eventData, eventName, locationName, eventAddress, dateDisplay, timeDisplay);
		Button createEvent = (Button) findViewById(R.id.addCreatedEventButton);
		createEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Event event = new Event();
				event.setEventName(eventName.getText().toString());
				event.setLocation(locationName.getText().toString());
				event.setAddress(eventAddress.getText().toString());
				event.setDate(dateDisplay.getText().toString());
				event.setTime(timeDisplay.getText().toString());
				event.setPhone(bundle.get("phone").toString());
				event.setEventId(eventData[7]);
				event.setOrganizer(bundle.get("firstName").toString() + " " + bundle.get("lastName"));
				CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
				new UpdateEvent().execute(event);

			}
		});
	}

	/**
	 * show the unedited event data	
	 */
	private void showData(String[] eventData, EditText eventName, EditText locationName, EditText eventAddress, TextView dateDisplay, TextView timeDisplay){
		eventName.setText(eventData[8]);
		locationName.setText(eventData[0]);
		eventAddress.setText(eventData[1]);
		dateDisplay.setText(eventData[2]);
		timeDisplay.setText(eventData[3]);
	}	

	private String padding_str(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}


	private void showDatePickerDialog() {

		Button changeDate = (Button) findViewById(R.id.pick_date);
		changeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

	}

	@SuppressLint("ValidFragment")
	private class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			Calendar calender = Calendar.getInstance();
			return new DatePickerDialog(getActivity(), this, calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DAY_OF_MONTH));
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {

			// get date in here
			String date = padding_str(month+1) + "-" + padding_str(day) + "-" + year;
			TextView dateDisplay = (TextView) findViewById(R.id.date_format);
			dateDisplay.setText(date);
		}
	}

	private void showTimePickerDialog() {

		Button changeTime = (Button) findViewById(R.id.pick_time);
		changeTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				DialogFragment newFragment = new TimePickerFragment();
				newFragment.show(getFragmentManager(), "timePicker");
			}
		});		
	}


	@SuppressLint("ValidFragment")
	private class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar calender = Calendar.getInstance();

			return new TimePickerDialog(getActivity(), this, calender.get(Calendar.HOUR), calender.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
		}

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String time = hourOfDay + ":" + padding_str(minute) + ":AM" ;
			if (hourOfDay > 12){				   
				time = (hourOfDay - 12) + ":" + padding_str(minute) + ":PM";			                   
			}		
			TextView timeDisplay = (TextView) findViewById(R.id.time_format);			
			timeDisplay.setText(time);			
		}		

	}




	/**
	 * Update the event data in both Databases
	 */
	private class UpdateEvent extends AsyncTask<Event, Void, Void> {
		@Override
		protected Void doInBackground(Event... event) {
			InternalDatabaseHelper dbhelper = new InternalDatabaseHelper(EditEvent.this, null, 1);
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();			
			// generate an event ID using phone number			
			db.updateAnEvent(event[0].getEventId(),event[0]);
			dbhelper.updateAnEvent(event[0].getEventId(), event[0]);
			dbhelper.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {

			CommonUtility.showProgress(false, EditEvent.this.getWindow().getDecorView(), R.id.progressBar);
			Intent intent = new Intent(EditEvent.this, EventsActivity.class);
			intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
			intent.putExtra("tab", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	} // get data async task

}
