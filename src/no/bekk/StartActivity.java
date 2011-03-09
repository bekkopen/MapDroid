package no.bekk;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;


public class StartActivity extends MapActivity {
    /** Called when the activity is first created. */
	MapView mapView;
	MapController mc;
    MyLocationOverlay lo;
    RestClient rc;
    Geocoder gc;
    JSONArray jsonArray;
    MyLocationOverlay myLocOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	initMap();
    	initLocation();
    	initRestClient();
    }

    private void drawItem(int lat, int lon, String url, String name, String content) {
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	Drawable drawable = LoadImageFromWebOperations(url); 
    	if(drawable == null){
    		drawable = this.getResources().getDrawable(R.drawable.pin);    		
    	}
    	MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable, this);
    	GeoPoint point = new GeoPoint(lat,lon);
    	OverlayItem overlayitem = new OverlayItem(point, name, content);
    	itemizedoverlay.addOverlay(overlayitem);
    	mapOverlays.add(itemizedoverlay);
	}

	private GeoPoint getLocationFromAddress(String address) {
    	gc = new Geocoder(this, Locale.UK);
    	GeoPoint point = new GeoPoint(0, 0);
    	try {
			List<Address> lonlatFromAddress = gc.getFromLocationName(address, 1);
			if(lonlatFromAddress!=null && lonlatFromAddress.size()> 0){
				point = new GeoPoint((int)(lonlatFromAddress.get(0).getLatitude()*1E6), (int)(lonlatFromAddress.get(0).getLongitude()*1E6));
			}
		} catch (IOException e) {
			Log.e("TAG", "Exception i geocoder", e);
		}
		return point;
	}

	private void initRestClient(){
    	rc = new RestClient();
    	try {
			jsonArray = rc.getEmployeeList("https://intern.bekk.no/api/Employees.svc/", "intern.bekk.no", getString(R.string.BEKKUser), getString(R.string.BEKKPass));
			getLocationsAndDrawItems(jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    private void getLocationsAndDrawItems(JSONArray employees) {
    	gc = new Geocoder(this, Locale.UK);
    	Log.i("Antall ansatte:", ""+employees.length());
    	for(int i=0; i<employees.length(); i++){
    		try {
    			JSONObject employee = employees.getJSONObject(i);
				if(employee.getString("Department").equals("Trondheim")){
					Log.i("Trondheimskontor",employee.getString("LastName"));
					GeoPoint empPoint = getLocationFromAddress(employee.getString("StreetAddress")+" "+employee.getString("PostalAddress"));
					if(empPoint.getLatitudeE6()!=0 && empPoint.getLongitudeE6()!=0)
						drawItem(empPoint.getLatitudeE6(), empPoint.getLongitudeE6(), employee.getString("ImageUrl"), employee.getString("FirstName")+" "+employee.getString("LastName"),employee.getString("Seniority"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
    	}
	}

	private void initMap(){
    	mapView = (MapView) findViewById(R.id.mapView);
    	mc = mapView.getController();
    	mapView.setBuiltInZoomControls(true);
    	mc.setZoom(4);
    }
    
    private void initLocation() {
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocOverlay);
    }

    private Drawable LoadImageFromWebOperations(String url){
	    try{
	    	InputStream is = (InputStream) new URL(url).getContent();
	    	Drawable d = Drawable.createFromStream(is, "BEKK-ansatt");
	    	return d;
	    }catch (Exception e) {
	    	System.out.println("Exc="+e);
	    	return null;
	    }
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

}