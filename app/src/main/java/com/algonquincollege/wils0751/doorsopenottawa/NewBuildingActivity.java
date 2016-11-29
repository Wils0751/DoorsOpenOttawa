package com.algonquincollege.wils0751.doorsopenottawa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by ShannonWilson on 2016-11-29.
 */

public class NewBuildingActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_building);

        Button okButton = (Button) findViewById(R.id.addbutton);
        okButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EditText buildingName = (EditText) findViewById(R.id.buildingName);
                EditText buildingAddress = (EditText) findViewById(R.id.buildingAddress);
                EditText buildingDescription = (EditText) findViewById(R.id.buildingDescription);

                String name = buildingName.getText().toString();
                String address = buildingAddress.getText().toString();
                String description = buildingDescription.getText().toString();

//                Toast.makeText(getApplicationContext(), "Email: " + username + " Password: " + userPass + " Remember? " + isRememberMe, Toast.LENGTH_LONG).show();
                Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.putExtra( "userName", name );
                intent.putExtra( "userAddress", address );
                intent.putExtra("userDescription", description);
                startActivity( intent );
            }
        });
    }
}
