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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
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
    }

    private void initGeocoder() {
    	gc = new Geocoder(this, Locale.UK);
    	String address = "Tors veg 40 D, 7032 Trondheim";
    	try {
            List<Address> addresses = gc.getFromLocation(
                    9,49, 1);

			List<Address> foundAddresses = gc.getFromLocationName(address, 1);
			Log.i("FoundAdresses",foundAddresses.toString());
			for(int i = 0; i<foundAddresses.size(); i++){
				Address a = foundAddresses.get(i);
				Log.i("Lon",""+a.getLongitude());
				Log.i("Lat",""+a.getLatitude());
				drawItem(a.getLongitude(),a.getLatitude());
			}
		} catch (IOException e) {
			Log.e("TAG", "Exception i geocoder", e);
			drawItem(9,49);
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
    	mc.setCenter(new GeoPoint((int) (63.4*1E6), (int) (10.4*1E6)));
    	mc.setZoom(4);
    	
    }
    
    private void initLocation() {
    	locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    	
    }

    private void makeUseOfNewLocation(Location location) {
        this.currentLocation = location;
        drawMarkerForLocation(location);
    }

    private void drawMarkerForLocation(Location location) {
        p = new GeoPoint((int)location.getLatitude(), (int)location.getLongitude());
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
    	Overlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);
        mapView.invalidate();
    }

    class MapOverlay extends com.google.android.maps.Overlay {

        @Override
        public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
            super.draw(canvas, mapView, shadow);

            //---translate the GeoPoint to screen pixels---
            Point screenPts = new Point();
            mapView.getProjection().toPixels(p, screenPts);

            //---add the marker---
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
            canvas.drawBitmap(bmp, screenPts.x, screenPts.y-24, null);
            return true;
        }
        /**
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView)
        {
            //---when user lifts his finger---
            if (event.getAction() == 1) {
                GeoPoint p = mapView.getProjection().fromPixels(
                    (int) event.getX(),
                    (int) event.getY());
                    Toast.makeText(getBaseContext(),
                        p.getLatitudeE6() / 1E6 + "," +
                        p.getLongitudeE6() /1E6 ,
                        Toast.LENGTH_SHORT).show();
            }
            return false;
        }  */

    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
}