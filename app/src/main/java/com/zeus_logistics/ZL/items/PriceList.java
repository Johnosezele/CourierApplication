package com.zeus_logistics.ZL.items;

import java.util.ArrayList;
import java.util.List;

/**
 * PriceList implementation is really raw. If you want to use it, you should probably change it.
 * This is just to show an example in PricelistFragment.
 */

public class PriceList {

    private static PriceList sPriceList;
    private List<PriceItem> mPriceItems;

    public static PriceList get() {
        if(sPriceList == null) {
            sPriceList = new PriceList();
        }
        return sPriceList;
    }

    private PriceList() {
        mPriceItems = new ArrayList<>();
        List<String> mPricelistList = createDescriptions();
        List<String> mPricelistPrices = createPrices();
        for(int i=0; i<6;i++) {
            PriceItem priceItem = new PriceItem();
            priceItem.setDescription(mPricelistList.get(i));
            priceItem.setPrice(mPricelistPrices.get(i));
            if(i<=2) {
//                priceItem.setGenre("Odległość");
                priceItem.setGenre("Distance");
            } else {
//                priceItem.setGenre("Usługi dodatkowe");Additional services
                priceItem.setGenre("Additional services");
            }
            mPriceItems.add(priceItem);
        }
    }

    public List<PriceItem> getPrices() {
        return mPriceItems;
    }

    public PriceItem getPriceItemByGenre(String genre) {
        for(PriceItem item : mPriceItems) {
            if(item.getGenre().equals(genre)) {
                return item;
            }
        }
        return null;
    }

    //RAW METHODS FOR STRINGS IMPLEMENTATION. Don't use them outside debugging process.
    private List<String> createDescriptions() {
        List<String> mPricelistList = new ArrayList<>();
//        mPricelistList.add("Centrum");
        //mPricelistList.add("Center");
//        mPricelistList.add("Do 5km");
        mPricelistList.add("Up to 9km");
//        mPricelistList.add("Od 5 do 10km");
        mPricelistList.add("From 10 to 20km");
        mPricelistList.add("20 km and above");
//        mPricelistList.add("Od 10 do 15km");
        //mPricelistList.add("From 10 to 15km");
//        mPricelistList.add("Od 15km do granic administracyjnych");
       // mPricelistList.add("From 15 km to the administrative boundaries");
//        mPricelistList.add("Do 10km od granic administracyjnych");
        //mPricelistList.add("Up to 10 km from the administrative borders");
//        mPricelistList.add("Do 30km od granic administracyjnych");
        //mPricelistList.add("Up to 30 km from the administrative borders");
//        mPricelistList.add("Express - doręczenie do 1 godziny");
        mPricelistList.add("Road express");
        mPricelistList.add("Express - delivery up to 1 hour");
//        mPricelistList.add("Superexpress - doręczenie poniżej 1 godziny");
        mPricelistList.add("Super express - delivery in less than 1 hour");
//        mPricelistList.add("Usługi w nadgodzinach (18:00-8:00) oraz w soboty i niedziele");
        //mPricelistList.add("Overtime services (6:00 p.m. - 8:00 a.m.) and on Saturdays and Sundays");
//        mPricelistList.add("Ekspres drogowy");
        //mPricelistList.add("Road express");
//        mPricelistList.add("Powiadomienie SMS o doręczeniu");
        //mPricelistList.add("SMS notification of delivery");
//        mPricelistList.add("Nadanie na PKS/PKP");
        //mPricelistList.add("Shipment to the PKS / PKP");
//        mPricelistList.add("Oczekiwanie kuriera powyżej 10 minut");
        //mPricelistList.add("Waiting for the courier over 10 minutes");
//        mPricelistList.add("Paczka powyżej 5kg");
        //mPricelistList.add("Package over 5 kg");
        return mPricelistList;
    }
    private List<String> createPrices() {
        List<String> mPricelistPrices = new ArrayList<>();
//        mPricelistPrices.add("8 PLN");
        //mPricelistPrices.add("847.06 Naira");
//        mPricelistPrices.add("12 PLN");
        mPricelistPrices.add("800 Naira");
//        mPricelistPrices.add("16 PLN");
        mPricelistPrices.add("1,000 Naira");
//        mPricelistPrices.add("20 PLN");
        mPricelistPrices.add("1,500 Naira");
//        mPricelistPrices.add("23 PLN");
        //mPricelistPrices.add("2435.24 Naira");
//        mPricelistPrices.add("30 PLN");
        //mPricelistPrices.add("3176.09 Naira");
//        mPricelistPrices.add("45 PLN");
        //mPricelistPrices.add("4764.14 Naira");
//        mPricelistPrices.add("+50%% do ceny standardowej");
        //mPricelistPrices.add("+20%% to the standard price");
        mPricelistPrices.add("standard price in km");
        mPricelistPrices.add("+20% to the standard price in km");
//        mPricelistPrices.add("+130%% do ceny standardowej");
        mPricelistPrices.add("+200% to the standard price in km");
//        mPricelistPrices.add("+100%% do zamówionej usługi");
        //mPricelistPrices.add("+100%% to the ordered service");
//        mPricelistPrices.add("20 PLN + 1,20PLN za kilometr (w obie strony)");
        //mPricelistPrices.add("2,117.60 Naira + 126.98 Naira per kilometer (both ways)");
//        mPricelistPrices.add("1 PLN");
        //mPricelistPrices.add("105.84 Naira");
//        mPricelistPrices.add("zamówiona usługa + opłata PKS/PKP");
        //mPricelistPrices.add("ordered service + PKS / PKP fee");
//        mPricelistPrices.add("5 PLN za każde 10 minut");
        //mPricelistPrices.add("529.19 Naira for every 10 minutes");
       // mPricelistPrices.add("+0,70/kg");
        return mPricelistPrices;
    }

}
