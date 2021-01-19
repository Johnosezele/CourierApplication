package com.zeus_logistics.ZL.presenters;

import com.zeus_logistics.ZL.fragments.PricelistFragment;
import com.zeus_logistics.ZL.interactors.PricelistInteractor;


public class PricelistPresenter {

    private PricelistInteractor mModel;
    private PricelistFragment mView;

    public PricelistPresenter(PricelistFragment pricelistFragment) {
        this.mModel = new PricelistInteractor(this);
        this.mView = pricelistFragment;
    }

    /**
     * Called from PricelistFragment
     * Sets adapter for @param.
     * @param
     */
    public void initialize() {
        mModel.setContext(mView.getContextFromFragment());
        mModel.onPrepareRecyclerView(mView.getRecyclerView());
    }

    public void detachView() {
        mView = null;
    }

}
