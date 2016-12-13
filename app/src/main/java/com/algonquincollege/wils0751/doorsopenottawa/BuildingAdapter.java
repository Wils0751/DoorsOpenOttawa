package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.algonquincollege.wils0751.doorsopenottawa.model.Building;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Adapts the array from the getters
 *
 * @author ShannonWilson (Wils0751)
 */

public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable {

    private Context context;
    private List<Building> buildingList;
    private final ArrayList<Building> searchlist;
    private ArrayList<Integer> favouritelist;
    private LruCache<Integer, Bitmap> imageCache;





    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;





        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
        searchlist = new ArrayList<Building>();
        searchlist.addAll(buildingList);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display planet name in the TextView widget
        final Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.buildingCellDescription);
        TextView tv1 = (TextView) view.findViewById(R.id.addressCell);


        ToggleButton favourite = (ToggleButton) view.findViewById(R.id.toggle);


        tv.setText(building.getName());
        tv1.setText(building.getAddress());
        favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Log.e("LOG", "adding to arraylist");

                    if (favouritelist == null) {
                        favouritelist = new ArrayList<Integer>();
                    }
                    favouritelist.add(buildingList.get(position).getBuildingId());
                    save_User_To_Shared_Prefs(context, favouritelist);
                    Log.e("Log",  "the number is " + favouritelist.contains(buildingList.get(position).getBuildingId()));

                }
                else{

                    favouritelist.remove(building.getBuildingId());

                    Log.e("LOG", "removing from arraylist");
                    Log.e("Log",  "the number is " + favouritelist.contains(buildingList.get(position).getBuildingId()));
                }

            }
        });

//        favourite.setChecked(true);
        if(favouritelist != null && favouritelist.contains(buildingList.get(position).getBuildingId())) {
            Log.e("Log", "is it getting here?");
            favourite.setChecked(true);
        }
        if(get_User_From_Shared_Prefs(getContext()).contains(buildingList.get(position).getBuildingId())){
            favourite.setChecked(true);
            Log.e("Log", "testing ");
        }


        Bitmap bitmap = imageCache.get(building.getBuildingId());

        if (bitmap != null) {
//            Log.i("BUILDINGS", building.getName() + "\tbitmap in cache");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(building.getBitmap());
        } else {
//            Log.i("BUILDINGS", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            try {
                ImageLoader loader = new ImageLoader();
                loader.execute(container);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public static void save_User_To_Shared_Prefs(Context context, List favouriteList) {
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouriteList);
        prefsEditor.putString("id", json);
        prefsEditor.commit();

    }
    public static List get_User_From_Shared_Prefs(Context context) {
        List favorites;
        SharedPreferences appSharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = appSharedPrefs.getString("id", "");


        List favourites = gson.fromJson(json, List.class);
        favorites = Arrays.asList(favourites);
        favorites = new ArrayList(favorites);
        return favorites;
    }


    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;

    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        buildingList.clear();
        if (charText.length() == 0) {
            buildingList.addAll(searchlist);
        }
        else
        {
            for (Building b : searchlist)
            {
                if (b.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    buildingList.add(b);
                }
            }
        }
        notifyDataSetChanged();
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {

            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                try {
                    InputStream in = (InputStream) new URL(imageUrl).getContent();
//                    InputStream error = in.getErrorStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    building.setBitmap(bitmap);
                    in.close();
                    container.bitmap = bitmap;
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                return container;
            } catch (Exception e) {
                System.err.println("IMAGE: " + building.getName());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            if(result==null) return;
            ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
            image.setImageBitmap(result.bitmap);
//        result.building.setBitmap(result.bitmap);
            if(result.building.getBuildingId()!=null && result.bitmap!=null) {
                imageCache.put(result.building.getBuildingId(), result.bitmap);
            }
//            imageCache.put(result.building.getBuildingId(), result.bitmap);
        }

    }
}



