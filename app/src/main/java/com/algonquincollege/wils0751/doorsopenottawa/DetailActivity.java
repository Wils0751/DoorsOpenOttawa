package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.location.Geocoder;
import android.location.Address;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.MapFragment;
/**
 * Detail activity class handles all the items displayed in the detail activity
 *
 * @author Shannon Wilson(Wils0751)
 */

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView buildingName;
    private TextView buildingDescription;
    private TextView buildingHours;
    public Integer buildingid;
    private String newStringBuildingAddress;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Instantiate geocode
        mGeocoder = new Geocoder(this);

        buildingName = (TextView) findViewById(R.id.textViewName);
        buildingDescription = (TextView) findViewById(R.id.textViewDescription);
        buildingHours = (TextView) findViewById(R.id.textviewDate);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String buildingNameFromMainActivity = bundle.getString("Name");
            String buildingDescriptionFromMainActivity = bundle.getString("Description");
            String buildingDateFromMainActivity = bundle.getString("Date");
            Integer buildingidfromMainActivity =bundle.getInt("buildingid");

            buildingid= buildingidfromMainActivity;
            Log.e("Log", String.valueOf(buildingid));
            newStringBuildingAddress = bundle.getString("Address");


            buildingName.setText(buildingNameFromMainActivity);
            buildingDescription.setText(buildingDescriptionFromMainActivity);
            buildingHours.setText(buildingDateFromMainActivity);

        }

    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() ==R.id.trash) {
            new AlertDialog.Builder(this)
                    .setMessage("Message")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deletePlanet(REST_URI + buildingid);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();




        }

        return false;
    }
    private void deletePlanet(String uri) {
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod( HttpMethod.DELETE );
        // DELETE the planet with Id 8
        pkg.setUri(uri);
        DoTask deleteTask = new DoTask();
        deleteTask.execute( pkg );
    }
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")


                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePlanet(REST_URI + buildingid);
                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Pin location
        pin(newStringBuildingAddress);
    }

    /**
     * Locate and pin locationName to the map.
     */
    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            // Set zoom level
            float zoomLevel = (float) 16.0; //This goes up to 21
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, zoomLevel));
          //  Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
//            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], "wils0751", "password");

            return content;
        }

        @Override
        protected void onPostExecute(String result) {

//            pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(DetailActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}



