package no.bekk;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

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


public class StartActivity extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */
	MapView mapView;
	MapController mc;
	GeoPoint p;
    Location currentLocation;
    MyLocationOverlay lo;
    RestClient rc;
    LocationManager locationManager;
    Geocoder gc;
    JSONArray jsonArray;

    // Define a listener that responds to location updates

        public void onLocationChanged(Location location) {
        	 
        	makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	initMap();
    	initLocation();
    	initRestClient();
    }

    private void drawItem(int lat, int lon, String url) {
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	Drawable drawable = LoadImageFromWebOperations(url); 
    	if(drawable == null){
    		drawable = this.getResources().getDrawable(R.drawable.pin);    		
    	}
    	MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable);
    	GeoPoint point = new GeoPoint(lat,lon);
    	OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
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
			// TODO Auto-generated catch block
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
						drawItem(empPoint.getLatitudeE6(), empPoint.getLongitudeE6(), employee.getString("ImageUrl"));
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
    	mc.setCenter(new GeoPoint((int) (10.4*1E6), (int) (-122.4*1E6)));
    	mc.setZoom(4);
    	
    }
    
    private void initLocation() {
    	locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    	
    }

    private void makeUseOfNewLocation(Location location) {
        this.currentLocation = location;
        //Kommenterer ut denne sŒ den ikke ¿delegger for resten: drawMarkerForLocation(location);
    }

    private void drawMarkerForLocation(Location location) {
        p = new GeoPoint((int)(location.getLatitude()*1E6), (int)(location.getLongitude()*1E6));
        lo = new MyLocationOverlay(this, mapView);
        lo.enableMyLocation();

        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(lo);
        mc.animateTo(p);
        mapView.postInvalidate();
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