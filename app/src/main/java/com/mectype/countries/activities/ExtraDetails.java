package com.mectype.countries.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mectype.countries.databinding.ActivityExtraDetailsBinding;
import com.mectype.countries.model.Country;

import org.json.JSONException;
import org.json.JSONObject;


public class ExtraDetails extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.mectype.countries.databinding.ActivityExtraDetailsBinding binding = ActivityExtraDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String countryStr = getIntent().getStringExtra(MainActivity.COUNTRY_KEY);
        JSONObject countryObj = null;
        try {
            countryObj = new JSONObject(countryStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (null != countryObj) {
            Country country = new Country(countryObj);
            binding.countryName.setText(country.getName());
            binding.region.setText(country.getRegion());
            binding.subRegion.setText(country.getSubRegion());
            binding.languages.setText(String.join(", ", country.getLanguages()));
            WebView wbb = binding.map;
            WebSettings webSettings = wbb.getSettings();
            wbb.setWebViewClient(new MapWebViewClient());
            webSettings.setJavaScriptEnabled(true);
            wbb.loadUrl(country.getMap());

        }

        binding.back.setOnClickListener((View v) -> onBackPressed());
    }

    public class MapWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            view.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Sorry, no map to display", Toast.LENGTH_LONG).show();
        }
    }
}