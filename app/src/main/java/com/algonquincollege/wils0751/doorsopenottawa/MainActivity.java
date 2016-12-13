package com.algonquincollege.wils0751.doorsopenottawa;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpManager;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.HttpMethod;
import com.algonquincollege.wils0751.doorsopenottawa.Utils.RequestPackage;
import com.algonquincollege.wils0751.doorsopenottawa.model.Building;
import com.algonquincollege.wils0751.doorsopenottawa.parsers.BuildingJSONParser;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main activity class handles all the stuff displaying on the screen
 *
 * @author Shannon Wilson(Wils0751)
 */
public class MainActivity extends ListActivity  /*implements AdapterView.OnItemClickListener*/ {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String LOGOUT_URI = "http://doors-open-ottawa-hurdleg.mybluemix.net/users/logout";

    private static final String TAG = "";
    SwipeRefreshLayout mSwipeRefreshLayout;

    private AboutDialogFragment mAboutDialog;


    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;

    private BuildingAdapter adapter;
    private SearchView searchView;
    private ListView list;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );

        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
//        list=(ListView) findViewById(R.id.list);
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
                intent.putExtra("buildingid", theSelectedBuilding.getBuildingId());

                startActivity(intent);
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Building theSelectedBuilding = buildingList.get(position);

                Intent i = new Intent(getApplicationContext(), EditBuildingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.putExtra("description", theSelectedBuilding.getDescription());
//                i.putExtra("address", theSelectedBuilding.getAddress());
                i.putExtra("buildingid", theSelectedBuilding.getBuildingId());

                startActivity(i);
                return true;
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
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isCheckable()) {
            // leave if the list is null
            if (buildingList == null) {
                return true;
            }


            // which sort menu item did the user pick?
            switch (item.getItemId()) {
                case R.id.action_sort_name_asc:
                    Collections.sort(buildingList, new Comparator<Building>() {
                        @Override
                        public int compare(Building lhs, Building rhs) {
                            Log.i("BUILDING", "Sorting planets by name (a-z)");
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    });
                    break;

                case R.id.action_sort_name_dsc:
                    Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>() {
                        @Override
                        public int compare(Building lhs, Building rhs) {
                            Log.i("BUILDING", "Sorting planets by name (z-a)");
                            return lhs.getName().compareTo(rhs.getName());
                        }
                    }));
                    break;
            }
            item.setChecked(true);
            // re-fresh the list to show the sort order
            ((ArrayAdapter) getListAdapter()).notifyDataSetChanged();
        }
        if (item.getItemId() == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), "About Dialog");
            return true;
        }
        if (item.getItemId() == R.id.edit) {

            startActivity(new Intent(this, NewBuildingActivity.class));

        }


        // remember which sort option the user picked

        return false;

    }

    private void requestData(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);
        MyTask getTask = new MyTask();
        getTask.execute(getPackage);

    }





    protected void updateDisplay() {
        //Use BuildingAdapter to display data
//        adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
//        setListAdapter(adapter);
        if(buildingList!=null) {
            adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
            setListAdapter(adapter);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.filter(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.filter(newText);
                    return true;
                }
            });
        }


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



    private class MyTask extends AsyncTask<RequestPackage, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], "wils0751", "password");
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
                Toast.makeText(getApplicationContext(), "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }
//
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//        Log.e("TAG", "onDestory");
//        requestData(LOGOUT_URI);
//
//    }


}
