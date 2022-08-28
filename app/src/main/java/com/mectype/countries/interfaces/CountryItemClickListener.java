package com.mectype.countries.interfaces;

import android.widget.ImageView;

import com.mectype.countries.model.Country;

public interface CountryItemClickListener {
    void onCountryItemClicked(Country country, ImageView flag);
}
