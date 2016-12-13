package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.algonquincollege.wils0751.doorsopenottawa.model.Building;

/**
 * Allows the user to edit a building of theirs on long click
 *
 * @author Shannon Wilson(Wils0751)
 */

public class EditBuildingActivity extends AppCompatActivity{
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings/";
    private EditText buildingDescription;
    private EditText buildingAddress;
    private Button savebtn;
    private Button cancelbtn;
    public String Address;
    public String Description;
    public Integer buildingid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        buildingDescription = (EditText) findViewById(R.id.buildingname);
        buildingAddress = (EditText) findViewById(R.id.buildingaddress);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Integer buildingIdformMainActivity = bundle.getInt("buildingid");

            buildingid = buildingIdformMainActivity;

            Log.e("Log",String.valueOf(buildingid));
        }


        savebtn = (Button) findViewById(R.id.save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBuilding(REST_URI + buildingid);
                Toast.makeText(getApplicationContext(), Description + Address, Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        cancelbtn = (Button) findViewById(R.id.cancelbutton);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.trash) {
            AlertDialog dialog = AskOption();
            dialog.show();


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
                .setMessage("Are you sure you want to delete this building?")


                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deletePlanet(REST_URI + buildingid);
                        dialog.dismiss();
                        finish();
                    }

                })



                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void updateBuilding(String uri) {
        Address = buildingAddress.getText().toString();
        Description = buildingDescription.getText().toString();
        Building building = new Building();

        building.setBuildingId(0);
        building.setAddress(Address);
        building.setDescription(Description);


        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.PUT);
        pkg.setUri(uri);
        pkg.setParam("address", building.getAddress());
        pkg.setParam("description", building.getDescription());

        DoTask putTask = new DoTask();
        putTask.execute( pkg );

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
                Toast.makeText(EditBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            } else{
                Log.e("Log", result);
            }
        }
    }


}
