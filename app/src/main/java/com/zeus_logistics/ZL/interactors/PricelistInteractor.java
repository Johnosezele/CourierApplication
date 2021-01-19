package com.zeus_logistics.ZL.interactors;


import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zeus_logistics.ZL.adapters.BasePriceAdapterByGenre;
import com.zeus_logistics.ZL.items.PriceItem;
import com.zeus_logistics.ZL.items.PriceList;
import com.zeus_logistics.ZL.presenters.PricelistPresenter;

import java.util.List;


public class PricelistInteractor {

    private Context mContext;

    public PricelistInteractor(PricelistPresenter presenter) {
        PricelistPresenter mPresenter = presenter;
    }

    /**
     * Prepares List with priceitems in correct order.
     * Dummy list within PriceList.class.
     * @return
     */
    private List<PriceItem> preparePrices() {
        PriceList priceList = PriceList.get();
        return priceList.getPrices();
    }

    public void onPrepareRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(prepareAdapter());
    }

    /**
     * Called from this interactor.
     * Creates and returns an adapter (calls getPriceItemList to prepare data)
     * @return
     */
    private BasePriceAdapterByGenre prepareAdapter() {
        return new BasePriceAdapterByGenre(getPriceItemList());
    }

    /**
     * Called from this presenter.
     * Retrieves priceitems list from PricelistInteractor.
     * @return
     */
    private List<PriceItem> getPriceItemList() {
        return preparePrices();
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    private Context getContext() {
        return mContext;
    }


}
