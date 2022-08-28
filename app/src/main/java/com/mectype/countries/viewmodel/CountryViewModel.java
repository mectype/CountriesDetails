package com.mectype.countries.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mectype.countries.model.Country;
import com.mectype.countries.repository.CountryRepository;
import com.mectype.countries.interfaces.GotDataListener;

import java.util.List;

public class CountryViewModel extends ViewModel implements GotDataListener {

    private CountryRepository repository;
    private MutableLiveData<List<Country>> countries;

    public void init() {
        if(null != countries)
            return;
        repository = CountryRepository.getInstance();
    }

    public LiveData<List<Country>> getCountries() {
        if (countries == null) {
            countries = new MutableLiveData<>();
            repository.loadCountries(this);
        }
        return countries;
    }

    @Override
    public void gotData(List<Country> data) {
            countries.setValue(data);
    }

    public void setLastCountry(Country lastCountry) {
        repository.setLastCountry(lastCountry);
    }
}