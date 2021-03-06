package com.zeus_logistics.ZL.presenters;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.zeus_logistics.ZL.fragments.CurrentOrderFragment;
import com.zeus_logistics.ZL.interactors.CurrentOrderInteractor;
import com.zeus_logistics.ZL.items.OrderReceived;


public class CurrentOrderPresenter {

    private CurrentOrderFragment mView;
    private CurrentOrderInteractor mInteractor;

    public CurrentOrderPresenter(CurrentOrderFragment fragment) {
        this.mView = fragment;
        this.mInteractor = new CurrentOrderInteractor(this);
        setInteractorContext();
    }

    /**
     * Detaches this presenter from the view.
     */
    public void detachView() {
        this.mView = null;
    }

    /**
     * Initial method.
     * Informs the view to show progress dialog while loading the data.
     * Sends request to interactor for data about current order, providing
     * interactor with order's timestamp.
     * Sends request to interactor to check whether user is a courier.
     */
    public void initialize() {
        mView.showProgressDialog();
        mView.getOrderTimeStamp();
       // mInteractor.getUserTimeStampFromDb();
        //mInteractor.getOrderData(mView.getOrderTimeStamp());
        //mInteractor.checkWhetherLoggedUserIsCourier();
    }
    public void recieveTimeStampFromOrderFragment(String timeS){
        mInteractor.getOrderData(timeS);
        mInteractor.checkWhetherLoggedUserIsCourier();
    }


    /**
     * Upon receiving order strings, sends it to view in order to display them to the user.
     * @param from
     * @param to
     * @param distance
     * @param phone
     * @param date
     * @param additionalOne
     * @param additionalTwo
     * @param additionalThree
     */
    public void onOrderStringsReceived(String from, String to, String distance, String phone,
                                       String date, boolean additionalOne, boolean additionalTwo,
                                       boolean additionalThree) {
        mView.setViewsWithData(from, to, distance, phone, date, additionalOne,
                additionalTwo, additionalThree);
        mView.hideProgressDialog();
    }

    /**
     * Informs view that it needs to inflate courier buttons
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void inflateCourierButtons() {
        mView.onInflateCourierButtonsCall();
    }

    /**
     * Informs interactor that one of the (whichButton) courier buttons was pressed.
     */
    public void onCourierButtonClick(int whichButton) {
        mInteractor.checkIfSureToChangeOrderStatus(mView.getFragmentContext(),
                whichButton);
    }
    /**
     * Receives a call when alertdialog is ready for the view.
     * Informs the view to show alertdialog from the interactor.
     */
    public void onCreatedAlertDialog() {
        mView.showDialog(mInteractor.getAlertDialog());
    }

    /**
     * Receives a call from the interactor when the order is either canceled or finished.
     * Informs the view in order for order's timestamp removal from the sharedpreferences.
     */
    public void removeOrderFromSharedPreferences() {
        mView.removeOrderFromSharedPreferences();
    }

    /**
     * Receives a call from the interactor when toast should be displayed.
     * Specifies what kind of toast it should be with @param.
     * @param whichType
     */
    public void sendToast(String whichType) {
        mView.makeToast(whichType);
    }

    public void hideProgressDialog() {
        mView.hideProgressDialog();
    }

    /**
     * Sends context to interactor
     */
    private void setInteractorContext() {
        mInteractor.setInteractorContext(mView.getContext());
    }

//send usertimestamp to the
    public void onReceivedUserTimestampFromDb(OrderReceived order) {
           mInteractor.onReceivedOrderData(order);
    }
}
