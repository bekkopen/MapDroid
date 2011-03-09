package no.bekk;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;


public class StartActivity extends MapActivity implements LocationListener {
    /** Called when the activity is first created. */
	MapView mapView;
	MapController mc;
	GeoPoint p;
	GeoPoint torsVeg;	
    Location currentLocation;
    MyLocationOverlay lo;
    RestClient rc;
    LocationManager locationManager;
    Geocoder gc;


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
    	initGeocoder();
    	//Tegner to punkter
    	initDrawing(9240000,49120000);
    	initDrawing(19240000,-99120000);
    }

    private void initDrawing(int lat, int lon) {
    	List<Overlay> mapOverlays = mapView.getOverlays();
    	Drawable drawable = this.getResources().getDrawable(R.drawable.pin);
    	MyItemizedOverlay itemizedoverlay = new MyItemizedOverlay(drawable);
    	GeoPoint point = new GeoPoint(lat,lon);
    	OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
    	itemizedoverlay.addOverlay(overlayitem);
    	mapOverlays.add(itemizedoverlay);


	}

	private void initGeocoder() {
    	gc = new Geocoder(this, Locale.UK);
    	String address = "Tors veg 40 D, 7032 Trondheim";
    	
    	try {
			List<Address> home = gc.getFromLocationName(address, 5);
			if(home != null){
				torsVeg = new GeoPoint((int)(home.get(0).getLatitude()*1E6), (int)(home.get(0).getLongitude()*1E6));
				mc.animateTo(torsVeg);
			}
		} catch (IOException e) {
			Log.e("TAG", "Exception i geocoder", e);
		}
		
	}

	private void initRestClient(){
    	rc = new RestClient();
    	try {
			rc.getEmployeeList("https://intern.bekk.no/api/Employees.svc/", "intern.bekk.no", getString(R.string.BEKKUser), getString(R.string.BEKKPass));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
        //Kommenterer ut denne så den ikke ødelegger for resten: drawMarkerForLocation(location);
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
    public void drawItem(double lon, double lat){
    	p = new GeoPoint((int)lat, (int)lon);
    	mapView.getOverlays();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

}