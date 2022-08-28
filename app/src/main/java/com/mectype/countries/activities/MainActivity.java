package com.mectype.countries.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mectype.countries.adapter.CountryAdapter;
import com.mectype.countries.databinding.MainAcitivityLayoutBinding;
import com.mectype.countries.interfaces.CountryItemClickListener;
import com.mectype.countries.model.Country;
import com.mectype.countries.viewmodel.CountryViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity implements CountryItemClickListener {

    final static String COUNTRY_KEY = "country";
    final static String FLAG_BITMAP_KEY = "flag_bitmap";
    MainAcitivityLayoutBinding binding;
    final static String IMAGES_FOLDER = "images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainAcitivityLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CountryViewModel model = new ViewModelProvider(this).get(CountryViewModel.class);
        model.init();
        model.setLastCountry(getLastCountry());
        File imagesDir = new File(getCacheDir(), IMAGES_FOLDER);
        if (!imagesDir.exists()) {
            if (!imagesDir.mkdir()) {
                Log.e("MainActivity", "Falied to create folder for images");
                System.exit(1);
            }
        }

        model.getCountries().observe(this, countries -> {
            binding.progressLayout.setVisibility(View.GONE);
            if (countries.size() != 0) {
                binding.recyclerView.setAdapter(new CountryAdapter(imagesDir, countries, this));
                binding.recyclerView.addItemDecoration(new DividerItemDecoration(binding.recyclerView.getContext(), LinearLayout.VERTICAL));
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            } else { // No internet connection, and no saved data (should be first run without internet)
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onCountryItemClicked(Country country, ImageView flag) {
        JSONObject jsonObject = country.toJson();
        saveCountryForOfflineAccess(jsonObject, ((BitmapDrawable)(flag.getDrawable())).getBitmap());
        Intent intent = new Intent(this, ExtraDetails.class);
        intent.putExtra("country", jsonObject.toString());
        startActivity(intent);
    }

    /***
     * I treat last clicked\selected country, and the last country to save (for offline access).
     */
    @SuppressWarnings("SpellCheckingInspection")
    private void saveCountryForOfflineAccess(JSONObject countryJsonStr, Bitmap flag) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(COUNTRY_KEY, countryJsonStr.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        flag.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String flagBitmapStr = Base64.encodeToString(b, Base64.DEFAULT);
        editor.putString(FLAG_BITMAP_KEY, flagBitmapStr);
        editor.apply();
    }


    private Country getLastCountry() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String countryKey = sharedPreferences.getString(COUNTRY_KEY, null);
        Country country = null;
        if (null != countryKey) {
            try {
                JSONObject obj = new JSONObject(countryKey);
                Bitmap bFlag = null;
                String bitmapStr = sharedPreferences.getString(FLAG_BITMAP_KEY, null);
                if (null != bitmapStr) {
                    byte[] imageAsBytes = Base64.decode(bitmapStr.getBytes(), Base64.DEFAULT);
                    bFlag = BitmapFactory.decodeByteArray(imageAsBytes, 0 , imageAsBytes.length);
                }
                country = new Country(obj);
                country.setFlagBitmap(bFlag);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return country;
    }
}