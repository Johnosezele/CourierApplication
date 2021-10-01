package com.zeus_logistics.ZL.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.zeus_logistics.ZL.items.User;
import com.zeus_logistics.ZL.presenters.ProfileEditPresenter;
import com.zeus_logistics.ZL.R;


public class ProfileEditFragment extends Fragment {

    private ProfileEditPresenter mPresenter;
    private TextInputEditText mEditTextPhone;
    private TextInputEditText mEditTextName;
    private TextInputEditText mEditTextEmail;
//    private EditText mNameEdit;
//    private EditText mPhoneEdit;
//    private EditText mMailEdit;
    private Button mAcceptButton;
    private ProgressDialog mProgressDialog;
    private User mUser;

    public ProfileEditFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mEditTextPhone = view.findViewById(R.id.profileedit_telephone_edit_text);
        mEditTextName = view.findViewById(R.id.profileedit_name_edit_text);
        mEditTextEmail = view.findViewById(R.id.profileedit_mail_edit_text);
//        mNameEdit = (EditText) view.findViewById(R.id.profileedit_name_edit_text);
//        mPhoneEdit = (EditText) view.findViewById(R.id.profileedit_telephone_edit_text);
//        mMailEdit = (EditText) view.findViewById(R.id.profileedit_mail_edit_text);
        mAcceptButton = (Button) view.findViewById(R.id.profileedit_accept_button);

        mPresenter = new ProfileEditPresenter(this);
        mPresenter.initialize();
    }

    public void callUpdateHeader(){
        //Nav drawer listener
        mUser = new User();
        DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                mUser = new User();
                NavigationView nvDrawer = (NavigationView) getActivity().findViewById(R.id.nvView);
                View headerView = nvDrawer.getHeaderView(0);
                TextView navUserName = headerView.findViewById(R.id.navUserName);
                navUserName.setText(mUser.getName());
            }
        });

    }

    public void setListeners() {
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.setListenerForAccept(mEditTextName.getText().toString(),
                        mEditTextPhone.getText().toString(),
                        mEditTextEmail.getText().toString());
            }
        });
        mEditTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_name), mEditTextName.getText().toString(), 1);
            }
        });
        mEditTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_phone), mEditTextPhone.getText().toString(), 2);
            }
        });
        mEditTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.checkIfEditTextValid(getString(R.string.profile_error_mail), mEditTextEmail.getText().toString(), 3);
            }
        });
    }

    /**
     * Method for producing toasts when event of a numberOfMessage type happened.
     * @param numberOfMessage: 1 = error in submission, 2 = successful submission
     */
    public void makeToast(int numberOfMessage) {
        String text;
        if(numberOfMessage == 1) {
            text = getString(R.string.profile_error_submit);
        } else {
            text = getString(R.string.profile_toast_update);
        }
        Toast toast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public void setTexts(String name, String phone, String mail) {
        mEditTextName.setText(name);
        mEditTextPhone.setText(phone);
        mEditTextEmail.setText(mail);
    }

    public void setEditTextErrors(String errorMessage, int dataNumber) {
        if(dataNumber == 1) {
            mEditTextName.setError(errorMessage);
        } else if(dataNumber == 2) {
            mEditTextPhone.setError(errorMessage);
        } else if(dataNumber == 3) {
            mEditTextEmail.setError(errorMessage);
        }
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
            mProgressDialog.hide();
        }
    }

    public void hideProgressDialog() {
        mProgressDialog.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
