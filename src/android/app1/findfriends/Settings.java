package android.app1.findfriends;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app1.findfriends.dao.ExternalDatabaseHelper;
import android.app1.findfriends.dao.InternalDatabaseHelper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class Settings extends Activity {
	Dialog dialog; 
	InternalDatabaseHelper ldb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		this.updateProfile();
		this.changePicture();
		this.toggleVisibiliy();
		this.logout();
	}

	private void updateProfile() {

		TextView profile = (TextView)findViewById(R.id.profile);
		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startActivity(new Intent(Settings.this, UpdateProfile.class)
				.putExtra("profileData", getIntent().getBundleExtra("profileData"))
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});
	}


	/**
	 * alter the showing state to friends or others Async task
	 */
	private void toggleVisibiliy(){
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		TextView invisible = (TextView) findViewById(R.id.invisible);		
		dialog = new Dialog(Settings.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.activity_visibility);		
		dialog.getWindow().setLayout((int) (screenSize.x - (screenSize.x * 0.2)), WindowManager.LayoutParams.WRAP_CONTENT);		
		invisible.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {				
				dialog.show();	
				//dialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				CommonUtility.showProgress(false, dialog.getWindow().getDecorView(), R.id.progressBar);
				final CheckBox friends = (CheckBox) dialog.findViewById(R.id.friendvisible);
				final CheckBox others = (CheckBox) dialog.findViewById(R.id.nonfriendvisible);
				ldb = new InternalDatabaseHelper(Settings.this, null, 1);	
				friends.setChecked(ldb.getUserProfile(getIntent().getBundleExtra("profileData").getString("email")).getShowing_friends().equalsIgnoreCase("T"));
				others.setChecked(ldb.getUserProfile(getIntent().getBundleExtra("profileData").getString("email")).getShowing_others().equalsIgnoreCase("T"));
				TextView submit = (TextView) dialog.findViewById(R.id.button);
				ldb.close();
				submit.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {		
						CommonUtility.showProgress(true, dialog.getWindow().getDecorView(), R.id.progressBar);
						new ChangeShowingState().execute(friends, others);							
					}					
				});				
			}
		});		
	}

	private void changePicture(){
		TextView picture = (TextView) findViewById(R.id.profilepicture);
		picture.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				cameraOptions();				
			}			
		});
	}


	/**
	 * alter the showing state to friends or others Async task
	 */
	private class ChangeShowingState extends AsyncTask<CheckBox, Void, Void> {		

		@Override
		protected Void doInBackground(CheckBox... box) {
			ExternalDatabaseHelper db = new ExternalDatabaseHelper();
			InternalDatabaseHelper ldb = new InternalDatabaseHelper(Settings.this,null,1);
			// check box for visibility to friends
			String phone = getIntent().getBundleExtra("profileData").getString("phone");

			if (box[0].isChecked()) {
				db.updateShowingToFriends("T", phone);
				ldb.updateShowingFriends("T", phone);
			}
			else{
				db.updateShowingToFriends("F", phone);
				ldb.updateShowingFriends("F", phone);
			}

			// check box for visibility to non friends
			if (box[1].isChecked()) {
				db.updateShowingToOthers("T", phone);
				ldb.updateShowingOthers("T", phone);
			}
			else{
				db.updateShowingToOthers("F", phone);
				ldb.updateShowingOthers("F", phone);
			}
			ldb.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {       
			ldb.close();
			CommonUtility.showProgress(false, dialog.getWindow().getDecorView(), R.id.progressBar);
			dialog.dismiss();
		}			
	} 

	// go take a profile picture 
	private void cameraOptions() {	
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		final Dialog dialog = new Dialog(Settings.this);
		dialog.setContentView(R.layout.activity_map_types_menu);
		dialog.setTitle("Profile Picture");
		dialog.getWindow().setLayout((int) (LayoutParams.WRAP_CONTENT), (int) (LayoutParams.WRAP_CONTENT));
		dialog.show();

		final String[] options = {"Take Picture"};
		ListView listView = (ListView)dialog.findViewById(R.id.mapTypesListView);              
		listView.setAdapter(new ArrayAdapter<String>(Settings.this, R.layout.activity_map_types_menu, R.id.text, options));                       

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {					
				dialog.dismiss();							
				Intent intent = new Intent(Settings.this, Camera.class);
				//System.out.println("Number " + getIntent().getBundleExtra("profileData").getString("phone"));
				intent.putExtra("isRegister", false);
				intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
				intent.putExtra("phone", getIntent().getBundleExtra("profileData").getString("phone"));
				startActivity(intent);					 
			}                  	
		}); 		
	}


	/**
	 * logout of the app
	 */
	private void logout() {

		TextView logout = (TextView)findViewById(R.id.logout);
		logout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Settings.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
			}
		});
	}

	// send the default image to the DB
	private class SendImageToDB extends AsyncTask<Bitmap, Void, Void> {
		@Override
		protected Void doInBackground(Bitmap... params) {				
			Bitmap b = params[0];
			ByteArrayOutputStream stream = new ByteArrayOutputStream();			
			b.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			new ExternalDatabaseHelper().insertImage(byteArray, getIntent().getBundleExtra("profileData").getString("phone"),Calendar.getInstance().getTime().toString());			
			return null;
		}

		@Override
		protected void onPostExecute(Void args) {   			
			CommonUtility.showProgress(false, getWindow().getDecorView(), R.id.progressBar);
			startActivity(new Intent(Settings.this,Login.class));
		}	
	} // set image async task
}
