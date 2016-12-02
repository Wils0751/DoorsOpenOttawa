package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by ShannonWilson on 2016-12-01.
 */

public class EditBuildingActivity extends Activity {
    private EditText buildingName;
    private EditText buildingAddress;
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
    }
}
