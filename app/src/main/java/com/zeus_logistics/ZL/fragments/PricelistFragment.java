package com.zeus_logistics.ZL.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.zeus_logistics.ZL.presenters.PricelistPresenter;
import com.zeus_logistics.ZL.R;

public class PricelistFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private PricelistPresenter mPresenter;

    public PricelistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pricelist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.pricelist_recyclerview);

        mPresenter = new PricelistPresenter(this);
        mPresenter.initialize();
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public Context getContextFromFragment() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
