package com.zeus_logistics.ZL.interactors;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.places.api.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.zeus_logistics.ZL.helperclasses.DirectionsJSONParser;
import com.zeus_logistics.ZL.helperclasses.FirebaseOpsHelper;
import com.zeus_logistics.ZL.items.NewOrder;
import com.zeus_logistics.ZL.presenters.NewOrderPresenter;
import com.zeus_logistics.ZL.R;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NewOrderInteractor {

    private static final String TAG = "NewOrderInteractor";
    private NewOrderPresenter mPresenter;
    private LatLng mFromAddressLatLng;
    private LatLng mToAddressLatLng;
    private String addressFrom;
    private String addressTo;
    private PolylineOptions polylineOptions;
    private String distance;
    private String gMapKey;
    private CameraUpdate cu;
    private FragmentActivity mFragmentActivity;
    private Intent autoCompleteIntent;
    private TextView placesFrom;
    private TextView placesTo;
    private boolean[] additionalServices = {false, false, false};
    private GoogleMap mMap;
    ArrayList markerOptions= new ArrayList();


    public NewOrderInteractor(NewOrderPresenter presenter) {
        this.mPresenter = presenter;
    }

    public boolean isOrderReadyToSubmit() {
        return mFromAddressLatLng != null && mToAddressLatLng != null && addressFrom != null
                && addressTo != null && distance != null;
    }



    public void createAndSendOrderToDb() {
        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper();
        fbHelper.addDbDataToOrderAndSend(prepareNewOrderFromExistingData(createNewOrder()));
    }

    private NewOrder createNewOrder() {
        //Alert Dialog message for successful order
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getFragmentActivity());
        builder.setCancelable(false);
        builder.setMessage("Your Order has been created successfully, please pay cash to the rider at pickup");

        //Button Recover Password
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        //show dialog
        builder.create().show();
//       Toast toast = Toast.makeText(mFragmentActivity.getApplicationContext(),"Order has been created Successfully",Toast. LENGTH_SHORT);
//       toast.show();
        return new NewOrder();
    }

    private NewOrder prepareNewOrderFromExistingData(NewOrder newOrder) {
        newOrder.setDate(generateDateString());
        newOrder.setUserTimeStamp(generateTimestampString());
        newOrder.setDistance(getDistanceString());
        newOrder.setFromPlaceLat(String.valueOf(getFromAddressLatLng().latitude));
        newOrder.setFromPlaceLng(String.valueOf(getFromAddressLatLng().longitude));
        newOrder.setToPlaceLat(String.valueOf(getToAddressLatLng().latitude));
        newOrder.setToPlaceLng(String.valueOf(getToAddressLatLng().longitude));
        newOrder.setFrom(getAddressFrom());
        newOrder.setTo(getAddressTo());
        if(additionalServices[0]) {
            newOrder.setIsExpress("yes");
        }
        if(additionalServices[1]) {
            newOrder.setIsSuperExpress("yes");
        }
        if(additionalServices[2]) {
            newOrder.setIsCarExpress("yes");
        }
        return newOrder;
    }


    public AlertDialog.Builder sendAdditionalAlertDialog() {
        return createAdditionalAlertDialog();
    }

    private AlertDialog.Builder createAdditionalAlertDialog() {
        CharSequence[] list = new CharSequence[3];
        list[0]=mFragmentActivity.getString(R.string.neworder_express_checkbox);
        list[1]=mFragmentActivity.getString(R.string.neworder_superexpress_checkbox);
        list[2]=mFragmentActivity.getString(R.string.neworder_carexpress_checkbox);
        AlertDialog.Builder builder = new AlertDialog.Builder(mFragmentActivity);
        builder.setTitle(mFragmentActivity.getString(R.string.neworder_alert_additional));
        builder.setSingleChoiceItems(list , -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        builder.setPositiveButton("OK",
                (dialog, which) -> {
                    ListView listview = ((AlertDialog) dialog).getListView();
                    additionalServices[0] = listview.isItemChecked(0);
                    additionalServices[1] = listview.isItemChecked(1);
                    additionalServices[2] = listview.isItemChecked(2);
                }
        );
        return builder;
    }

    public String getStringFromXml(int stringId) {
        if(stringId == NewOrderPresenter.RESULT_ERROR) {
            return mFragmentActivity.getString(R.string.neworder_error);
        } else {
            return mFragmentActivity.getString(R.string.neworder_placed_success);
        }
    }

    public void onPlaceAutocompleteCall(int requestCode) {

//        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

//        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                .setLocationBias(bounds)
//                .setTypeFilter(TypeFilter.ADDRESS)
//                .setSessionToken(token)
//                .setQuery(String.valueOf(requestCode))
//                .build();
//
//
//        PlacesClient placesClient = (PlacesClient) NonNull;
//        placesClient.findAutocompletePredictions(request).addOnSuccessListener(new OnSuccessListener<FindAutocompletePredictionsResponse>() {
//           @Override
//            public void onSuccess(FindAutocompletePredictionsResponse response) {
//                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                    Log.i(TAG, prediction.getPlaceId());
//                    Log.i(TAG, prediction.getPrimaryText(null).toString());
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                if (exception instanceof ApiException) {
//                    ApiException apiException = (ApiException) exception;
//                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
//                }
//            }
//        });

        Places.initialize(getFragmentActivity().getApplicationContext(), gMapKey);

        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Start the autocomplete intent.
         autoCompleteIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                 .setHint("Enter your preferred location")
                .build(mFragmentActivity);
//            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
//                    .build();
//            autoCompleteIntent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                    .setFilter(typeFilter)
//                    .setBoundsBias(getDefaultBounds())
//                    .build(mFragmentActivity);
        mPresenter.onReadyStartActivityForResult(requestCode);
    }

    public Intent getPlacesAutocompleteIntent() {
        return autoCompleteIntent;
    }

    public void setFragmentActivity(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public CameraUpdate getCameraUpdate() {
        return cu;
    }

    public String getDistanceString() {
        return distance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResultSuccesful(int requestCode, Intent data) {
        if(requestCode == 1) {
            onAddressFromReceived(String.valueOf(Autocomplete.getPlaceFromIntent(data).getAddress()));
            onFromLatLngReceived(Autocomplete.getPlaceFromIntent(data).getLatLng().latitude,
                    Autocomplete.getPlaceFromIntent(data).getLatLng().longitude);
            sendFromAddressString();
        } else if(requestCode == 2) {
            onAddressToReceived(String.valueOf(Autocomplete.getPlaceFromIntent(data).getAddress()));
            onToLatLngReceived(Autocomplete.getPlaceFromIntent(data).getLatLng().latitude,
                    Autocomplete.getPlaceFromIntent(data).getLatLng().longitude);
            sendToAddressString();
        }
//        if(requestCode == 1 && data.getData()!=null) {
//            onAddressFromReceived(String.valueOf(PlaceAutocomplete.getPlace(mFragmentActivity, data).getAddress()));
//            onFromLatLngReceived(PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().latitude,
//                    PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().longitude);
//            sendFromAddressString();
//        } else if(requestCode == 2 && data.getData()!=null) {
//            onAddressToReceived(String.valueOf(PlaceAutocomplete.getPlace(mFragmentActivity, data).getAddress()));
//            onToLatLngReceived(PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().latitude,
//                    PlaceAutocomplete.getPlace(mFragmentActivity, data).getLatLng().longitude);
//            sendToAddressString();
//        }
    }



    private void sendFromAddressString() {
        mPresenter.onReadyFromText(getAddressFromString());
    }

    private void sendToAddressString() {
        mPresenter.onReadyToText(getAddressToString());
    }

    private String getAddressFromString() {
        return addressFrom;
    }

    private String getAddressToString() {
        return addressTo;
    }

    private void onAddressFromReceived(String addressFrom) {
        this.addressFrom = addressFrom;
    }

    private void onAddressToReceived(String addressTo) {
        this.addressTo = addressTo;
    }

    private void onFromLatLngReceived(double latitude, double longitude) {
        this.mFromAddressLatLng = new LatLng(latitude, longitude);
    }

    private void onToLatLngReceived(double latitude, double longitude) {
        this.mToAddressLatLng = new LatLng(latitude, longitude);
    }

    public boolean areFromToReceived() {
        return mFromAddressLatLng != null && mToAddressLatLng != null;
    }

    /**
     * Returns default coordinates (Benin City, Nigeria)
     * Called from NewOrderPresenter.
     * @return double
     */
    public double getDefaultLatitude() {
        return 6.342450;
    }

    public double getDefaultLongitude() {
        return 5.633840;
    }

    /**
     * Returns default LatLngBounds (area around Poznań city)
     * Called from NewOrderPresenter.
     * @return LatLngBounds
     */
    private LatLngBounds getDefaultBounds() {
        return new LatLngBounds(new LatLng(6.339185, 	5.617447), new LatLng(6.339185, 5.617447));
//        RectangularBounds bounds = RectangularBounds.newInstance(
//                new LatLng(	6.339185, 	5.617447),
//                new LatLng(6.339185, 5.617447));
//        return bounds;
    }


    /**
     * GENERATORS
     */

    private String generateDateString() {
        return new SimpleDateFormat(
                "yyyy-MM-dd HH-mm", Locale.US).
                format(Calendar.getInstance().
                        getTime());
    }

    private String generateTimestampString() {
        return new SimpleDateFormat(
                "yyyyMMddHHmmssSSS", Locale.US)
                .format(Calendar.getInstance()
                        .getTime());
    }

    /**
     * SETTERS AND GETTERS
     *
     */

    public String getNotReadyString() {
        return getFragmentActivity().getString(R.string.neworder_placed_unsuccesful);
    }


    private LatLng getFromAddressLatLng() {
        return mFromAddressLatLng;
    }

    private LatLng getToAddressLatLng() {
        return mToAddressLatLng;
    }

    private String getAddressFrom() {
        return addressFrom;
    }

    private String getAddressTo() {
        return addressTo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }


    private FragmentActivity getFragmentActivity() {
        return mFragmentActivity;
    }

    //Method to download JSON data from url


    public void setGMapKey(String gMapKey) {
        this.gMapKey = gMapKey;
    }

    private String getMapsApiDirectionsUrl() {
        String origin = "origin=" + mFromAddressLatLng.latitude + "," + mFromAddressLatLng.longitude;
        String destination = "destination=" + mToAddressLatLng.latitude + "," + mToAddressLatLng.longitude;
        String sensor = "sensor=false";
        String travelingMode = "mode=driving";
        String output = "json";
        String key = "&key=" + gMapKey;
        //String params = origin + "&" + destination + "&" + travelingMode + "&" + sensor + "&" + key;
        String params = origin + "&" + destination + "&" + sensor + "&" + travelingMode;
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params + key;
        return url;
    }


    public void prepareMapVariables() {
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(getMapsApiDirectionsUrl());
    }

    // Method to download JSON data from url
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){

        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    // Fetches data from url passed
    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
            }
            return data;
        }
        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(@Nullable String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    /** A class to parse the Google Places in JSON format */

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        // Executes in UI thread, after the parsing process
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)

        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            polylineOptions = null;
            distance = "";

            MarkerOptions markerOptions=  new MarkerOptions();
            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points =  new ArrayList<>();
                polylineOptions = new PolylineOptions();
                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);
                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);
                    if(j==0) {
                        distance = point.get("distance");
                        continue;
                    }
                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));

                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                polylineOptions.addAll(points);
                polylineOptions.width(6);
                polylineOptions.color(Color.RED);
                polylineOptions.zIndex(6);
                polylineOptions.geodesic(true);
            }
            if(result.size()<1){
                return;
            }

            // Drawing polyline in the Google Map for the i-th route
            mPresenter.onMapPolylineReady();

            // Set bounds according to the path in polyline.
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(Object point : points) {
                builder.include((LatLng) point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 20;
            cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mPresenter.onCameraUpdateReady();
            mPresenter.onMapUpdatedShowDistance();
        }
    }

}
