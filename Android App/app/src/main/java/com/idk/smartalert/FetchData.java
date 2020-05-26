package com.idk.smartalert;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL  ;
import java.util.Map;

public class FetchData extends AsyncTask<Void,Void,Void> {
    String data="";
    int dp=0;
    String level="";

    public FetchData() {
        Log.e("tv","hghg");

    }

    String flevel="";

    public interface AsyncResponse {
        void processFinish(String output);
    }
    public AsyncResponse as = null;

    public FetchData(AsyncResponse as){
        this.as = as;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url=new URL("https://api.thingspeak.com/channels/863503/fields/1.json?results=1");
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            InputStream inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            while(line!=null){
                line=bufferedReader.readLine();
                data=data+line;
            }
            dp=data.lastIndexOf("field1");
            level=data.substring(dp+9,dp+13);
            flevel= level.replaceAll("[^0-9]", "");

            //Log.e("tv","hidhar aayaaaaaa");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;}
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        LatLng sydney = new LatLng(-34, 151);
//
//        MapsActivity.mMap.addMarker(new MarkerOptions().position(sydney).title(flevel));

        //MapsActivity.mMap.setText(flevel);
        //Intent intent= new Intent(FetchData.this,MapsActivity.class);
        as.processFinish(flevel);
        com.idk.smartalert.MapsActivity.tv.setText(flevel);
        Log.e("tv",""+ com.idk.smartalert.MapsActivity.tv.getText().toString());
    }
}