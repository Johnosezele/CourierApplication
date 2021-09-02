package com.zeus_logistics.ZL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;
//import androidx.multidex.MultiDex;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zeus_logistics.ZL.firebaseservices.MyFirebaseMessagingService;
import com.zeus_logistics.ZL.fragments.AboutFragment;
import com.zeus_logistics.ZL.fragments.CurrentOrderFragment;
import com.zeus_logistics.ZL.fragments.NewOrderFragment;
import com.zeus_logistics.ZL.fragments.PreviousOrdersFragment;
import com.zeus_logistics.ZL.fragments.PricelistFragment;
import com.zeus_logistics.ZL.fragments.ProfileEditFragment;
import com.zeus_logistics.ZL.fragments.ProfileFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    FloatingActionButton floatingActionButton;
    ConstraintLayout bottomSheetLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        // Set SharedPreferences
        mSharedPreferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);

        //Hooks(earlier implementation)
        //floatingActionButton = findViewById(R.id.floatingActionButton2);

        // Set a toolbar to replace the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Find drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Tie DrawerLayout events to the ActionBarToggle.
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        // Tie DrawerLayout events to the FAB (earlier implementation).
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mDrawer.isDrawerOpen(GravityCompat.START)) {
//                    mDrawer.closeDrawer(GravityCompat.START);
//                } else {
//                    mDrawer.openDrawer(GravityCompat.START);
//                }
//            }
//        });


        // Attach listener to drawer menuitems and handle user selections.
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        //View drawerHeader = nvDrawer.inflateHeaderView(R.layout.drawer_header);
        setupDrawerContent(nvDrawer);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() != null) {
                if(getIntent().getExtras() == null) {
                    // Begin transaction
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    // Replace the contents of the container with the new fragment
                    ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
                    // Complete the changes added above.
                    ft.commit();
                } else {
                    Bundle extras = getIntent().getExtras();
                    if(extras.containsKey(MyFirebaseMessagingService.NOTIFICATION_TYPE)) {
                        String type = extras.getString(MyFirebaseMessagingService.NOTIFICATION_TYPE);
                        if (type.equals("new") || type.equals("taken")) {
                            saveData(extras.getString(MyFirebaseMessagingService.NOTIFICATION_TIMESTAMP));
                            Class fragmentClass = CurrentOrderFragment.class;
                            Fragment fragment = null;
                            try {
                                fragment = (Fragment) fragmentClass.newInstance();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.fragment_placeholder, fragment);
                            ft.commit();
                        } else if(type.equals("finished")) {
                            saveData(PREFERENCES_EMPTY);
                            // Begin transaction
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_placeholder, new ProfileFragment());
                            // Complete the changes added above.
                            ft.commit();
                        }
                    } else {
                        // Begin transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
                        // Complete the changes added above.
                        ft.commit();
                    }
                }
            } else {
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        };

        // initializing our variables.
        bottomSheetLayout = findViewById(R.id.buttomSheetContainer);
        // calling method to display bottom sheet.
       // displayBottomSheet();
    }

//    private void displayBottomSheet() {
//
//        // creating a variable for our bottom sheet dialog.
//        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
//        // passing a layout file for our bottom sheet dialog.
//        View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_bottom_sheet, bottomSheetLayout);
//        // passing our layout file to our bottom sheet dialog.
//        bottomSheetDialog.setContentView(layout);
//        // below line is to set our bottom sheet dialog as cancelable.
//        bottomSheetDialog.setCancelable(true);
//        // below line is to set our bottom sheet cancelable.
//        bottomSheetDialog.setCanceledOnTouchOutside(false);
//        // below line is to display our bottom sheet dialog.
//        bottomSheetDialog.show();
//    }

    private void saveData(String data) {
        SharedPreferences.Editor preferencesEditor = mSharedPreferences.edit();
        preferencesEditor.putString(PREFERENCES_TEXT_FIELD, data);
        preferencesEditor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
    }

    // Attach item selection listener to the drawer
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                item -> {
                    selectDrawerItem(item);
                    return true;
                }
        );
    }

    // Creates a fragment and specifies it (according to selected menuItem), then shows it.
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_profileedit:
                fragmentClass = ProfileEditFragment.class;
                break;
            case R.id.nav_pricelist:
                fragmentClass = PricelistFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_neworder:
                fragmentClass = NewOrderFragment.class;
                break;
            case R.id.nav_previous_orders:
                fragmentClass = PreviousOrdersFragment.class;
                break;
            case R.id.nav_current_order:
                fragmentClass = CurrentOrderFragment.class;
                break;
            case R.id.nav_logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                mAuth.signOut();
            default:
                fragmentClass = NewOrderFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment)
//                .addToBackStack(String.valueOf(fragment))
                .commit();

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    /**
     *
     * Actionbar/toolbar and drawer relations established here.
     *
     */

    // Actionbar/Toolbar home/up button opens/close the drawer
    // Allowing ActionBarToggle to handle the events.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Ties actionbar/toolbar with drawerlayout
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // Needed to show the hamburger icon
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

}
