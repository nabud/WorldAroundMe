package android.app1.findfriends;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class GetLatLngFromAddress {

	/**
	 * convert an address to a LAT and LONG value
	 */
	public static ArrayList<LatLng> getLatLongFromAddress(String youraddress) {
		ArrayList<LatLng>  list = new ArrayList<LatLng>();
		double lat = 0.0;
		double lng = 0.0;
		String key =  "Fmjtd%7Cluubn902l9%2Cr5%3Do5-902a9f"; 
		youraddress = youraddress.replaceAll("[^A-Za-z0-9. ]", "").replace(" ", "+");
		
		//www.mapquestapi.com/geocoding/v1/address?key=Fmjtd|luubn902l9%2Cr5%3Do5-902a9f&callback=renderOptions&inFormat=kvp&outFormat=json&location=216+N+Atlanta+St+Metairie+LA+70003
		String uri = "http://www.mapquestapi.com/geocoding/v1/address?key="+key+"&callback=renderOptions&inFormat=kvp&outFormat=json&location="+youraddress;
	    //String uri = "http://maps.google.com/maps/api/geocode/json?address=" + youraddress + "&sensor=true";
	    HttpGet httpGet = new HttpGet(uri);
	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response;
	    StringBuilder stringBuilder = new StringBuilder();
        
	    try {
	        response = client.execute(httpGet);
	        HttpEntity entity = response.getEntity();
	        InputStream stream = entity.getContent();
	        int b;
	        while ((b = stream.read()) != -1) {
	            stringBuilder.append((char) b);
	        }
	    } catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    
        

	    
	    JSONObject jsonObject = null;
	    try {
	    	
	    	String jsonString = stringBuilder.toString();
	    	jsonString = jsonString.substring(14);
	    	
	    	jsonObject = new JSONObject(jsonString.replace(");", ""));
	    	
	    	jsonObject = (jsonObject.getJSONArray("results").getJSONObject(0)).getJSONArray("locations").getJSONObject(0).getJSONObject("latLng");

            
            
//            jsonObject = new JSONObject(array.getString(0));
//            array = jsonObject.getJSONArray("location");
//            
//            jsonObject = new JSONObject(array.getString(0));
//            array = jsonObject.getJSONArray("latlng");
            
            
            
            lng = jsonObject.getDouble("lng");
	        lat = jsonObject.getDouble("lat");
	     
	        
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }
	    list.add(new LatLng(lat,lng));	    
	    return list;
	}	
	
	
	
}
