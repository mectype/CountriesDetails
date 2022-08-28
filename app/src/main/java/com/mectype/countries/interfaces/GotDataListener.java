package com.mectype.countries.interfaces;

import com.mectype.countries.model.Country;

import java.util.List;

public interface GotDataListener {
    void gotData(List<Country> data);
}
