package com.tcs.mmpl.customer.Activity;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tcs.mmpl.customer.R;

public class StoreMapActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener,LocationListener {

    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_store_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        GoogleMap googleMap =((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(getIntent().getStringExtra("lat")), Double.parseDouble(getIntent().getStringExtra("log"))),8));
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(getIntent().getStringExtra("lat")), Double.parseDouble(getIntent().getStringExtra("log"))))
                .title("")
                .snippet(getIntent().getStringExtra("address"))
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.stores_flag)));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View mymarkerview;

        CustomInfoWindowAdapter() {
            mymarkerview = getLayoutInflater().inflate(R.layout.layout_custominfowindow, null);
        }

        // Use default InfoWindow frame
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        public View getInfoContents(Marker marker) {
            TextView txt_title = (TextView) mymarkerview.findViewById(R.id.snippet_title);
            TextView txt_address = (TextView) mymarkerview.findViewById(R.id.snippet_address);

            txt_title.setText(Html.fromHtml("<b>" + marker.getTitle() + "</b>"));
            String string_snip=marker.getSnippet();
            StringBuilder string_snip_build = new StringBuilder(string_snip);
            if(string_snip.contains(","))
            {
                int counter = 0;
                for( int i=0; i<string_snip.length(); i++ )
                {
                    if( string_snip.charAt(i) == ',' )
                    {
                        counter++;
                    }
                }
                //// System.out.println("Total comma: "+counter);

                //// System.out.println("middle comma: "+Math.round(counter/2));
                if(counter>=2)
                {
                    string_snip_build.setCharAt(nthOccurrence(string_snip,',',Math.round(counter/2)),'\n');
                }
                //string_snip.
                //string_snip= string_snip.replace(String.valueOf((string_snip.charAt(string_snip.lastIndexOf(",")))),"\n");
            }

            txt_address.setText(string_snip_build);
            return mymarkerview;
        }




        public int nthOccurrence(String str, char c, int n) {
            int pos = str.indexOf(c, 0);
            while (n-- > 0 && pos != -1)
                pos = str.indexOf(c, pos+1);
            return pos;
        }




    }

}
