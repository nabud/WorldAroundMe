package android.app1.findfriends;

import android.app.Application;
import android.util.Log;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

public class LocationApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        Log.d("LocationApplication", "onCreate()");

        // output debug to LogCat, with tag LittleFluffyLocationLibrary
        LocationLibrary.showDebugOutput(true);

        try {
            // in most cases the following initialising code using defaults is probably sufficient:
            //
            // LocationLibrary.initialiseLibrary(getBaseContext(), "com.your.package.name");
            //
            // however for the purposes of the test app, we will request unrealistically frequent location broadcasts
            // every 1 minute, and force a location update if there hasn't been one for 2 minutes.
            LocationLibrary.initialiseLibrary(getBaseContext(), 10 * 1000,  30 * 1000, "android.app1.findfriends");
        }
        catch (UnsupportedOperationException ex) {
            Log.d("TestApplication", "UnsupportedOperationException thrown - the device doesn't have any location providers");
        }
    }
}
