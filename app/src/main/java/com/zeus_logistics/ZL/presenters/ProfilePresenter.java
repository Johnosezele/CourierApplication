package com.zeus_logistics.ZL.presenters;


import com.zeus_logistics.ZL.fragments.ProfileFragment;
import com.zeus_logistics.ZL.interactors.ProfileInteractor;
import com.zeus_logistics.ZL.items.User;

public class ProfilePresenter {

    private ProfileInteractor mModel;
    private ProfileFragment mView;

    public ProfilePresenter(ProfileFragment view) {
        this.mModel = new ProfileInteractor(this);
        this.mView = view;
    }

    /**
     * Called by fragment instantiation {ProfileFragment}
     * Sends a request for user data from db based on his UID
     */
    public void onUserDetailsRequired() {
        mView.showProgressDialog();
        mModel.getUserDataFromDb();
    }

    // RETURNING FROM MODEL

    /**
     * Receives call from the model when User data is acquired from db
     */
    public void onReceivedUserDataFromDb(User user) {
        showUserDataInViews(user);
    }

    /**
     * Sends user details to the view's textfields.
     * @param user
     */
    private void showUserDataInViews(User user) {
        mView.setTexts(user.getName(), user.getPhone(), user.getEmail());
        hideProgressDialog();
    }

    private void hideProgressDialog() {
        mView.hideProgressDialog();
    }

    public void detachView() {
        mView = null;
    }


}
