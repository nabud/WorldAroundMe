package android.app1.findfriends;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMS extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sms);

		Button sendSMSButton = (Button) findViewById(R.id.SendSMSButton);
		final EditText phoneNumberField = (EditText) findViewById(R.id.PhoneNumber);
		final EditText messageField = (EditText) findViewById(R.id.message);

		Intent intent = getIntent();
		final String contactName = intent.getStringExtra("name");
		final String contactPhoneNumber = intent.getStringExtra("phoneNumber");
		if(contactName != null){
			phoneNumberField.setText("To: "+contactName+" "+contactPhoneNumber);			
		}
		else{
			phoneNumberField.setFocusable(true);
			phoneNumberField.setFocusableInTouchMode(true);
		}

		sendSMSButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
				String message = messageField.getText().toString();
				if(contactName == null && (phoneNumberField.getText().length() > 0 && message.length() > 0)){                	
					sendSMS(phoneNumberField.getText().toString(), message);
				}
				else if (contactPhoneNumber.length() > 0 && message.length() > 0) {
					sendSMS(contactPhoneNumber, message);
				} 
				else {
					Toast.makeText(getBaseContext(), "Please enter both phone number and message.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	public void onResume(){
		super.onResume();
		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
	}
	
	// Sends a SMS message to another device
	private void sendSMS(String phoneNumber, String message) {

		final String SENT = "SMS_SENT";
		final String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

		// when the SMS has been sent
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode()) {
				case Activity.RESULT_OK:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
					break;

				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
					break;

				case SmsManager.RESULT_ERROR_NO_SERVICE:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
					break;

				case SmsManager.RESULT_ERROR_NULL_PDU:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
					break;

				case SmsManager.RESULT_ERROR_RADIO_OFF:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		// SMS has been delivered
		registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode()) {
				case Activity.RESULT_OK:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
					break;

				case Activity.RESULT_CANCELED:
					CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
					Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(DELIVERED));

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentIntent, deliveredIntent);
	}
}