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

public class CreateEvent extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);		
		showDatePickerDialog();
		showTimePickerDialog();
		CreateEvents();

	}

	public void onResume(){
		super.onResume();
		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
	}


	private void CreateEvents() {
		final Bundle bundle = getIntent().getBundleExtra("profileData");
		final EditText eventName = (EditText) findViewById(R.id.eventname);
		final EditText locationName = (EditText) findViewById(R.id.eventlocationname);
		final EditText eventAddress = (EditText) findViewById(R.id.eventaddress);
		final TextView dateDisplay = (TextView) findViewById(R.id.date_format);
		final TextView timeDisplay = (TextView) findViewById(R.id.time_format);
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
				event.setOrganizer(bundle.get("firstName").toString() + " " + bundle.get("lastName"));
				CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
				new AddEvents().execute(event);

			}
		});
	}

	private String padding_str(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public void showDatePickerDialog() {

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

	public void showTimePickerDialog() {

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


	private class AddEvents extends AsyncTask<Event, Void, Void> {
		@Override
		protected Void doInBackground(Event... event) {
			InternalDatabaseHelper dbhelper = new InternalDatabaseHelper(CreateEvent.this, null, 1);
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			int eventNumber = dbhelper.getEventsList(event[0].getPhone()).size();
			// generate an event ID using phonenumber
			String id = (getIntent().getBundleExtra("profileData").get("phone").toString()).replaceAll("[^\\d]", "");
			id = "E" + id  + "-"+ eventNumber + ((int) (Math.random() * 9));
			event[0].setEventId(id);
			db.insertEvent(event[0]);
			dbhelper.addEvent(event[0], event[0].getPhone());
			dbhelper.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {

			CommonUtility.showProgress(false, CreateEvent.this.getWindow().getDecorView(), R.id.progressBar);
			Intent intent = new Intent(CreateEvent.this, EventsActivity.class);
			intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
			intent.putExtra("tab", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		}
	} // get data async task

}
