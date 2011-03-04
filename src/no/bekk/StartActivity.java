package no.bekk;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.util.Log;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;


public class StartActivity extends MapActivity {
    /** Called when the activity is first created. */
	MapView mapView;
	MapController mc;
	GeoPoint p;
    Location currentLocation;

    LocationManager locationManager;

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("LOCATION LISTNER", "inside onLocationChanged");
        // Called when a new location is found by the network location provider.
            makeUseOfNewLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };



    private void makeUseOfNewLocation(Location location) {
        this.currentLocation = location;
        drawMarkerForLocation(location);
    }

    private void drawMarkerForLocation(Location location) {
        mc = mapView.getController();
        /**
        String coordinates[] = {"63.4", "10.4"};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
           **/
        p = new GeoPoint((int)currentLocation.getLatitude(), (int)currentLocation.getLongitude());

        mc.animateTo(p);
        mc.setZoom(17);

        //Add pin
        MapOverlay mapOverlay = new MapOverlay();
        List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);
    }


    // Register the listener with the Location Manager to receive location updates

    @Override
    public void onCreate(Bundle savedInstanceState) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);

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