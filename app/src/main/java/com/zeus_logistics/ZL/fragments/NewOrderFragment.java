package com.zeus_logistics.ZL.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import android.os.HandlerThread;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.zeus_logistics.ZL.MapFromFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zeus_logistics.ZL.MainActivity;
import com.zeus_logistics.ZL.presenters.NewOrderPresenter;
import com.zeus_logistics.ZL.R;

import static com.google.android.material.resources.MaterialResources.getDrawable;


public class NewOrderFragment extends Fragment implements OnMapReadyCallback {


    private MapView mMapView;
    private NewOrderPresenter mPresenter;
    private GoogleMap mGoogleMap;
    private TextView mDistanceView;
    private Button mCalculateButton;
    private Button mCallButton;
    private Button mAdditionalButton;
    private TextView placesFrom;
    private TextView placesTo;
    private Intent mOnActivityResultIntent;
    ConstraintLayout bottomSheetLayout;
    BottomSheetDialog bottomSheetDialog;
    Button distanceButton;
    Button showDistanceDialog;
    private ProgressDialog mProgressDialog;
    private Dialog mDialog;
//    private MapView mMapView1;


    public NewOrderFragment() {
    }

    public static Fragment newInstance() {
        return new NewOrderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayBottomSheet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_neworder, container, false);
    }

    private void displayBottomSheet() {

        // creating a variable for our bottom sheet dialog.
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        // passing a layout file for our bottom sheet dialog.
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet, bottomSheetLayout);
        // passing our layout file to our bottom sheet dialog.
        bottomSheetDialog.setContentView(layout);
        // below line is to set our bottom sheet dialog as cancelable.
        bottomSheetDialog.setCancelable(true);
        // below line is to set our bottom sheet cancelable.
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        // below line is to display our bottom sheet dialog.
        bottomSheetDialog.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mPresenter = new NewOrderPresenter(this);
        mPresenter.setGMapsUrl(getString(R.string.google_maps_key));
        mPresenter.sendFragmentActivityToInteractor();

        mDistanceView = (TextView) view.findViewById(R.id.neworder_textview_summary);
        //mCalculateButton = (Button) view.findViewById(R.id.neworder_button_calculate);
        mAdditionalButton = (Button) view.findViewById(R.id.neworder_button_additional);
        distanceButton = (Button) bottomSheetDialog.findViewById(R.id.distanceButton);
        //button that calls distance dialog
        //showDistanceDialog = (Button) mDialog.findViewById(R.id.distanceButton);
        mCallButton = (Button) view.findViewById(R.id.neworder_button_call);
        placesFrom = (TextView) bottomSheetDialog.findViewById(R.id.from);
        placesTo = (TextView) bottomSheetDialog.findViewById(R.id.to);
        mMapView = (MapView) view.findViewById(R.id.neworder_map);
//        mMapView1 = (MapView) view.findViewById(R.id.neworderMap);
        mMapView.onCreate(savedInstanceState);

        mPresenter.onMapAsyncCall();
        setListeners();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mOnActivityResultIntent = data;
        mPresenter.onActivityResultCalled(requestCode, resultCode);
    }

    public Intent getOnActivityResultIntent() {
        return mOnActivityResultIntent;
    }

    public void setDistanceView(@Nullable String distance) {
        mDistanceView.setText(getString(R.string.neworder_summary_start) + " " + distance +
        getString(R.string.neworder_summary_mid));

    }

    public void setFromText(String address) {
        placesFrom.setText(address);
    }

    public void setToText(String address) {
        placesTo.setText(address);
    }

    public void moveCameraTo(double lat, double lng) {
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(10)
                .build()));
    }

    // STARTING AGAIN FFS

    private void setListeners() {
        placesFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(1);
//                Fragment fragment = new MapFromFragment();
//                FragmentManager fm = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fm.beginTransaction();
//                fragmentTransaction.replace(R.id.place_autocomplete_frame, fragment)
//                .commit();
            }
        });
        placesTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForPlaceAutocompleteCall(2);
            }
        });
        mAdditionalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForAdditionalServices();
            }
        });
        distanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //bottomSheetDialog.dismiss();
                if(placesFrom.getText().toString().matches("") || placesTo.getText().toString().matches("")){
                    Toast.makeText(getContext(), "Enter complete location", Toast.LENGTH_LONG).show();
              } else if (placesFrom.getText().toString().matches("") && placesTo.getText().toString().matches("")){
                    Toast.makeText(getContext(), "Enter your location", Toast.LENGTH_LONG).show();
                } else {
                    bottomSheetDialog.dismiss();
                    showProgressDialog();
                    mPresenter.createListenerForCalculate();
                }
            }
        });
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.createListenerForCall();
            }
        });
    }

//    private void distanceDialog() {
//        //Create the Dialog here
//        mDialog = new Dialog(getContext());
//        mDialog.setContentView(R.layout.distance_dialog);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.distance_dialog));
//        }
//        mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mDialog.setCancelable(false); //Optional
//        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //Setting the animations to dialog
//    }

//    private Drawable getDrawable(int distance_dialog) {
//        return null;
//    }

    /**
     * Prepares and shows ProgressDialog upon call (while the data is loading).
     */
    public void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.progress_dialog_loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        mProgressDialog.hide();
    }

    public FragmentActivity getParentActivity() {
        return getActivity();
    }

    public void mapAsyncCall() {
        mMapView.getMapAsync(this);
    }

    public void setMapClear() {
        mGoogleMap.clear();
    }

    public void onMapPolylineAdded(@Nullable PolylineOptions polylineOptions) {
        mGoogleMap.addPolyline(polylineOptions);
    }

    public void animateCamera(@Nullable CameraUpdate cu) {
        mGoogleMap.moveCamera(cu);
        mGoogleMap.animateCamera(cu, 1000, null);
    }

    public void makeToast(String message) {
        Toast toast = Toast.makeText(getContext(),
                message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        builder.show();
    }

    @Override
    public void onMapReady(@Nullable GoogleMap map) {
        mGoogleMap = map;
        mPresenter.setMapView();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public final void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public final void onPause() {
        super.onPause();
        mMapView.onPause();
    }

}
