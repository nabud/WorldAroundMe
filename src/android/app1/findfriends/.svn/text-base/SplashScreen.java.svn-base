package android.app1.findfriends;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;


public class SplashScreen extends Activity {

	private static int SPLASH_TIME_OUT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				CheckEnableGPS();
				Intent i = new Intent(SplashScreen.this, Login.class);
				startActivity(i);
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

	private void CheckEnableGPS(){
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if(!provider.equals("")){
			//GPS Enabled
			Toast.makeText(SplashScreen.this, "Providers Enabled: " + provider,
					Toast.LENGTH_LONG).show();
		}else{
			Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			startActivity(intent);
		}

	}
}

