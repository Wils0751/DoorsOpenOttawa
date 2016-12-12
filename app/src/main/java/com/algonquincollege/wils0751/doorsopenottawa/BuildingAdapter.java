package com.algonquincollege.wils0751.doorsopenottawa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
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
    private final ArrayList<Building> arraylist;
    private ArrayList<Integer> favouritelist;
    private LruCache<Integer, Bitmap> imageCache;
    private SharedPreferences sp;


    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;
//        this.temporarydata = objects;



        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
//        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        arraylist = new ArrayList<Building>();
        arraylist.addAll(buildingList);

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

                    if (null == favouritelist) {
                        favouritelist = new ArrayList<Integer>();
                    }
                    favouritelist.add(buildingList.get(position).getBuildingId());
//
//                    SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    try {
//                        editor.putString(TASKS, ObjectSerializer.serialize(favouritelist));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    editor.commit();



                    Log.e("Log",  "the number is " +favouritelist.get(0));

                }
                else{

                    favouritelist.remove(building.getBuildingId());
                    Log.e("LOG", "removing from arraylist");
                }

            }
        });
        if (favouritelist != null)
            if (favouritelist.contains(building)){
                favourite.setChecked(true);
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
    public boolean favouriteitem(String buildingId){
     boolean check = false;

        return check;
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
            buildingList.addAll(arraylist);
        }
        else
        {
            for (Building b : arraylist)
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



