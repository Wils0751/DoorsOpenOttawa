package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.algonquincollege.wils0751.doorsopenottawa.model.Building;

import static android.R.attr.button;
import static android.R.attr.data;
import static android.R.attr.start;

/**
 * Created by ShannonWilson on 2016-11-29.
 */

public class NewBuildingActivity extends Activity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    private static int RESULT_LOAD_IMAGE = 1;
    String ImageDecode;
    private static final String TAG = "";
    public String name;
    public String address;
    public String description;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_building);

        Button okButton = (Button) findViewById(R.id.addbutton);

        Button cancelButton = (Button) findViewById(R.id.cancelbutton);
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText buildingName = (EditText) findViewById(R.id.buildingName);
                EditText buildingAddress = (EditText) findViewById(R.id.buildingAddress);
                EditText buildingDescription = (EditText) findViewById(R.id.buildingDescription);

                name = buildingName.getText().toString();
                address = buildingAddress.getText().toString();
                description = buildingDescription.getText().toString();

                createBuilding(REST_URI);

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        Button buttonLoadImage = (Button) findViewById(R.id.loadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View c) {

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {


            Uri URI = data.getData();
            String[] FILE = {MediaStore.Images.Media.DATA};


            Cursor cursor = getContentResolver().query(URI,
                    FILE, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(FILE[0]);
            ImageDecode = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory
                    .decodeFile(ImageDecode));
        }
    }




    private void createBuilding(String uri) {
            Building building = new Building();


            building.setBuildingId(0);
            building.setName(name);
            building.setAddress(address);
            building.setImage("image/test.jpg");
            building.setDescription(description);

//
            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.POST);
            pkg.setUri(uri);
//            pkg.setParam("buildingId", building.getBuildingId() + "");
            pkg.setParam("name", building.getName());
            pkg.setParam("address", building.getAddress());
            pkg.setParam("image", building.getImage());
            pkg.setParam("description", building.getDescription());


           DoTask postTask = new DoTask();
            postTask.execute(pkg);
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
                Toast.makeText(NewBuildingActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
}


