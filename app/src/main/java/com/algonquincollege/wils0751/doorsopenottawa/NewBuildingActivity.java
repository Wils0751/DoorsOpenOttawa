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

import java.io.File;

import static android.R.attr.button;
import static android.R.attr.data;
import static android.R.attr.start;

/**
 * Created by ShannonWilson on 2016-11-29.
 */

public class NewBuildingActivity extends Activity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String REST_URI_IMAGE="http://doors-open-ottawa-hurdleg.mybluemix.net/buildings/";
    private static int RESULT_LOAD_IMAGE = 1;
    String ImageDecode;
    private static final String TAG = "";
    public String name;
    public String address;
    public String description;
    public ImageView buildingImage;

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


                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);
// Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            buildingImage =(ImageView) findViewById(R.id.imageView1);
            buildingImage.setImageBitmap(imageBitmap);
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap ProfilePic = extras.getParcelable("data");
                buildingImage.setImageBitmap(ProfilePic);
                Uri fullPhotoUri = data.getData();
                buildingImage.setImageURI(fullPhotoUri);

             }
        }
    }




    private void createBuilding(String uri) {
            Building building = new Building();


            building.setBuildingId(0);
            building.setName(name);
            building.setAddress(address);

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
    private void uploadImage(String uri){
        Building building = new Building();
        building.setImage("tests.jpg");

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(uri + "138" + "/image" );
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


