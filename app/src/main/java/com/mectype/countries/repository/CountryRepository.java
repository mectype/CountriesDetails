package com.mectype.countries.repository;

import android.os.Handler;
import android.os.Looper;

import com.mectype.countries.interfaces.GotDataListener;
import com.mectype.countries.model.Country;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class CountryRepository {

    private final static String address = "https://restcountries.com/v3.1/all";

    private static CountryRepository instance;

    private final ArrayList<Country> countries = new ArrayList<>();
    private Country lastCountry;

    public static CountryRepository getInstance() {
        if(instance == null) {
            instance = new CountryRepository();
        }
        return instance;
    }

    public void loadCountries(GotDataListener listener) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(()-> {
            HttpURLConnection connection = null;
            BufferedReader in = null;
            try {
                URL url = new URL(address);
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    String dataStr = sb.toString();
                    JSONArray jsonArray = new JSONArray(dataStr);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.optJSONObject(i);
                        Country country = new Country(obj);
                        countries.add(country);
                    }
                } else { // If we have internet problems, or in offline, add last saved/queried country
                        countries.add(lastCountry);
                }
            } catch (Exception e) { // No internet connection
                e.printStackTrace();
                countries.clear();
                if (null != lastCountry)
                    countries.add(lastCountry);
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != connection) connection.disconnect();
            }
            handler.post(() -> listener.gotData(countries));
        });
    }

    public void setLastCountry(Country lastCountry) {
        this.lastCountry = lastCountry;
    }
}
