package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
 * Allows the user to add a building to the list
 *
 * @author Shannon Wilson(Wils0751)
 */
public class NewBuildingActivity extends Activity {

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String REST_URI_IMAGE = "http://doors-open-ottawa-hurdleg.mybluemix.net/buildings/";
    private static int RESULT_LOAD_IMAGE = 1;
    private static final String TAG = "";
    public String name;
    public String address;
    public String description;
    public ImageView buildingImage;

    public String thePath;
    private Uri fullPhotoUri;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_building);

        Button okButton = (Button) findViewById(R.id.addbutton);
        buildingImage=(ImageView)findViewById(R.id.doorsopenimage);
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
                finish();

//                uploadImage(REST_URI_IMAGE);

            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                Toast.makeText(getApplicationContext(), getPath(fullPhotoUri), Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonLoadImage = (Button) findViewById(R.id.loadImage);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View c) {


                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
// Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "OnActivityResult");
        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_LOAD_IMAGE) {
            fullPhotoUri = data.getData();
            if (fullPhotoUri == null) {
                Log.e("TAG","FullPhotoUri");
                return;
            }
            buildingImage.setImageURI(fullPhotoUri);
            Log.e("TAG", fullPhotoUri.getPath());
            thePath = getPath(fullPhotoUri);
            Log.e("TAG", thePath);

        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    private void createBuilding(String uri) {
        Building building = new Building();


        building.setBuildingId(0);
        building.setName(name);
        building.setAddress(address);
        building.setImage(thePath);
        building.setDescription(description);


//
        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(uri);
//         pkg.setParam("buildingId", building.getBuildingId() + "");
        pkg.setParam("image", building.getImage());
        pkg.setParam("name", building.getName());
        pkg.setParam("address", building.getAddress());
        pkg.setParam("description", building.getDescription());


        DoTask postTask = new DoTask();
        postTask.execute(pkg);

    }

    private void uploadImage(String uri) {
        Building building = new Building();
        building.setImage(thePath);

        RequestPackage pkg = new RequestPackage();
        pkg.setMethod(HttpMethod.POST);
        pkg.setUri(uri + 145 + "/image");
        pkg.setParam("image", building.getImage());

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
    private class MyTask extends AsyncTask<RequestPackage, Void, String> {

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

            }else{
                Log.e("Log", result);
            }
        }
    }
}


