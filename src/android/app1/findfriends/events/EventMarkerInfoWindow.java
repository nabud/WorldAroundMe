package android.app1.findfriends.events;

import android.app1.findfriends.R;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public class EventMarkerInfoWindow implements InfoWindowAdapter {
  LayoutInflater inflater=null;

  public EventMarkerInfoWindow(LayoutInflater inflater) {
    this.inflater=inflater;
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return(null);
  }

  @Override
  public View getInfoContents(Marker marker) {
    View popup=inflater.inflate(R.layout.eventmarkerinfowindow, null);
    
    TextView eventInfo=(TextView)popup.findViewById(R.id.eventinfo);
      
    eventInfo.setText(marker.getSnippet());

    return(popup);
  }
}