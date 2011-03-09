package no.bekk;

import java.util.ArrayList;
import java.util.List;
 
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
 
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
 
public class MyItemizedOverlay extends ItemizedOverlay {
 
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Drawable marker;
	Context mContext;
	
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public MyItemizedOverlay(Drawable defaultMarker, Context context){
		super(defaultMarker);
		mContext = context;
	}
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}
 
	@Override
	public int size() {
	  return mOverlays.size();
	}
	
	/**@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}**/
}
