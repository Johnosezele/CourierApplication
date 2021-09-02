
package com.zeus_logistics.ZL.helperclasses;


import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeus_logistics.ZL.fragments.CurrentOrderFragment;
import com.zeus_logistics.ZL.interactors.CurrentOrderInteractor;
import com.zeus_logistics.ZL.interactors.PreviousOrderInteractor;
import com.zeus_logistics.ZL.items.NewOrder;
import com.zeus_logistics.ZL.items.OrderReceived;
import com.zeus_logistics.ZL.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class FirebaseOpsHelper {

    private static final String TAG = "MyActivity";
    private FirebaseAuth mAuth;
    private NewOrder mNewOrder;
    private CurrentOrderInteractor mCurrentOrderInteractor;
    private CurrentOrderFragment mCurrentOrderFragment;
    private ArrayList<OrderReceived> mOrdersList;
    private OrderReceived mOrderReceived;
    private static final String ORDER_TAKEN = "taken";
    private static final String ORDER_FINISH = "finished";
    private static final String ORDER_CANCEL = "canceled";
    //private Object FirebaseMessagingService;


    public FirebaseOpsHelper() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseOpsHelper(CurrentOrderInteractor interactor) {
        mAuth = FirebaseAuth.getInstance();
        this.mCurrentOrderInteractor = interactor;
    }

    public FirebaseOpsHelper(CurrentOrderFragment currentOrderFragment) {

        this.mCurrentOrderFragment = currentOrderFragment;
    }

    private FirebaseUser getCurrentUser() {
        mAuth.getCurrentUser();
        return mAuth.getCurrentUser();
    }

    private String getCurrentUserUid() {
        return getCurrentUser().getUid();
    }

    private Task<String> getCurrentUserToken() {
        return FirebaseMessaging.getInstance().getToken();
    }

    private void sendNewOrderToDbWithTimestampAndPhone() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference dbRef = database.getReference();
        dbRef.child("users").child(getCurrentUserUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    mNewOrder.setPhoneNumber(String.valueOf(dataSnapshot.child("phone").getValue()));
                    mNewOrder.setTimeStamp(ServerValue.TIMESTAMP);
                    sendNewOrderToDb(dbRef);
                } else {
                }
            }
            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });
    }

    public void addDbDataToOrderAndSend(NewOrder newOrder) {
        this.mNewOrder = newOrder;
        mNewOrder.setUserToken(String.valueOf(getCurrentUserToken()));
        mNewOrder.setCustomersUid(getCurrentUserUid());
        sendNewOrderToDbWithTimestampAndPhone();
    }

    private void sendNewOrderToDb(DatabaseReference dbRef) {
        dbRef.child("orders").child(mNewOrder.getUserTimeStamp()).setValue(mNewOrder);
        //dbRef.child("orders").child(mNewOrder.getTimeStamp()).setValue(mNewOrder);
    }

    /**
     * METHODS FOR CURRENTORDERFRAGMENT
     */

    /**
     * Looks for order in db based on given timestamp, then
     * gets it and sends it to the next method.
     * @param orderTimestamp
     */
    //djdjdj
    public void getOrderFromDbWithTimestamp(String orderTimestamp) {
        mOrderReceived = new OrderReceived();
       readData(new FirebaseCallback() {
                  @Override
                  public void onCallback(long list) {
                      Log.d(TAG, orderTimestamp);
                  }
              }, orderTimestamp);
    }

    //Method for reading data
    private void readData(FirebaseCallback firebaseCallback, String orderTimestamp){
        long orderTimestampLong = Long.parseLong(orderTimestamp);
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "Before attaching the listener");
        dbRef.child("orders").orderByChild("timeStamp").equalTo(orderTimestampLong)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Log.d(TAG, "inside onDataChange() method");
                            for(DataSnapshot order : dataSnapshot.getChildren()) {
                                mOrderReceived = order.getValue(OrderReceived.class);
                                assert mOrderReceived != null;
                                mCurrentOrderInteractor.setOrderUserTimeStamp(mOrderReceived.getUserTimeStamp());
                            }
                            Log.d(TAG, orderTimestamp);
                            checkIfPhoneNumberAndSend(mOrderReceived);
                            firebaseCallback.onCallback(orderTimestampLong);
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                    }
                });
        Log.d(TAG, "After attaching the listener");
    }
    //Interface for handling Firebase Asynchronous Nature
    private interface FirebaseCallback{
        void onCallback(long list);
    }

    /**
     * Send order back to fragment's main interactor.
     * @param order
     */
    private void onCurrentOrderReceived(OrderReceived order) {
        mCurrentOrderInteractor.onReceivedOrderData(order);
    }

    /**
     * If order's status is ,,taken'' or further, set courier's
     * phone number as order's phone number and send order data to the onCurrentOrderReceived().
     * @param orderReceived
     */
    private void checkIfPhoneNumberAndSend(OrderReceived orderReceived) {
        mOrderReceived = orderReceived;
        if(!mOrderReceived.getType().equals("new")) {
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("users").orderByChild("uid").equalTo(mOrderReceived.getWhoDelivers())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()) {
                                for(DataSnapshot order : dataSnapshot.getChildren()) {
                                    mOrderReceived.setPhoneNumber(String.valueOf(
                                            order.child("phone").getValue()));
                                }
                                onCurrentOrderReceived(mOrderReceived);
                            }
                        }
                        @Override
                        public void onCancelled(@NotNull DatabaseError databaseError) {
                        }
                    });
        } else {
            onCurrentOrderReceived(mOrderReceived);
        }
    }

    public void onCheckWhetherUserIsACourier() {
        getUserRole(getCurrentUser());
    }

    private void getUserRole(FirebaseUser user) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("users").child(user.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String role = String.valueOf(dataSnapshot.child("role").getValue());
                            if(role.equals("courier")) {
                                mCurrentOrderInteractor.onUserRoleReceived(true);
                            } else {
                                mCurrentOrderInteractor.onUserRoleReceived(false);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public void getTimeStamp() {
        mNewOrder = new NewOrder();
        readDataStamp(new FirebaseCallbackStamp() {
            @Override
            public void onCallback(List<String> list) {
                Log.d(TAG, mNewOrder.getTimeStamp().toString());
            }
        });
    }

    //Method that reads timestamtamp from firebase db asynchronously
    private void readDataStamp(FirebaseCallbackStamp firebaseCallbackStamp){
        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, "getTimeStamp: Before attaching the listener");
        dRef.child("orders");
        dRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Log.d(TAG, "onDataChange: Inside onData Changed");
                            for (DataSnapshot order : snapshot.getChildren()) {
                                mOrderReceived = order.getValue(OrderReceived.class);
                                mCurrentOrderFragment.setOrderTimeStamp(mNewOrder.setTimeStamp(String.valueOf(snapshot.child("timeStamp").getValue())));
                            }
                            Log.d(TAG, mNewOrder.getTimeStamp().toString());
                            firebaseCallbackStamp.onCallback(Collections.singletonList(mNewOrder.getTimeStamp().toString()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, error.getMessage());
                    }
                });
        Log.d(TAG, "getTimeStamp: After attaching the listener");
    }

    //Interface for getting the TimeStamp
    private interface FirebaseCallbackStamp{
        void onCallback(List<String> list);
    }

    /**
     * Changes the order status in the database, according to the int value
     * 1 = taken, 2 = finish, 3 = cancel
     * First checks whether order is already taken by someone else.
     * If not, sends it to the method that performs the actual db update.
     */

    public void checkAndChangeOrderStatus(final String userOrderTimestamp, final int whatStatus) {
        DatabaseReference checkingReference = FirebaseDatabase.getInstance().getReference();
        checkingReference.child("orders").child(userOrderTimestamp).child("whoDelivers")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String courierUidInDb = (String) dataSnapshot.getValue();
                            assert courierUidInDb != null;
                            if(!courierUidInDb.equals(getCurrentUserUid()) && !courierUidInDb.equals("")) {
                                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_other_courier));
                                mCurrentOrderInteractor.hideProgressDialog();
                            } else {
                                changeOrderStatus(userOrderTimestamp, whatStatus, courierUidInDb);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                });
    }

    private void changeOrderStatus(String userOrderTimestamp, int whatStatus, String courierUidInDb) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        if(whatStatus == 1) {
            changeOrderToTaken(dbRef, userOrderTimestamp);
        } else if(whatStatus == 2) {
            if(courierUidInDb.equals(getCurrentUserUid())) {
                dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_FINISH);
                mCurrentOrderInteractor.removeOrderFromSharedPreferences();
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_finished));
            } else {
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_first_take_order));
            }
        } else if(whatStatus == 3) {
            if (courierUidInDb.equals(getCurrentUserUid())) {
                dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_CANCEL);
                mCurrentOrderInteractor.removeOrderFromSharedPreferences();
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_canceled));
            } else {
                mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_first_take_order));
            }
        }
    }

    private void changeOrderToTaken(final DatabaseReference dbRef, final String userOrderTimestamp) {
        dbRef.child("users").child(getCurrentUserUid()).child("name").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            dbRef.child("orders").child(userOrderTimestamp).child("type").setValue(ORDER_TAKEN);
                            dbRef.child("orders").child(userOrderTimestamp).child("whoDelivers").setValue(getCurrentUserUid());
                            dbRef.child("orders").child(userOrderTimestamp).child("whoDeliversName").setValue(String.valueOf(dataSnapshot.getValue()));
                            mCurrentOrderInteractor.sendToastCall(mCurrentOrderInteractor.getInteractorContext().getString(R.string.toast_order_taken));
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                }
        );
    }

    public void onPreviousOrdersCall(final PreviousOrderInteractor interactor) {
        mOrdersList = new ArrayList<>();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("orders").orderByChild("customersUid").equalTo(getCurrentUserUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for(DataSnapshot order : dataSnapshot.getChildren()) {
                                OrderReceived orderFromDb = order.getValue(OrderReceived.class);
                                mOrdersList.add(orderFromDb);
                                interactor.onReceivedPreviousOrdersData(mOrdersList);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                });
    }
}
