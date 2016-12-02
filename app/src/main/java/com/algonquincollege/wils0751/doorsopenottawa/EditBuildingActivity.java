package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by ShannonWilson on 2016-12-01.
 */

public class EditBuildingActivity extends Activity {
    private EditText buildingName;
    private EditText buildingAddress;
    private Button savebtn;
    private Button cancelbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        buildingName = (EditText) findViewById(R.id.buildingname);
        buildingAddress = (EditText) findViewById(R.id.buildingaddress);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String buildingNameFromMainActivity = bundle.getString("name");
            String buildingAddressFromMainActivity = bundle.getString("address");

            buildingName.setText(buildingNameFromMainActivity);
            buildingAddress.setText(buildingAddressFromMainActivity);
        }

        savebtn = (Button) findViewById(R.id.save);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), EditBuildingActivity.class));
            }
        });
        cancelbtn = (Button) findViewById(R.id.cancelbutton);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditBuildingActivity.class));
            }
        });


    }
    private void updateBuilding(String uri) {

    }


}
