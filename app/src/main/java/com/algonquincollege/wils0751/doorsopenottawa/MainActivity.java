package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.algonquincollege.wils0751.doorsopenottawa.model.Building;
import com.algonquincollege.wils0751.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity class handles all the stuff displaying on the screen
 *
 * @author Geemakun Storey (Stor0095), Shannon Wilson(Wils0751)
 */
public class MainActivity extends ListActivity  /*implements AdapterView.OnItemClickListener*/ {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String LOGOUT_URI ="http://doors-open-ottawa-hurdleg.mybluemix.net/users/logout";
    private static final String TAG = "";
    SwipeRefreshLayout mSwipeRefreshLayout;

    private AboutDialogFragment mAboutDialog;


    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);



        tasks = new ArrayList<>();
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);
//               Toast.makeText(this, theSelectedBuilding.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Name", theSelectedBuilding.getName());
                intent.putExtra("Description", theSelectedBuilding.getDescription());
                intent.putExtra("Address", theSelectedBuilding.getAddress());
                intent.putExtra("Date", theSelectedBuilding.getDate());

                startActivity(intent);
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        if (isOnline()) {
                            requestData(REST_URI);
                            mSwipeRefreshLayout.setRefreshing(false);
                        } else {
                            Toast.makeText(getApplicationContext(), "Network isn't available", Toast.LENGTH_LONG).show();
                        }


                    }
                }

        );



        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), "About Dialog");
            return true;
        }
        if (item.getItemId() == R.id.edit) {

            startActivity(new Intent(this, NewBuildingActivity.class));

        }


        return false;

    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }
    private void createBuilding(String uri) {
        Building building = new Building();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String NameFromLoginActivity = bundle.getString("userName");
            String PasswordFromLoginActivity = bundle.getString("userAddress");
            String DescriptionFromLoginActivity = bundle.getString("userDescription");

            building.setBuildingId(0);
            building.setName(NameFromLoginActivity);
            building.setAddress(PasswordFromLoginActivity);
            building.setDescription(DescriptionFromLoginActivity);

//
            RequestPackage pkg = new RequestPackage();
            pkg.setMethod(HttpMethod.POST);
            pkg.setUri(uri);
            pkg.setParam("buildingId", building.getBuildingId() + "");
            pkg.setParam("name", building.getName());
            pkg.setParam("address", building.getAddress());
            pkg.setParam("address", building.getDescription());


            DoTask postTask = new DoTask();
            postTask.execute(pkg);
        }
    }

    protected void updateDisplay() {
        //Use BuildingAdapter to display data

        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData(params[0], "wils0751", "password" );
            buildingList = BuildingJSONParser.parseFeed(content);
            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();

        }
    }

    private class DoTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0],"wils0751" ,"password");

            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            pb.setVisibility(View.INVISIBLE);

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e("TAG", "onDestory");
        requestData(LOGOUT_URI);

    }





}
