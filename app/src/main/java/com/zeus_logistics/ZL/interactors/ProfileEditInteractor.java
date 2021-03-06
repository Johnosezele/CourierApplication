package com.zeus_logistics.ZL.interactors;


import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zeus_logistics.ZL.R;
import com.zeus_logistics.ZL.activities.MainActivity;
import com.zeus_logistics.ZL.items.User;
import com.zeus_logistics.ZL.presenters.ProfileEditPresenter;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ProfileEditInteractor {

    private ProfileEditPresenter mPresenterEdit;
    private DatabaseReference mDatabaseReference;
    private User mUser;
    public MainActivity mMainActivity;

    public ProfileEditInteractor(ProfileEditPresenter mPresenterEdit) {
        this.mPresenterEdit = mPresenterEdit;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        this.mUser = new User();
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
        mDatabaseReference.child("users").child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        mUser.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                        mUser.setPhone(String.valueOf(dataSnapshot.child("phone").getValue()));
                        mUser.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                        mUser.setUid(uid);
                        mUser.setRole(String.valueOf(dataSnapshot.child("role").getValue()));
                        //checkAndSetCurrentUserToken(uid);
                        mPresenterEdit.onReceivedUserDataFromDb(mUser);
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {

                    }
                }
        );
    }


    // Precaution, in case user's token get changed or removed
    private FirebaseMessaging checkAndSetCurrentUserToken(String userUid) {
        FirebaseMessaging instanceId = FirebaseMessaging.getInstance();
        if(instanceId != null) {
            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.child("users")
                    .child(userUid)
                    .child("instanceId")
                    .setValue(instanceId);
        }
        return instanceId;
    }

    public void setUserDetail(int dataNumber, String value) {
        if(dataNumber == 3) {
            mUser.setEmail(value);
        } else if(dataNumber == 2) {
            mUser.setPhone(value);
        } else if(dataNumber == 1) {
            mUser.setName(value);
        }
    }

    /**
     * Receives a call from editPresenter upon user's button click.
     * Updates user data in db.
     */
    public void updateUserDb() {
        mMainActivity = new MainActivity();
        final String userUid = mUser.getUid();
        mDatabaseReference.child("users").child(userUid).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        //DATA ALREADY EXISTS
                        String oldMail = String.valueOf(dataSnapshot.child("email").getValue());
                        if(!oldMail.equals(mUser.getEmail())) {
                            resetUserAuthMail(mUser.getEmail());
                        }
                        mDatabaseReference.child("users").child(userUid).setValue(mUser);

                        mPresenterEdit.sendToastRequestToView(2);
                    }
                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                        Log.d("warning", "oncancelled");
                    }
                });
    }


    private void resetUserAuthMail(String newmail) {
        FirebaseUser loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        loggedUser.updateEmail(newmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d("MAILRESET", "Succesful");
                        }
                    }
                });
    }

    public static boolean isValidEmailAddress(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
