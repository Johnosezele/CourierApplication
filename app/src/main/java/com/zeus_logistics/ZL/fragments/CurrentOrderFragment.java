package com.zeus_logistics.ZL.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zeus_logistics.ZL.interactors.CurrentOrderInteractor;
import com.zeus_logistics.ZL.items.NewOrder;
import com.zeus_logistics.ZL.items.OrderReceived;
import com.zeus_logistics.ZL.presenters.CurrentOrderPresenter;
import com.zeus_logistics.ZL.R;

import java.util.Objects;


public class CurrentOrderFragment extends Fragment {
    private OrderReceived mOrderReceived;
    public NewOrder mNewOrder;
    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    public String mUserTimeStamp;
    private String orderTimeStamp;
    private SharedPreferences mSharedPreferences;
    private TextView mDateTextView;
    private TextView mFromTextView;
    private TextView mToTextView;
    private TextView mDistanceTextView;
    private TextView mPhoneTextView;
    private CheckedTextView mFirstAdditionalServiceTextView;
    private CheckedTextView mSecondAdditionalServiceTextView;
    private CheckedTextView mThirdAdditionalServiceTextView;
    private ProgressDialog mProgressDialog;
    private CurrentOrderPresenter mPresenter;

    public CurrentOrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        FirebaseOpsHelper fbHelper = new FirebaseOpsHelper(this);
//        fbHelper.getTimeStamp();
        //getOrderTimeStamp();
        mSharedPreferences = this.requireActivity().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString(PREFERENCES_TEXT_FIELD, orderTimeStamp);
        //editor.apply();
        orderTimeStamp = mSharedPreferences.getString(PREFERENCES_TEXT_FIELD, PREFERENCES_EMPTY);

        //FirebaseDatabase.getInstance().getReference().child("orders").orderByChild("timeStamp").equalTo()

        return inflater.inflate(R.layout.fragment_current_order, container, false);

    }
//
//    private String getTimeStamp() {
//        return null;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void getTimeStamp(OrderReceived orderReceived) {
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
//        mOrderReceived = new OrderReceived(orderReceived);
//        dbRef.child("orders").orderByChild("timeStamp").equalTo(mOrderReceived.getTimeStamp())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()) {
//                            for(DataSnapshot order : dataSnapshot.getChildren()) {
//                                mOrderReceived.setTimeStamp(
//                                        (Long) order.child("timeStamp").getValue());
//                            }
//                        }
//                    }
//                    @Override
//                    public void onCancelled(@NotNull DatabaseError databaseError) {
//                    }
//                });
//
//
//
//    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mDateTextView = view.findViewById(R.id.progress_body_date);
        mFromTextView = view.findViewById(R.id.progress_body_from_txt);
        mToTextView = view.findViewById(R.id.progress_body_to_txt);
        mDistanceTextView = view.findViewById(R.id.progress_body_distance);
        mPhoneTextView = view.findViewById(R.id.progress_body_phone_txt);
        mFirstAdditionalServiceTextView = view.findViewById(R.id.progress_check_1);
        mSecondAdditionalServiceTextView = view.findViewById(R.id.progress_check_2);
        mThirdAdditionalServiceTextView = view.findViewById(R.id.progress_check_3);

        mPresenter = new CurrentOrderPresenter(this);
        mPresenter.initialize();
    }

    /**
     * Sets views' text with provided strings about the current order.
     * @param from
     * @param to
     * @param distance
     * @param phone
     * @param date
     * @param additionalOne
     * @param additionalTwo
     * @param additionalThree
     */
    public void setViewsWithData(String from, String to, String distance, String phone,
                                 String date, boolean additionalOne, boolean additionalTwo,
                                 boolean additionalThree) {
        mDateTextView.setText(date);
        mFromTextView.setText(from);
        mToTextView.setText(to);
        mDistanceTextView.setText(distance);
        mPhoneTextView.setText(phone);
        mFirstAdditionalServiceTextView.setChecked(additionalOne);
        mSecondAdditionalServiceTextView.setChecked(additionalTwo);
        mThirdAdditionalServiceTextView.setChecked(additionalThree);
    }

    /**
     * Method launches only if the user is a courier. Inflates the courier buttons.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onInflateCourierButtonsCall() {
        LayoutInflater vi = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View v = vi.inflate(R.layout.current_order_courier_buttons, null);

        Button buttonAccept = v.findViewById(R.id.progress_accept_button);
        Button buttonFinish = v.findViewById(R.id.progress_success_button);
        Button buttonCancel = v.findViewById(R.id.progress_cancel_button);


        ViewGroup insertPoint = getActivity().findViewById(R.id.progress_buttons_insert_point);
        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setButtonsListeners(buttonAccept, buttonFinish, buttonCancel);
    }

    /**
     * ClickListeners for courier's buttons.
     * @param accept
     * @param finish
     * @param cancel
     */
    private void setButtonsListeners(Button accept, Button finish, Button cancel) {
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(1);
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(2);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onCourierButtonClick(3);
            }
        });
    }

    /**
     * Displays alertDialog provided to the view.
     * @param alertDialog
     */
    public void showDialog(AlertDialog alertDialog) {
        alertDialog.show();
    }

    /**
     * Sets sharedpreferences current order string value to 'empty'.
     */
    public void removeOrderFromSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(PREFERENCES_TEXT_FIELD, PREFERENCES_EMPTY);
        editor.apply();
    }

    /**
     * Shows toast to the user.
     */
    public void makeToast(String whichType) {
        Toast toast = Toast.makeText(getContext(), whichType, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * Prepares and shows ProgressDialog upon call (while the data is loading).
     */
    public void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.progress_dialog_loading));
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        mProgressDialog.hide();
    }

    public void getOrderTimeStamp() {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("orders").orderByChild("timeStamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for(DataSnapshot order : dataSnapshot.getChildren()) {
                                mOrderReceived = order.getValue(OrderReceived.class);
                                //mOrderReceived.setTimeStamp(Objects.requireNonNull(order.child("timeStamp").getValue(OrderReceived.class)).getTimeStamp());
                                assert mOrderReceived != null;
                                long timeStamp = mOrderReceived.getTimeStamp();
                                String timeS = Long.toString(timeStamp);
                                ordertimeStamp(timeS);
//                                mInteractor.getOrderData();
//                                mInteractor.checkWhetherLoggedUserIsCourier();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void ordertimeStamp(String timeS) {
        mPresenter.recieveTimeStampFromOrderFragment(timeS);
        orderTimeStamp = timeS;
    }


    public Context getFragmentContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

//    public Map<String, String> setOrderTimeStamp(Map<String, String> timeStamp) {
//        return timeStamp;
//    }
}
