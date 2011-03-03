package no.bekk;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import android.os.Bundle;
 
public class StartActivity extends MapActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
 
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}