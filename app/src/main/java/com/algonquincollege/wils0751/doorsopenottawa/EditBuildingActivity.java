package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.algonquincollege.wils0751.doorsopenottawa.model.Building;


/**
 * Created by ShannonWilson on 2016-12-01.
 */

public class EditBuildingActivity extends Activity {
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings/";
    private EditText buildingName;
    private EditText buildingAddress;
    private Button savebtn;
    private Button cancelbtn;
    public String Address;
    public String Description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        buildingName = (EditText) findViewById(R.id.buildingname);
        buildingAddress = (EditText) findViewById(R.id.buildingaddress);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String buildingDescriptionFromMainActivity = bundle.getString("description");
            String buildingAddressFromMainActivity = bundle.getString("address");

            buildingName.setText(buildingDescriptionFromMainActivity);
            buildingAddress.setText(buildingAddressFromMainActivity);
        }

        savebtn = (Button) findViewById(R.id.save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Description = buildingName.getText().toString();
                Address = buildingAddress.getText().toString();

                updateBuilding(REST_URI);
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
    private void updateBuilding(String uri) {
        Building building = new Building();
        String address = buildingName.getText().toString();
        String description = buildingAddress.getText().toString();

        if (!address.equals(building.getAddress())) {
            building.setAddress(address);
        }
        if (!description.equals(building.getDescription())) {
            building.setDescription(description);
        }

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.PUT);
        pkg.setUri(uri + building.getBuildingId() +"/wils0751");
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
            }
        }
    }


}
