package com.zeus_logistics.ZL.interactors;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.messaging.FirebaseMessaging;
import com.zeus_logistics.ZL.items.User;
import com.zeus_logistics.ZL.presenters.ProfilePresenter;

import org.jetbrains.annotations.NotNull;

public class ProfileInteractor {

    private ProfilePresenter mPresenter;
    private DatabaseReference mDatabaseReference;
    private User mUser;


    public ProfileInteractor(ProfilePresenter mPresenter) {
        this.mPresenter = mPresenter;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Get logged user data from DB, called from the profilePresenter which is called
     * from the ProfileFragment onViewCreated.
     * Returns user data to the presenter.
     */
    public void getUserDataFromDb() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String uid = user.getUid();
        mUser = new User();
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                            mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                            mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                            mUser.setUid(uid);
                            mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                     //       checkAndSetCurrentUserToken(uid);
                            mPresenter.onReceivedUserDataFromDb(mUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                }
        );
    }

    // Precaution, in case user's token get changed or removed
//    private String checkAndSetCurrentUserToken(String userUid) {
//        String instanceId = FirebaseInstanceId.getInstance().getToken();
//        if(instanceId != null) {
//            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//            mDatabaseReference.child("users")
//                    .child(userUid)
//                    .child("instanceId")
//                    .setValue(instanceId);
//        }
//        return instanceId;
//    }
//    private FirebaseMessaging checkAndSetCurrentUserToken(String userUid) {
//        FirebaseMessaging instanceId = FirebaseMessaging.getInstance();
//        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mDatabaseReference.child("users")
//                .child(userUid)
//                .child("instanceId")
//                .setValue(instanceId);
//        return instanceId;
//    }

}
