package android.app1.findfriends;

import java.util.Calendar;

import android.app.Activity;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.app1.findfriends.models.Contact;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UpdateProfile extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_profile);
		Button update = (Button) findViewById(R.id.updatebutton);
		final EditText name = (EditText) findViewById(R.id.nameField);
		final EditText phone = (EditText) findViewById(R.id.phoneNumberField);
		final EditText userName = (EditText) findViewById(R.id.usernameField);
		final EditText email = (EditText) findViewById(R.id.emailField);
		final EditText password = (EditText) findViewById(R.id.passwordField);
		getProfileData(name, phone, userName, email, password);
		CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
		update.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				CommonUtility.showProgress(true, getWindow().getDecorView(), R.id.progressBar);
				Contact contact = getContact(name, phone, userName, email, password);
				new ChangeProfileData().execute(contact);
			}

		});
	}


	private void getProfileData(EditText name, EditText phone, EditText userName, EditText email, EditText password){
		InternalDatabaseHelper db = new InternalDatabaseHelper(this,null,1);
		Contact contact = db.getUserProfile(getIntent().getBundleExtra("profileData").getString("email"));
		name.setText(contact.getFirstName() +" " + contact.getLastName());
		phone.setText(contact.getPhoneNumber().replaceAll("[^\\d]", ""));
		userName.setText(contact.getUserName());
		email.setText(contact.getEmailAddress());
		password.setText(contact.getPassword());
		db.close();
	}

	private Contact getContact(EditText name, EditText phone, EditText userName, EditText email, EditText password){
		Contact contact = new Contact();

		String number = String.format("(%s) %s-%s", phone.getText().toString().substring(0, 3), 
				phone.getText().toString().substring(3, 6), phone.getText().toString().substring(6, 10));
		contact.setFirstName(name.getText().toString().split(" ")[0]);
		contact.setLastName(name.getText().toString().split(" ")[1]);
		contact.setUserName(userName.getText().toString());
		contact.setEmailAddress(email.getText().toString());
		contact.setPassword(password.getText().toString());
		contact.setPhoneNumber(number);
		return contact;
	}


	private class ChangeProfileData extends AsyncTask<Contact, Void, Void> {		

		@Override
		protected Void doInBackground(Contact... contact) {
			InternalDatabaseHelper ldb = new InternalDatabaseHelper(UpdateProfile.this,null,1);
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();		
			ldb.updateProfile(contact[0], getIntent().getBundleExtra("profileData").getString("phone"));
			System.out.println(getIntent().getBundleExtra("profileData").getString("phone"));
			db.updateUserProfile(contact[0], getIntent().getBundleExtra("profileData").getString("phone"),Calendar.getInstance().getTime().toString());
			db.updateUserEventProfile(contact[0], getIntent().getBundleExtra("profileData").getString("phone"));
			ldb.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {       			
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			startActivity(new Intent(UpdateProfile.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		}			
	} 

}
